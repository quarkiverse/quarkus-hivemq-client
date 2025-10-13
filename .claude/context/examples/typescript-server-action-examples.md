# NextBase Server Action Examples

## Basic Authenticated Server Action
```typescript
// ✅ actions/workspace-actions.ts
'use server';
import { z } from 'zod';
import { authenticatedAction } from '@/lib/safe-action';
import { createSupabaseUserServerActionClient } from '@/supabase-clients/user/createSupabaseUserServerActionClient';
import { revalidatePath } from 'next/cache';

const createWorkspaceSchema = z.object({
  name: z.string().min(1, 'Name is required').max(50, 'Name too long'),
  slug: z.string()
    .min(3, 'Slug must be at least 3 characters')
    .regex(/^[a-z0-9-]+$/, 'Only lowercase letters, numbers, and hyphens allowed'),
  description: z.string().optional()
});

export const createWorkspaceAction = authenticatedAction
  .schema(createWorkspaceSchema)
  .action(async ({ parsedInput, ctx }) => {
    const { user } = ctx;
    const supabase = createSupabaseUserServerActionClient();
    
    // Check if slug is already taken
    const { data: existingWorkspace } = await supabase
      .from('workspaces')
      .select('id')
      .eq('slug', parsedInput.slug)
      .single();
      
    if (existingWorkspace) {
      throw new Error('Workspace slug is already taken');
    }
    
    // Create workspace
    const { data: workspace, error } = await supabase
      .from('workspaces')
      .insert({
        name: parsedInput.name,
        slug: parsedInput.slug,
        description: parsedInput.description,
        created_by: user.id
      })
      .select()
      .single();
      
    if (error) {
      throw new Error(`Failed to create workspace: ${error.message}`);
    }
    
    // Add user as owner
    const { error: memberError } = await supabase
      .from('workspace_members')
      .insert({
        workspace_id: workspace.id,
        user_id: user.id,
        role: 'owner'
      });
      
    if (memberError) {
      throw new Error(`Failed to add workspace member: ${memberError.message}`);
    }
    
    // Revalidate relevant pages
    revalidatePath('/dashboard');
    revalidatePath('/workspaces');
    
    return workspace;
  });
```

## Server Action with Permission Checks
```typescript
// ✅ actions/project-actions.ts
'use server';
import { z } from 'zod';
import { authenticatedAction } from '@/lib/safe-action';
import { createSupabaseUserServerActionClient } from '@/supabase-clients/user/createSupabaseUserServerActionClient';
import { revalidatePath } from 'next/cache';

const createProjectSchema = z.object({
  name: z.string().min(1, 'Project name is required'),
  description: z.string().optional(),
  workspaceId: z.string().uuid('Invalid workspace ID'),
  status: z.enum(['draft', 'active', 'completed']).default('draft')
});

export const createProjectAction = authenticatedAction
  .schema(createProjectSchema)
  .action(async ({ parsedInput, ctx }) => {
    const { user } = ctx;
    const supabase = createSupabaseUserServerActionClient();
    
    // Check user has permission to create projects in this workspace
    const { data: membership, error: membershipError } = await supabase
      .from('workspace_members')
      .select('role')
      .eq('workspace_id', parsedInput.workspaceId)
      .eq('user_id', user.id)
      .eq('deleted_at', null)
      .single();
      
    if (membershipError || !membership) {
      throw new Error('You do not have access to this workspace');
    }
    
    // Only owners and admins can create projects
    if (!['owner', 'admin'].includes(membership.role)) {
      throw new Error('Insufficient permissions to create projects');
    }
    
    // Create project
    const { data: project, error } = await supabase
      .from('projects')
      .insert({
        name: parsedInput.name,
        description: parsedInput.description,
        workspace_id: parsedInput.workspaceId,
        status: parsedInput.status,
        created_by: user.id
      })
      .select(`
        *,
        workspace:workspaces(name, slug),
        creator:profiles!projects_created_by_fkey(full_name, email)
      `)
      .single();
      
    if (error) {
      throw new Error(`Failed to create project: ${error.message}`);
    }
    
    // Revalidate workspace pages
    revalidatePath(`/workspace/${project.workspace.slug}`);
    revalidatePath(`/workspace/${project.workspace.slug}/projects`);
    
    return project;
  });
```

## Update Action with Optimistic Updates
```typescript
// ✅ actions/project-actions.ts
const updateProjectSchema = z.object({
  id: z.string().uuid(),
  name: z.string().min(1).optional(),
  description: z.string().optional(),
  status: z.enum(['draft', 'active', 'completed', 'archived']).optional()
});

export const updateProjectAction = authenticatedAction
  .schema(updateProjectSchema)
  .action(async ({ parsedInput, ctx }) => {
    const { user } = ctx;
    const supabase = createSupabaseUserServerActionClient();
    
    // Get project and verify permissions
    const { data: project, error: projectError } = await supabase
      .from('projects')
      .select(`
        *,
        workspace:workspaces!inner(slug),
        workspace_members!inner(role)
      `)
      .eq('id', parsedInput.id)
      .eq('workspace_members.user_id', user.id)
      .eq('workspace_members.deleted_at', null)
      .single();
      
    if (projectError || !project) {
      throw new Error('Project not found or access denied');
    }
    
    // Check permissions
    const canEdit = ['owner', 'admin'].includes(project.workspace_members.role) || 
                   project.created_by === user.id;
                   
    if (!canEdit) {
      throw new Error('Insufficient permissions to edit this project');
    }
    
    // Update project
    const updateData = Object.fromEntries(
      Object.entries(parsedInput).filter(([key, value]) => 
        key !== 'id' && value !== undefined
      )
    );
    
    const { data: updatedProject, error } = await supabase
      .from('projects')
      .update({
        ...updateData,
        updated_at: new Date().toISOString()
      })
      .eq('id', parsedInput.id)
      .select()
      .single();
      
    if (error) {
      throw new Error(`Failed to update project: ${error.message}`);
    }
    
    // Revalidate pages
    revalidatePath(`/workspace/${project.workspace.slug}`);
    revalidatePath(`/workspace/${project.workspace.slug}/projects`);
    revalidatePath(`/project/${project.id}`);
    
    return updatedProject;
  });
```

## Delete Action with Soft Delete
```typescript
// ✅ actions/workspace-actions.ts
const deleteWorkspaceSchema = z.object({
  id: z.string().uuid(),
  confirmationText: z.string().refine(
    (val) => val === 'DELETE',
    'You must type DELETE to confirm'
  )
});

export const deleteWorkspaceAction = authenticatedAction
  .schema(deleteWorkspaceSchema)
  .action(async ({ parsedInput, ctx }) => {
    const { user } = ctx;
    const supabase = createSupabaseUserServerActionClient();
    
    // Verify user is workspace owner
    const { data: membership, error: membershipError } = await supabase
      .from('workspace_members')
      .select('role')
      .eq('workspace_id', parsedInput.id)
      .eq('user_id', user.id)
      .eq('deleted_at', null)
      .single();
      
    if (membershipError || membership?.role !== 'owner') {
      throw new Error('Only workspace owners can delete workspaces');
    }
    
    // Check for active projects
    const { count: activeProjects } = await supabase
      .from('projects')
      .select('*', { count: 'exact', head: true })
      .eq('workspace_id', parsedInput.id)
      .eq('status', 'active');
      
    if (activeProjects && activeProjects > 0) {
      throw new Error('Cannot delete workspace with active projects. Please complete or archive them first.');
    }
    
    // Soft delete workspace
    const { error } = await supabase
      .from('workspaces')
      .update({
        deleted_at: new Date().toISOString(),
        updated_at: new Date().toISOString()
      })
      .eq('id', parsedInput.id);
      
    if (error) {
      throw new Error(`Failed to delete workspace: ${error.message}`);
    }
    
    // Revalidate pages
    revalidatePath('/dashboard');
    revalidatePath('/workspaces');
    
    return { success: true };
  });
```

## Bulk Action Example
```typescript
// ✅ actions/project-actions.ts
const updateMultipleProjectsSchema = z.object({
  projectIds: z.array(z.string().uuid()).min(1, 'At least one project required'),
  updates: z.object({
    status: z.enum(['draft', 'active', 'completed', 'archived']).optional(),
    assignee_id: z.string().uuid().optional()
  }).refine(data => Object.keys(data).length > 0, 'At least one update field required')
});

export const updateMultipleProjectsAction = authenticatedAction
  .schema(updateMultipleProjectsSchema)
  .action(async ({ parsedInput, ctx }) => {
    const { user } = ctx;
    const supabase = createSupabaseUserServerActionClient();
    
    // Verify user has permission for all projects
    const { data: projects, error: projectsError } = await supabase
      .from('projects')
      .select(`
        id,
        workspace_id,
        workspace_members!inner(role)
      `)
      .in('id', parsedInput.projectIds)
      .eq('workspace_members.user_id', user.id)
      .eq('workspace_members.deleted_at', null);
      
    if (projectsError || !projects || projects.length !== parsedInput.projectIds.length) {
      throw new Error('Some projects not found or access denied');
    }
    
    // Check permissions for each project
    const unauthorized = projects.filter(p => 
      !['owner', 'admin'].includes(p.workspace_members.role)
    );
    
    if (unauthorized.length > 0) {
      throw new Error('Insufficient permissions for some projects');
    }
    
    // Perform bulk update
    const { data: updatedProjects, error } = await supabase
      .from('projects')
      .update({
        ...parsedInput.updates,
        updated_at: new Date().toISOString()
      })
      .in('id', parsedInput.projectIds)
      .select();
      
    if (error) {
      throw new Error(`Failed to update projects: ${error.message}`);
    }
    
    // Revalidate affected workspaces
    const workspaceIds = [...new Set(projects.map(p => p.workspace_id))];
    for (const workspaceId of workspaceIds) {
      revalidatePath(`/workspace/${workspaceId}`);
      revalidatePath(`/workspace/${workspaceId}/projects`);
    }
    
    return updatedProjects;
  });
```

## File Upload Action
```typescript
// ✅ actions/upload-actions.ts
'use server';
import { z } from 'zod';
import { authenticatedAction } from '@/lib/safe-action';
import { createSupabaseUserServerActionClient } from '@/supabase-clients/user/createSupabaseUserServerActionClient';

const uploadAvatarSchema = z.object({
  file: z.instanceof(FormData)
});

export const uploadAvatarAction = authenticatedAction
  .schema(uploadAvatarSchema)
  .action(async ({ parsedInput, ctx }) => {
    const { user } = ctx;
    const supabase = createSupabaseUserServerActionClient();
    
    const file = parsedInput.file.get('avatar') as File;
    
    if (!file) {
      throw new Error('No file provided');
    }
    
    // Validate file type
    if (!file.type.startsWith('image/')) {
      throw new Error('File must be an image');
    }
    
    // Validate file size (max 5MB)
    if (file.size > 5 * 1024 * 1024) {
      throw new Error('File size must be less than 5MB');
    }
    
    // Generate unique filename
    const fileExt = file.name.split('.').pop();
    const fileName = `${user.id}/${Date.now()}.${fileExt}`;
    
    // Upload to Supabase Storage
    const { data: uploadData, error: uploadError } = await supabase.storage
      .from('avatars')
      .upload(fileName, file, {
        cacheControl: '3600',
        upsert: false
      });
      
    if (uploadError) {
      throw new Error(`Failed to upload file: ${uploadError.message}`);
    }
    
    // Get public URL
    const { data: urlData } = supabase.storage
      .from('avatars')
      .getPublicUrl(uploadData.path);
      
    // Update user profile
    const { error: updateError } = await supabase
      .from('profiles')
      .update({ avatar_url: urlData.publicUrl })
      .eq('id', user.id);
      
    if (updateError) {
      throw new Error(`Failed to update profile: ${updateError.message}`);
    }
    
    revalidatePath('/user/settings');
    
    return { avatarUrl: urlData.publicUrl };
  });
```

## Transaction Example
```typescript
// ✅ actions/billing-actions.ts
const createSubscriptionSchema = z.object({
  workspaceId: z.string().uuid(),
  priceId: z.string(),
  paymentMethodId: z.string()
});

export const createSubscriptionAction = authenticatedAction
  .schema(createSubscriptionSchema)
  .action(async ({ parsedInput, ctx }) => {
    const { user } = ctx;
    const supabase = createSupabaseUserServerActionClient();
    
    // Start transaction
    const { data, error } = await supabase.rpc('create_subscription_transaction', {
      p_workspace_id: parsedInput.workspaceId,
      p_user_id: user.id,
      p_price_id: parsedInput.priceId,
      p_payment_method_id: parsedInput.paymentMethodId
    });
    
    if (error) {
      throw new Error(`Failed to create subscription: ${error.message}`);
    }
    
    // Process payment with Stripe
    try {
      const stripe = require('stripe')(process.env.STRIPE_SECRET_KEY);
      
      const subscription = await stripe.subscriptions.create({
        customer: data.customer_id,
        items: [{ price: parsedInput.priceId }],
        default_payment_method: parsedInput.paymentMethodId,
        metadata: {
          workspace_id: parsedInput.workspaceId,
          user_id: user.id
        }
      });
      
      // Update subscription with Stripe ID
      await supabase
        .from('subscriptions')
        .update({ stripe_subscription_id: subscription.id })
        .eq('id', data.subscription_id);
        
      revalidatePath(`/workspace/${parsedInput.workspaceId}/billing`);
      
      return { subscriptionId: subscription.id };
      
    } catch (stripeError) {
      // Rollback transaction
      await supabase.rpc('rollback_subscription_transaction', {
        p_subscription_id: data.subscription_id
      });
      
      throw new Error(`Payment failed: ${stripeError.message}`);
    }
  });
```