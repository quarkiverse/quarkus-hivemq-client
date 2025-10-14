# NextBase v3 AI Coding Reference

## About NextBase v3
NextBase v3 is a production-ready SaaS starter kit that provides pre-built infrastructure for multi-tenant applications. It includes authentication, workspace management, billing integration, and content systems out of the box. The codebase follows established patterns for data fetching, mutations, and security that should be maintained when adding new features.

## Tech Stack
- Next.js 15 (App Router)
- Supabase (PostgreSQL + Auth + Storage + Realtime)
- TypeScript (strict mode)
- Tailwind CSS + shadcn/ui
- React Hook Form + Zod
- Stripe (billing)
- React Email (templates)

## Project Structure

```
src/
├── app/                        # Next.js App Router
│   ├── (authenticated-pages)/  # Protected routes (middleware + layout check)
│   ├── (login-pages)/         # Auth routes
│   ├── (static-pages)/        # Marketing pages
│   └── api/                   # Route handlers
├── components/
│   ├── ui/                    # shadcn/ui base components
│   ├── form-components/       # Reusable form elements
│   └── [feature]/             # Feature-specific components
├── data/                      # Server Actions (mutations) - "use server"
├── rsc-data/                  # Server Component data fetching
├── supabase-clients/          # Client configurations
│   ├── server.ts             # createSupabaseUserServerComponentClient()
│   ├── action.ts             # createSupabaseUserServerActionClient()
│   └── middleware.ts         # createSupabaseUserMiddlewareClient()
└── utils/
    └── zod-schemas/          # Validation schemas (shared between client/server)
```

## Core Patterns

### Data Operations
```typescript
// READ: Server Components (rsc-data/)
async function getWorkspace(id: string) {
  const supabase = await createSupabaseUserServerComponentClient();
  const { data, error } = await supabase
    .from('workspaces')
    .select('*, members(*)')
    .eq('id', id)
    .single();
}

// WRITE: Server Actions (data/)
"use server";
export const updateWorkspaceAction = authActionClient
  .schema(workspaceSchema)
  .action(async ({ parsedInput, ctx: { userId } }) => {
    // Implementation
    revalidatePath('/workspaces');
  });
```

### Authentication Flow
1. **Middleware**: Cookie-based quick check → redirect if needed
2. **Layout**: Database session validation → thorough check
3. **RLS Policies**: Database-level access control

### Form Pattern
```typescript
// Component uses React Hook Form + Server Action
const form = useForm<SchemaType>({
  resolver: zodResolver(schema),
});

const { execute, status } = useAction(serverAction, {
  onSuccess: () => toast.success("Success"),
  onError: ({ error }) => toast.error(error.serverError)
});
```

## Database Schema

### Naming Conventions
- Tables: `plural_snake_case`, domain-prefixed (`user_*`, `workspace_*`)
- Primary keys: `id` (UUID with uuid_generate_v4())
- Foreign keys: `*_id` suffix
- Timestamps: `created_at`, `updated_at` (timestamptz)
- Booleans: `is_*` prefix
- JSON: `*_data` or `*_settings` suffix (JSONB type)

### Core Tables

#### User System
- `user_profiles`: Public profile data
- `user_settings`: Private preferences (default_workspace)
- `user_roles`: App-level roles (enum: 'admin', 'user')
- `user_notifications`: In-app notifications (payload: jsonb)
- `user_api_keys`: API key management (Ultimate only)

#### Workspace System
- `workspaces`: Core workspace data (slug unique)
- `workspace_members`: Membership with roles
- `workspace_invitations`: Pending invites
- `workspace_settings`: Member-visible settings (jsonb)
- `workspace_admin_settings`: Admin-only settings (jsonb)
- `workspace_credits`: Resource allocation

#### Projects
- `projects`: Workspace resources (status enum)
- `project_comments`: Threaded comments

#### Billing (Stripe)
- `billing_customers`: Stripe customer mapping
- `billing_subscriptions`: Active subscriptions
- `billing_products`: Product catalog
- `billing_prices`: Pricing info
- `billing_invoices`: Invoice records

#### Marketing/Content
- `marketing_blog_posts`: Blog system (status, seo_data, json_content)
- `marketing_feedback_boards`: Feedback collection
- `marketing_feedback_threads`: User feedback
- `marketing_changelog`: Product updates
- `marketing_author_profiles`: Content authors

### RLS Patterns
```sql
-- All tables have RLS enabled
-- Utility functions available:
is_workspace_member(workspace_id)
is_workspace_admin(workspace_id)

-- Pattern: One policy per operation
CREATE POLICY "Users can view their workspaces" ON workspaces
  FOR SELECT TO authenticated
  USING (is_workspace_member(id));
```

### Enum Types
- `app_role`: 'admin' | 'user'
- `workspace_invitation_link_status`: 'pending' | 'accepted' | 'rejected' | 'expired'
- `project_status`: 'active' | 'archived' | 'deleted'
- `subscription_status`: 'active' | 'canceled' | 'incomplete' | 'past_due' | 'trialing'
- `marketing_blog_post_status`: 'draft' | 'published' | 'archived'
- `marketing_feedback_thread_type`: 'bug' | 'feature' | 'improvement'
- `marketing_feedback_reactions`: '👍' | '❤️' | '🎉' | '🚀' | '👎'

## Key Implementation Details

### Multi-Tenancy
- All resources scoped to workspaces
- RLS enforces isolation
- Workspace context available via `workspace_id`

### Server Action Pattern
```typescript
// Standard server action structure
export const actionName = authActionClient
  .schema(zodSchema)  // Input validation
  .action(async ({ parsedInput, ctx: { userId } }) => {
    const supabase = await createSupabaseUserServerActionClient();
    // Implementation
    revalidatePath('/affected-route');
    return result;
  });
```

### Email Templates
Location: `emails/` directory
- React Email components
- Sent via configured provider (Resend/SendGrid)

### File Uploads
- Use Supabase Storage
- Files organized by workspace: `${workspaceId}/assets/`

### Realtime Subscriptions
```typescript
const channel = supabase
  .channel('workspace-updates')
  .on('postgres_changes', {
    event: '*',
    schema: 'public',
    table: 'projects',
    filter: `workspace_id=eq.${workspaceId}`
  }, handleUpdate)
  .subscribe();
```

### Environment Variables
Required prefixes:
- `NEXT_PUBLIC_*`: Client-side accessible
- Others: Server-side only

Key variables:
- `NEXT_PUBLIC_SUPABASE_URL`
- `NEXT_PUBLIC_SUPABASE_ANON_KEY`
- `SUPABASE_SERVICE_ROLE_KEY`
- `STRIPE_SECRET_KEY`
- `NEXT_PUBLIC_STRIPE_PUBLISHABLE_KEY`

## Route Organization

```
app/
├── (authenticated-pages)/
│   ├── dashboard/
│   ├── workspaces/[id]/
│   └── settings/
├── (login-pages)/
│   ├── login/
│   └── register/
├── api/
│   ├── stripe/webhooks/
│   └── [domain]/route.ts
└── (static-pages)/
    └── blog/[slug]/
```

## Testing Patterns
- E2E: `/e2e/*.spec.ts` (Playwright)
- Unit: `*.test.ts` files (Vitest)
- Test database operations use transaction rollback

## Error Handling
- Server Actions: Return `{ error: message }` or throw
- API Routes: Return appropriate HTTP status codes
- Forms: Display via `toast.error()` or inline validation

## Feature Flags
- Stored in `app_settings` table
- Accessed via server components
- Cached for performance