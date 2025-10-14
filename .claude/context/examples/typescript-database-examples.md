# NextBase Database Examples

## Data Fetching Functions
```typescript
// ✅ data/user/workspaces.ts
import { createSupabaseUserServerComponentClient } from '@/supabase-clients/user/createSupabaseUserServerComponentClient';

export async function getUserWorkspaces(userId: string) {
  const supabase = createSupabaseUserServerComponentClient();
  
  return supabase
    .from('workspaces')
    .select(`
      id,
      name,
      slug,
      description,
      created_at,
      updated_at,
      workspace_members!inner (
        id,
        role,
        joined_at,
        user_id
      )
    `)
    .eq('workspace_members.user_id', userId)
    .eq('workspace_members.deleted_at', null)
    .order('created_at', { ascending: false });
}

export async function getWorkspaceById(workspaceId: string, userId: string) {
  const supabase = createSupabaseUserServerComponentClient();
  
  return supabase
    .from('workspaces')
    .select(`
      *,
      workspace_members!inner (
        id,
        role,
        joined_at,
        user:profiles (
          id,
          full_name,
          email,
          avatar_url
        )
      ),
      projects (
        id,
        name,
        status,
        created_at,
        updated_at,
        creator:profiles!projects_created_by_fkey (
          full_name,
          avatar_url
        )
      )
    `)
    .eq('id', workspaceId)
    .eq('workspace_members.user_id', userId)
    .eq('workspace_members.deleted_at', null)
    .single();
}

export async function getWorkspaceMembers(workspaceId: string) {
  const supabase = createSupabaseUserServerComponentClient();
  
  return supabase
    .from('workspace_members')
    .select(`
      id,
      role,
      joined_at,
      user:profiles (
        id,
        full_name,
        email,
        avatar_url
      )
    `)
    .eq('workspace_id', workspaceId)
    .eq('deleted_at', null)
    .order('joined_at', { ascending: true });
}
```

## Complex Queries with Aggregations
```typescript
// ✅ data/admin/analytics.ts
import { createSupabaseAdminClient } from '@/supabase-clients/admin/supabaseAdminClient';

export async function getWorkspaceAnalytics(workspaceId: string) {
  const supabase = createSupabaseAdminClient();
  
  // Get workspace stats
  const [
    { data: workspace },
    { data: projectStats },
    { data: memberActivity },
    { data: recentActivity }
  ] = await Promise.all([
    // Basic workspace info
    supabase
      .from('workspaces')
      .select(`
        id,
        name,
        created_at,
        workspace_members (count)
      `)
      .eq('id', workspaceId)
      .single(),
      
    // Project statistics
    supabase
      .from('projects')
      .select(`
        status,
        created_at,
        updated_at
      `)
      .eq('workspace_id', workspaceId),
      
    // Member activity over time
    supabase.rpc('get_member_activity', {
      p_workspace_id: workspaceId,
      p_days: 30
    }),
    
    // Recent activity feed
    supabase
      .from('activity_logs')
      .select(`
        id,
        action,
        entity_type,
        entity_id,
        created_at,
        user:profiles (
          full_name,
          avatar_url
        )
      `)
      .eq('workspace_id', workspaceId)
      .order('created_at', { ascending: false })
      .limit(20)
  ]);
  
  // Process project stats
  const projectStatsSummary = projectStats?.reduce((acc, project) => {
    acc[project.status] = (acc[project.status] || 0) + 1;
    return acc;
  }, {} as Record<string, number>) || {};
  
  return {
    workspace,
    projectStats: projectStatsSummary,
    memberActivity,
    recentActivity,
    totalProjects: projectStats?.length || 0,
    totalMembers: workspace?.workspace_members?.[0]?.count || 0
  };
}
```

## Database Functions (PostgreSQL)
```sql
-- ✅ Supabase function for complex workspace queries
CREATE OR REPLACE FUNCTION get_workspace_dashboard_data(p_workspace_id UUID, p_user_id UUID)
RETURNS JSON AS $$
DECLARE
    result JSON;
    user_role TEXT;
BEGIN
    -- Check user permission
    SELECT role INTO user_role
    FROM workspace_members
    WHERE workspace_id = p_workspace_id 
    AND user_id = p_user_id 
    AND deleted_at IS NULL;
    
    IF user_role IS NULL THEN
        RAISE EXCEPTION 'Access denied to workspace';
    END IF;
    
    -- Build dashboard data
    SELECT json_build_object(
        'workspace', (
            SELECT json_build_object(
                'id', w.id,
                'name', w.name,
                'slug', w.slug,
                'member_count', (
                    SELECT COUNT(*) 
                    FROM workspace_members wm 
                    WHERE wm.workspace_id = w.id 
                    AND wm.deleted_at IS NULL
                )
            )
            FROM workspaces w
            WHERE w.id = p_workspace_id
        ),
        'projects', (
            SELECT COALESCE(json_agg(
                json_build_object(
                    'id', p.id,
                    'name', p.name,
                    'status', p.status,
                    'created_at', p.created_at,
                    'creator', json_build_object(
                        'full_name', pr.full_name,
                        'avatar_url', pr.avatar_url
                    )
                )
            ), '[]'::json)
            FROM projects p
            LEFT JOIN profiles pr ON pr.id = p.created_by
            WHERE p.workspace_id = p_workspace_id
            ORDER BY p.created_at DESC
            LIMIT 10
        ),
        'recent_activity', (
            SELECT COALESCE(json_agg(
                json_build_object(
                    'action', al.action,
                    'entity_type', al.entity_type,
                    'created_at', al.created_at,
                    'user', json_build_object(
                        'full_name', pr.full_name,
                        'avatar_url', pr.avatar_url
                    )
                )
            ), '[]'::json)
            FROM activity_logs al
            LEFT JOIN profiles pr ON pr.id = al.user_id
            WHERE al.workspace_id = p_workspace_id
            ORDER BY al.created_at DESC
            LIMIT 20
        ),
        'user_role', user_role
    ) INTO result;
    
    RETURN result;
END;
$$ LANGUAGE plpgsql SECURITY DEFINER;
```

## RLS Policies Examples
```sql
-- ✅ Workspace access policies
CREATE POLICY "Users can view workspaces they're members of" ON workspaces
    FOR SELECT
    USING (
        id IN (
            SELECT workspace_id 
            FROM workspace_members 
            WHERE user_id = auth.uid() 
            AND deleted_at IS NULL
        )
    );

CREATE POLICY "Users can update workspaces where they're owners/admins" ON workspaces
    FOR UPDATE
    USING (
        id IN (
            SELECT workspace_id 
            FROM workspace_members 
            WHERE user_id = auth.uid() 
            AND role IN ('owner', 'admin')
            AND deleted_at IS NULL
        )
    );

-- ✅ Project access policies
CREATE POLICY "Users can view projects in their workspaces" ON projects
    FOR SELECT
    USING (
        workspace_id IN (
            SELECT workspace_id 
            FROM workspace_members 
            WHERE user_id = auth.uid() 
            AND deleted_at IS NULL
        )
    );

CREATE POLICY "Users can create projects in workspaces where they have admin+ role" ON projects
    FOR INSERT
    WITH CHECK (
        workspace_id IN (
            SELECT workspace_id 
            FROM workspace_members 
            WHERE user_id = auth.uid() 
            AND role IN ('owner', 'admin')
            AND deleted_at IS NULL
        )
    );

-- ✅ Admin-only policies
CREATE POLICY "Application admins can manage all workspaces" ON workspaces
    FOR ALL
    USING (
        EXISTS (
            SELECT 1 FROM profiles 
            WHERE id = auth.uid() 
            AND is_application_admin = true
        )
    );
```

## Migration Examples
```sql
-- ✅ Create workspace features table
CREATE TABLE workspace_features (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    workspace_id UUID NOT NULL REFERENCES workspaces(id) ON DELETE CASCADE,
    feature_name TEXT NOT NULL CHECK (length(feature_name) > 0),
    enabled BOOLEAN DEFAULT false,
    config JSONB DEFAULT '{}'::jsonb,
    created_at TIMESTAMPTZ DEFAULT NOW(),
    updated_at TIMESTAMPTZ DEFAULT NOW(),
    
    UNIQUE(workspace_id, feature_name)
);

-- Enable RLS
ALTER TABLE workspace_features ENABLE ROW LEVEL SECURITY;

-- Create policies
CREATE POLICY "Users can view workspace features" ON workspace_features
    FOR SELECT
    USING (
        workspace_id IN (
            SELECT workspace_id 
            FROM workspace_members 
            WHERE user_id = auth.uid()
            AND deleted_at IS NULL
        )
    );

-- Create indexes
CREATE INDEX idx_workspace_features_workspace_id ON workspace_features(workspace_id);
CREATE INDEX idx_workspace_features_enabled ON workspace_features(enabled) WHERE enabled = true;

-- Create updated_at trigger
CREATE TRIGGER set_workspace_features_updated_at
    BEFORE UPDATE ON workspace_features
    FOR EACH ROW
    EXECUTE FUNCTION set_updated_at();
```

## Trigger Examples
```sql
-- ✅ Audit trigger for workspace changes
CREATE OR REPLACE FUNCTION audit_workspace_changes()
RETURNS TRIGGER AS $$
BEGIN
    IF TG_OP = 'UPDATE' THEN
        INSERT INTO activity_logs (
            workspace_id,
            user_id,
            action,
            entity_type,
            entity_id,
            old_values,
            new_values,
            created_at
        ) VALUES (
            NEW.id,
            auth.uid(),
            'workspace_updated',
            'workspace',
            NEW.id,
            to_jsonb(OLD),
            to_jsonb(NEW),
            NOW()
        );
        RETURN NEW;
    ELSIF TG_OP = 'DELETE' THEN
        INSERT INTO activity_logs (
            workspace_id,
            user_id,
            action,
            entity_type,
            entity_id,
            old_values,
            created_at
        ) VALUES (
            OLD.id,
            auth.uid(),
            'workspace_deleted',
            'workspace',
            OLD.id,
            to_jsonb(OLD),
            NOW()
        );
        RETURN OLD;
    END IF;
    RETURN NULL;
END;
$$ LANGUAGE plpgsql SECURITY DEFINER;

CREATE TRIGGER workspace_audit_trigger
    AFTER UPDATE OR DELETE ON workspaces
    FOR EACH ROW
    EXECUTE FUNCTION audit_workspace_changes();
```

## Real-time Subscriptions
```typescript
// ✅ Real-time project updates
'use client';
import { useEffect, useState } from 'react';
import { supabaseUserClientComponent } from '@/supabase-clients/user/supabaseUserClientComponent';

export function useRealtimeProjects(workspaceId: string) {
  const [projects, setProjects] = useState<Project[]>([]);
  
  useEffect(() => {
    // Initial fetch
    const fetchProjects = async () => {
      const { data } = await supabaseUserClientComponent
        .from('projects')
        .select(`
          *,
          creator:profiles!projects_created_by_fkey(full_name, avatar_url)
        `)
        .eq('workspace_id', workspaceId)
        .order('created_at', { ascending: false });
        
      if (data) setProjects(data);
    };
    
    fetchProjects();
    
    // Set up real-time subscription
    const channel = supabaseUserClientComponent
      .channel(`projects:workspace_id=eq.${workspaceId}`)
      .on(
        'postgres_changes',
        {
          event: '*',
          schema: 'public',
          table: 'projects',
          filter: `workspace_id=eq.${workspaceId}`
        },
        (payload) => {
          if (payload.eventType === 'INSERT') {
            setProjects(prev => [payload.new as Project, ...prev]);
          } else if (payload.eventType === 'UPDATE') {
            setProjects(prev => 
              prev.map(p => p.id === payload.new.id ? { ...p, ...payload.new } : p)
            );
          } else if (payload.eventType === 'DELETE') {
            setProjects(prev => prev.filter(p => p.id !== payload.old.id));
          }
        }
      )
      .subscribe();
      
    return () => {
      supabaseUserClientComponent.removeChannel(channel);
    };
  }, [workspaceId]);
  
  return projects;
}
```

## Database Error Handling
```typescript
// ✅ Centralized database error handling
export class DatabaseError extends Error {
  constructor(
    message: string,
    public code?: string,
    public details?: string,
    public hint?: string
  ) {
    super(message);
    this.name = 'DatabaseError';
  }
}

export function handleDatabaseError(error: any): never {
  console.error('Database error:', error);
  
  // Handle specific PostgreSQL errors
  switch (error.code) {
    case '23505': // unique_violation
      if (error.details?.includes('workspaces_slug_key')) {
        throw new DatabaseError('Workspace slug is already taken');
      }
      throw new DatabaseError('This value already exists');
      
    case '23503': // foreign_key_violation
      throw new DatabaseError('Referenced record does not exist');
      
    case '42501': // insufficient_privilege
      throw new DatabaseError('Access denied');
      
    case 'PGRST116': // Row level security violation
      throw new DatabaseError('Access denied to this resource');
      
    default:
      throw new DatabaseError(
        error.message || 'An unexpected database error occurred',
        error.code,
        error.details,
        error.hint
      );
  }
}
```

## Performance Optimization
```typescript
// ✅ Optimized queries with proper indexing
export async function getWorkspaceProjectsOptimized(
  workspaceId: string, 
  userId: string,
  options: {
    limit?: number;
    offset?: number;
    status?: string;
    search?: string;
  } = {}
) {
  const supabase = createSupabaseUserServerComponentClient();
  
  let query = supabase
    .from('projects')
    .select(`
      id,
      name,
      status,
      created_at,
      updated_at,
      creator:profiles!projects_created_by_fkey(full_name, avatar_url)
    `, { count: 'exact' })
    .eq('workspace_id', workspaceId);
  
  // Apply filters
  if (options.status) {
    query = query.eq('status', options.status);
  }
  
  if (options.search) {
    query = query.or(`name.ilike.%${options.search}%, description.ilike.%${options.search}%`);
  }
  
  // Apply pagination
  if (options.limit) {
    query = query.limit(options.limit);
  }
  
  if (options.offset) {
    query = query.range(options.offset, options.offset + (options.limit || 10) - 1);
  }
  
  // Order by most recent first (indexed)
  query = query.order('created_at', { ascending: false });
  
  return query;
}
```