---
description: Supabase integration, PostgreSQL patterns, and database best practices
alwaysApply: true
---

# Supabase & Database Patterns

Consolidated from: supabase-react-coding-standards.mdc, supabase-javascript-coding-standards.mdc, postgres-sql-style-guide.mdc, db-migrations.mdc, create-rls-policies.mdc, create-db-functions.mdc, writing-supabase-edge-functions.mdc

**Enhanced with NextBase-specific patterns and database optimization strategies**

## NextBase Supabase Client Selection

```typescript
// ✅ NextBase Server Components
import { createSupabaseUserServerComponentClient } from '@/supabase-clients/user/createSupabaseUserServerComponentClient';

export default async function Page() {
  const supabase = createSupabaseUserServerComponentClient();
  const { data } = await supabase.from('workspaces').select('*');
  return <WorkspaceList workspaces={data} />;
}

// ✅ NextBase Server Actions
import { createSupabaseUserServerActionClient } from '@/supabase-clients/user/createSupabaseUserServerActionClient';

export async function createWorkspace(formData: FormData) {
  'use server';
  const supabase = createSupabaseUserServerActionClient();
  const { data, error } = await supabase
    .from('workspaces')
    .insert({ name: formData.get('name') })
    .select()
    .single();
}

// ✅ NextBase Client Components  
import { supabaseUserClientComponent } from '@/supabase-clients/user/supabaseUserClientComponent';

export function RealtimeComponent() {
  const [data, setData] = useState([]);
  
  useEffect(() => {
    const channel = supabaseUserClientComponent
      .channel('workspaces')
      .on('postgres_changes', { event: '*', schema: 'public', table: 'workspaces' }, setData)
      .subscribe();
      
    return () => supabaseUserClientComponent.removeChannel(channel);
  }, []);
}

// ✅ NextBase Route Handlers
import { createSupabaseUserRouteHandlerClient } from '@/supabase-clients/user/createSupabaseUserRouteHandlerClient';

export async function GET(request: NextRequest) {
  const supabase = createSupabaseUserRouteHandlerClient();
  const { data: { user } } = await supabase.auth.getUser();
  // ...
}
```

## NextBase Data Layer Organization
```
data/
├── user/           # User-scoped data functions
│   ├── workspaces.ts
│   ├── projects.ts
│   └── billing.ts
├── admin/          # Admin-scoped data functions
│   ├── users.ts
│   └── analytics.ts
└── anon/           # Anonymous/public data
    ├── blog.ts
    └── pricing.ts
```

```typescript
// ✅ NextBase Data Layer Pattern - /data/user/workspaces.ts
import { createSupabaseUserServerComponentClient } from '@/supabase-clients/user/createSupabaseUserServerComponentClient';

export async function getUserWorkspaces(userId: string) {
  const supabase = createSupabaseUserServerComponentClient();
  
  return supabase
    .from('workspaces')
    .select(`
      *,
      workspace_members!inner (
        role,
        user_id
      )
    `)
    .eq('workspace_members.user_id', userId)
    .eq('workspace_members.deleted_at', null);
}

export async function getWorkspaceById(id: string) {
  const supabase = createSupabaseUserServerComponentClient();
  
  return supabase
    .from('workspaces')
    .select('*')
    .eq('id', id)
    .single();
}
```

## Generic Supabase Client Setup (For Reference)

### Server-Side Clients
```typescript
// utils/supabase/server.ts
import { createServerClient, type CookieOptions } from '@supabase/ssr';
import { cookies } from 'next/headers';

export function createClient() {
  const cookieStore = cookies();

  return createServerClient(
    process.env.NEXT_PUBLIC_SUPABASE_URL!,
    process.env.NEXT_PUBLIC_SUPABASE_ANON_KEY!,
    {
      cookies: {
        get(name: string) {
          return cookieStore.get(name)?.value;
        },
        set(name: string, value: string, options: CookieOptions) {
          try {
            cookieStore.set({ name, value, ...options });
          } catch (error) {
            // Handle cookie errors in Server Components
          }
        },
        remove(name: string, options: CookieOptions) {
          try {
            cookieStore.set({ name, value: '', ...options });
          } catch (error) {
            // Handle cookie errors
          }
        },
      },
    }
  );
}
```

### Client-Side Setup
```typescript
// utils/supabase/client.ts
import { createBrowserClient } from '@supabase/ssr';
import type { Database } from '@/types/database';

export function createClient() {
  return createBrowserClient<Database>(
    process.env.NEXT_PUBLIC_SUPABASE_URL!,
    process.env.NEXT_PUBLIC_SUPABASE_ANON_KEY!
  );
}
```

### Middleware Client
```typescript
// utils/supabase/middleware.ts
import { createServerClient, type CookieOptions } from '@supabase/ssr';
import { NextResponse, type NextRequest } from 'next/server';

export async function updateSession(request: NextRequest) {
  let response = NextResponse.next({
    request: {
      headers: request.headers,
    },
  });

  const supabase = createServerClient(
    process.env.NEXT_PUBLIC_SUPABASE_URL!,
    process.env.NEXT_PUBLIC_SUPABASE_ANON_KEY!,
    {
      cookies: {
        get(name: string) {
          return request.cookies.get(name)?.value;
        },
        set(name: string, value: string, options: CookieOptions) {
          request.cookies.set({ name, value, ...options });
          response = NextResponse.next({
            request: {
              headers: request.headers,
            },
          });
          response.cookies.set({ name, value, ...options });
        },
        remove(name: string, options: CookieOptions) {
          request.cookies.set({ name, value: '', ...options });
          response = NextResponse.next({
            request: {
              headers: request.headers,
            },
          });
          response.cookies.set({ name, value: '', ...options });
        },
      },
    }
  );

  await supabase.auth.getUser();
  return response;
}
```

## Authentication Patterns

### Sign Up Flow
```typescript
// actions/auth.ts
'use server';

import { createClient } from '@/utils/supabase/server';
import { redirect } from 'next/navigation';

export async function signUp(formData: FormData) {
  const supabase = createClient();
  
  const data = {
    email: formData.get('email') as string,
    password: formData.get('password') as string,
    options: {
      data: {
        full_name: formData.get('fullName') as string,
      },
    },
  };

  const { error } = await supabase.auth.signUp(data);

  if (error) {
    return { error: error.message };
  }

  // Send confirmation email
  redirect('/auth/confirm-email');
}
```

### Sign In with OAuth
```typescript
// components/oauth-button.tsx
'use client';

import { createClient } from '@/utils/supabase/client';
import { Button } from '@/components/ui/button';

type Provider = 'github' | 'google' | 'discord';

export function OAuthButton({ provider }: { provider: Provider }) {
  const handleOAuth = async () => {
    const supabase = createClient();
    
    const { error } = await supabase.auth.signInWithOAuth({
      provider,
      options: {
        redirectTo: `${location.origin}/auth/callback`,
        queryParams: {
          access_type: 'offline',
          prompt: 'consent',
        },
      },
    });

    if (error) {
      console.error('OAuth error:', error);
    }
  };

  return (
    <Button onClick={handleOAuth} variant="outline">
      Sign in with {provider}
    </Button>
  );
}
```

### Session Management
```typescript
// hooks/use-user.ts
import { useEffect, useState } from 'react';
import { createClient } from '@/utils/supabase/client';
import type { User } from '@supabase/supabase-js';

export function useUser() {
  const [user, setUser] = useState<User | null>(null);
  const [loading, setLoading] = useState(true);
  const supabase = createClient();

  useEffect(() => {
    // Get initial session
    supabase.auth.getSession().then(({ data: { session } }) => {
      setUser(session?.user ?? null);
      setLoading(false);
    });

    // Listen for auth changes
    const {
      data: { subscription },
    } = supabase.auth.onAuthStateChange((_event, session) => {
      setUser(session?.user ?? null);
    });

    return () => subscription.unsubscribe();
  }, [supabase]);

  return { user, loading };
}
```

## Database Queries

### Type-Safe Queries
```typescript
// lib/database.types.ts - Generated from Supabase
export type Database = {
  public: {
    Tables: {
      profiles: {
        Row: {
          id: string;
          username: string | null;
          full_name: string | null;
          avatar_url: string | null;
          updated_at: string;
        };
        Insert: {
          id: string;
          username?: string | null;
          full_name?: string | null;
          avatar_url?: string | null;
          updated_at?: string;
        };
        Update: {
          username?: string | null;
          full_name?: string | null;
          avatar_url?: string | null;
          updated_at?: string;
        };
      };
    };
  };
};

// queries/profiles.ts
import { createClient } from '@/utils/supabase/server';
import type { Database } from '@/lib/database.types';

type Profile = Database['public']['Tables']['profiles']['Row'];

export async function getProfile(userId: string): Promise<Profile | null> {
  const supabase = createClient();
  
  const { data, error } = await supabase
    .from('profiles')
    .select('*')
    .eq('id', userId)
    .single();

  if (error) {
    console.error('Error fetching profile:', error);
    return null;
  }

  return data;
}
```

### Complex Queries with Joins
```typescript
// Get posts with author information
export async function getPostsWithAuthors() {
  const supabase = createClient();
  
  const { data, error } = await supabase
    .from('posts')
    .select(`
      id,
      title,
      content,
      created_at,
      author:profiles!user_id (
        id,
        username,
        avatar_url
      ),
      categories:post_categories (
        category:categories (
          id,
          name,
          slug
        )
      )
    `)
    .eq('published', true)
    .order('created_at', { ascending: false })
    .limit(10);

  if (error) throw error;
  return data;
}
```

### Pagination Pattern
```typescript
interface PaginationParams {
  page: number;
  pageSize: number;
}

export async function getPaginatedPosts({ page, pageSize }: PaginationParams) {
  const supabase = createClient();
  const from = (page - 1) * pageSize;
  const to = from + pageSize - 1;

  const { data, error, count } = await supabase
    .from('posts')
    .select('*', { count: 'exact' })
    .range(from, to)
    .order('created_at', { ascending: false });

  if (error) throw error;

  return {
    posts: data || [],
    totalCount: count || 0,
    totalPages: Math.ceil((count || 0) / pageSize),
    currentPage: page,
  };
}
```

## Real-time Subscriptions

### Basic Real-time Subscription
```typescript
// hooks/use-realtime-posts.ts
import { useEffect, useState } from 'react';
import { createClient } from '@/utils/supabase/client';
import type { RealtimePostgresChangesPayload } from '@supabase/supabase-js';

export function useRealtimePosts() {
  const [posts, setPosts] = useState<Post[]>([]);
  const supabase = createClient();

  useEffect(() => {
    // Initial fetch
    const fetchPosts = async () => {
      const { data } = await supabase
        .from('posts')
        .select('*')
        .order('created_at', { ascending: false });
      
      if (data) setPosts(data);
    };

    fetchPosts();

    // Subscribe to changes
    const channel = supabase
      .channel('posts-changes')
      .on(
        'postgres_changes',
        {
          event: '*',
          schema: 'public',
          table: 'posts',
        },
        (payload: RealtimePostgresChangesPayload<Post>) => {
          if (payload.eventType === 'INSERT') {
            setPosts(current => [payload.new, ...current]);
          } else if (payload.eventType === 'UPDATE') {
            setPosts(current =>
              current.map(post =>
                post.id === payload.new.id ? payload.new : post
              )
            );
          } else if (payload.eventType === 'DELETE') {
            setPosts(current =>
              current.filter(post => post.id !== payload.old.id)
            );
          }
        }
      )
      .subscribe();

    return () => {
      supabase.removeChannel(channel);
    };
  }, [supabase]);

  return posts;
}
```

### Presence (Who's Online)
```typescript
// hooks/use-presence.ts
interface UserPresence {
  userId: string;
  username: string;
  avatarUrl?: string;
  onlineAt: string;
}

export function usePresence(roomId: string) {
  const [presences, setPresences] = useState<UserPresence[]>([]);
  const supabase = createClient();
  const { user } = useUser();

  useEffect(() => {
    if (!user) return;

    const channel = supabase.channel(`room:${roomId}`);

    channel
      .on('presence', { event: 'sync' }, () => {
        const state = channel.presenceState();
        const users = Object.values(state).flat() as UserPresence[];
        setPresences(users);
      })
      .subscribe(async (status) => {
        if (status === 'SUBSCRIBED') {
          await channel.track({
            userId: user.id,
            username: user.email,
            onlineAt: new Date().toISOString(),
          });
        }
      });

    return () => {
      channel.untrack();
      supabase.removeChannel(channel);
    };
  }, [supabase, roomId, user]);

  return presences;
}
```

## Database Schema Design

### Table Creation with Best Practices
```sql
-- Enable necessary extensions
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";
CREATE EXTENSION IF NOT EXISTS "pg_trgm"; -- For text search

-- Organizations table
CREATE TABLE organizations (
  id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
  name TEXT NOT NULL,
  slug TEXT UNIQUE NOT NULL,
  logo_url TEXT,
  settings JSONB DEFAULT '{}',
  created_at TIMESTAMPTZ DEFAULT NOW(),
  updated_at TIMESTAMPTZ DEFAULT NOW()
);

-- Create indexes for performance
CREATE INDEX idx_organizations_slug ON organizations(slug);
CREATE INDEX idx_organizations_created_at ON organizations(created_at DESC);

-- Add comments for documentation
COMMENT ON TABLE organizations IS 'Organizations that own workspaces';
COMMENT ON COLUMN organizations.settings IS 'JSON settings: {theme, features, limits}';
```

### Foreign Key Relationships
```sql
-- Projects table with proper foreign keys
CREATE TABLE projects (
  id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
  organization_id UUID NOT NULL REFERENCES organizations(id) ON DELETE CASCADE,
  created_by UUID NOT NULL REFERENCES auth.users(id) ON DELETE SET NULL,
  name TEXT NOT NULL,
  description TEXT,
  status TEXT CHECK (status IN ('active', 'archived', 'deleted')) DEFAULT 'active',
  metadata JSONB DEFAULT '{}',
  created_at TIMESTAMPTZ DEFAULT NOW(),
  updated_at TIMESTAMPTZ DEFAULT NOW(),
  
  -- Composite unique constraint
  UNIQUE(organization_id, name)
);

-- Indexes for foreign keys and common queries
CREATE INDEX idx_projects_organization_id ON projects(organization_id);
CREATE INDEX idx_projects_created_by ON projects(created_by);
CREATE INDEX idx_projects_status ON projects(status) WHERE status != 'deleted';
```

## Row Level Security (RLS)

### Basic RLS Policies
```sql
-- Enable RLS
ALTER TABLE organizations ENABLE ROW LEVEL SECURITY;

-- Policy: Users can view organizations they belong to
CREATE POLICY "Users can view their organizations"
  ON organizations
  FOR SELECT
  TO authenticated
  USING (
    EXISTS (
      SELECT 1 FROM organization_members
      WHERE organization_members.organization_id = organizations.id
      AND organization_members.user_id = auth.uid()
    )
  );

-- Policy: Only admins can update organizations
CREATE POLICY "Admins can update organizations"
  ON organizations
  FOR UPDATE
  TO authenticated
  USING (
    EXISTS (
      SELECT 1 FROM organization_members
      WHERE organization_members.organization_id = organizations.id
      AND organization_members.user_id = auth.uid()
      AND organization_members.role = 'admin'
    )
  );
```

### Advanced RLS with Functions
```sql
-- Function to check if user is org admin
CREATE OR REPLACE FUNCTION is_org_admin(org_id UUID)
RETURNS BOOLEAN AS $$
BEGIN
  RETURN EXISTS (
    SELECT 1 FROM organization_members
    WHERE organization_id = org_id
    AND user_id = auth.uid()
    AND role = 'admin'
  );
END;
$$ LANGUAGE plpgsql SECURITY DEFINER;

-- Use function in policy
CREATE POLICY "Admins can delete projects"
  ON projects
  FOR DELETE
  TO authenticated
  USING (is_org_admin(organization_id));
```

### RLS for Soft Deletes
```sql
-- Policy that excludes soft-deleted records by default
CREATE POLICY "View non-deleted projects"
  ON projects
  FOR SELECT
  TO authenticated
  USING (
    status != 'deleted'
    AND EXISTS (
      SELECT 1 FROM organization_members
      WHERE organization_members.organization_id = projects.organization_id
      AND organization_members.user_id = auth.uid()
    )
  );

-- Separate policy for admins to view deleted items
CREATE POLICY "Admins can view deleted projects"
  ON projects
  FOR SELECT
  TO authenticated
  USING (
    status = 'deleted'
    AND is_org_admin(organization_id)
  );
```

## Database Functions

### Trigger Functions
```sql
-- Auto-update updated_at timestamp
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
  NEW.updated_at = NOW();
  RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Apply trigger to tables
CREATE TRIGGER update_organizations_updated_at
  BEFORE UPDATE ON organizations
  FOR EACH ROW
  EXECUTE FUNCTION update_updated_at_column();
```

### Business Logic Functions
```sql
-- Function to create organization with initial setup
CREATE OR REPLACE FUNCTION create_organization_with_admin(
  org_name TEXT,
  org_slug TEXT,
  admin_user_id UUID DEFAULT auth.uid()
)
RETURNS UUID AS $$
DECLARE
  new_org_id UUID;
BEGIN
  -- Insert organization
  INSERT INTO organizations (name, slug)
  VALUES (org_name, org_slug)
  RETURNING id INTO new_org_id;
  
  -- Add creator as admin
  INSERT INTO organization_members (organization_id, user_id, role)
  VALUES (new_org_id, admin_user_id, 'admin');
  
  -- Create default workspace
  INSERT INTO workspaces (organization_id, name, is_default)
  VALUES (new_org_id, 'Default Workspace', true);
  
  RETURN new_org_id;
END;
$$ LANGUAGE plpgsql SECURITY DEFINER;

-- Grant execute permission
GRANT EXECUTE ON FUNCTION create_organization_with_admin TO authenticated;
```

### Aggregation Functions
```sql
-- Get organization statistics
CREATE OR REPLACE FUNCTION get_organization_stats(org_id UUID)
RETURNS TABLE (
  member_count BIGINT,
  project_count BIGINT,
  storage_used BIGINT,
  last_activity TIMESTAMPTZ
) AS $$
BEGIN
  RETURN QUERY
  SELECT
    (SELECT COUNT(*) FROM organization_members WHERE organization_id = org_id),
    (SELECT COUNT(*) FROM projects WHERE organization_id = org_id AND status = 'active'),
    (SELECT COALESCE(SUM(size), 0) FROM storage.objects WHERE bucket_id = 'org-files' AND name LIKE org_id || '/%'),
    (SELECT MAX(created_at) FROM activity_logs WHERE organization_id = org_id);
END;
$$ LANGUAGE plpgsql SECURITY DEFINER;
```

## Storage Patterns

### File Upload with Metadata
```typescript
// utils/storage.ts
export async function uploadFile(
  file: File,
  bucket: string,
  path: string
) {
  const supabase = createClient();
  
  // Upload file
  const { data, error } = await supabase.storage
    .from(bucket)
    .upload(path, file, {
      cacheControl: '3600',
      upsert: false,
    });

  if (error) throw error;

  // Store metadata in database
  const { error: dbError } = await supabase
    .from('file_metadata')
    .insert({
      bucket,
      path: data.path,
      name: file.name,
      size: file.size,
      mime_type: file.type,
      uploaded_by: (await supabase.auth.getUser()).data.user?.id,
    });

  if (dbError) {
    // Rollback file upload
    await supabase.storage.from(bucket).remove([data.path]);
    throw dbError;
  }

  return data;
}
```

### Signed URLs for Private Files
```typescript
export async function getSignedUrl(
  bucket: string,
  path: string,
  expiresIn = 3600
) {
  const supabase = createClient();
  
  const { data, error } = await supabase.storage
    .from(bucket)
    .createSignedUrl(path, expiresIn);

  if (error) throw error;
  
  return data.signedUrl;
}
```

## Edge Functions

### Basic Edge Function
```typescript
// supabase/functions/send-email/index.ts
import { serve } from 'https://deno.land/std@0.168.0/http/server.ts';
import { createClient } from 'https://esm.sh/@supabase/supabase-js@2';

const corsHeaders = {
  'Access-Control-Allow-Origin': '*',
  'Access-Control-Allow-Headers': 'authorization, x-client-info, apikey, content-type',
};

serve(async (req) => {
  // Handle CORS
  if (req.method === 'OPTIONS') {
    return new Response('ok', { headers: corsHeaders });
  }

  try {
    const supabaseClient = createClient(
      Deno.env.get('SUPABASE_URL') ?? '',
      Deno.env.get('SUPABASE_ANON_KEY') ?? '',
      {
        global: {
          headers: { Authorization: req.headers.get('Authorization')! },
        },
      }
    );

    // Verify user
    const {
      data: { user },
    } = await supabaseClient.auth.getUser();

    if (!user) {
      throw new Error('Unauthorized');
    }

    // Get request data
    const { to, subject, html } = await req.json();

    // Send email using Resend/SendGrid/etc
    const response = await fetch('https://api.resend.com/emails', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        Authorization: `Bearer ${Deno.env.get('RESEND_API_KEY')}`,
      },
      body: JSON.stringify({
        from: 'noreply@example.com',
        to,
        subject,
        html,
      }),
    });

    const data = await response.json();

    return new Response(JSON.stringify(data), {
      headers: { ...corsHeaders, 'Content-Type': 'application/json' },
      status: 200,
    });
  } catch (error) {
    return new Response(JSON.stringify({ error: error.message }), {
      headers: { ...corsHeaders, 'Content-Type': 'application/json' },
      status: 400,
    });
  }
});
```

### Edge Function with Database Access
```typescript
// supabase/functions/process-webhook/index.ts
import { serve } from 'https://deno.land/std@0.168.0/http/server.ts';
import { createClient } from 'https://esm.sh/@supabase/supabase-js@2';

serve(async (req) => {
  try {
    const signature = req.headers.get('stripe-signature');
    if (!signature) {
      throw new Error('Missing signature');
    }

    // Initialize Supabase Admin Client
    const supabaseAdmin = createClient(
      Deno.env.get('SUPABASE_URL') ?? '',
      Deno.env.get('SUPABASE_SERVICE_ROLE_KEY') ?? ''
    );

    // Parse webhook payload
    const payload = await req.json();

    // Process based on event type
    switch (payload.type) {
      case 'checkout.session.completed':
        const session = payload.data.object;
        
        // Update user subscription
        const { error } = await supabaseAdmin
          .from('subscriptions')
          .upsert({
            user_id: session.client_reference_id,
            stripe_customer_id: session.customer,
            stripe_subscription_id: session.subscription,
            status: 'active',
            current_period_end: new Date(session.expires_at * 1000).toISOString(),
          });

        if (error) throw error;
        break;

      // Handle other event types...
    }

    return new Response(JSON.stringify({ received: true }), {
      headers: { 'Content-Type': 'application/json' },
      status: 200,
    });
  } catch (error) {
    return new Response(JSON.stringify({ error: error.message }), {
      headers: { 'Content-Type': 'application/json' },
      status: 400,
    });
  }
});
```

## SQL Style Guide

### Naming Conventions
```sql
-- Tables: plural, snake_case
CREATE TABLE users (...);
CREATE TABLE organization_members (...);

-- Columns: snake_case
user_id UUID
created_at TIMESTAMPTZ
is_active BOOLEAN

-- Primary keys: table_name_pkey
ALTER TABLE users ADD CONSTRAINT users_pkey PRIMARY KEY (id);

-- Foreign keys: table_from_column_to_table_fkey
ALTER TABLE posts 
  ADD CONSTRAINT posts_user_id_users_fkey 
  FOREIGN KEY (user_id) REFERENCES users(id);

-- Indexes: idx_table_column(s)
CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_posts_user_id_created_at ON posts(user_id, created_at DESC);
```

### Query Formatting
```sql
-- Multi-line format for complex queries
SELECT 
  u.id,
  u.email,
  u.created_at,
  COUNT(p.id) AS post_count,
  MAX(p.created_at) AS last_post_at
FROM users u
LEFT JOIN posts p ON p.user_id = u.id
WHERE u.is_active = true
  AND u.created_at >= NOW() - INTERVAL '30 days'
GROUP BY u.id, u.email, u.created_at
HAVING COUNT(p.id) > 0
ORDER BY post_count DESC, u.created_at DESC
LIMIT 10;

-- CTEs for readability
WITH active_users AS (
  SELECT id, email
  FROM users
  WHERE is_active = true
    AND last_seen_at >= NOW() - INTERVAL '7 days'
),
user_stats AS (
  SELECT 
    user_id,
    COUNT(*) AS post_count,
    AVG(view_count) AS avg_views
  FROM posts
  WHERE published = true
  GROUP BY user_id
)
SELECT 
  au.email,
  COALESCE(us.post_count, 0) AS posts,
  COALESCE(us.avg_views, 0) AS average_views
FROM active_users au
LEFT JOIN user_stats us ON us.user_id = au.id
ORDER BY posts DESC;
```

## Best Practices

### Do's
- Use TypeScript with generated types
- Implement RLS for all tables
- Use prepared statements/parameterized queries
- Create indexes for foreign keys and common queries
- Use transactions for related operations
- Handle errors gracefully
- Clean up subscriptions
- Use connection pooling

### Don'ts
- Don't bypass RLS unless necessary
- Don't store sensitive data in public tables
- Don't use synchronous subscriptions in server components
- Don't forget to handle loading states
- Don't expose service role key to client
- Don't use `*` in production queries
- Don't ignore database constraints
- Don't forget cleanup in useEffect

### Security Checklist
- [ ] RLS enabled on all tables
- [ ] Appropriate policies for each operation
- [ ] Service role key only in secure environments
- [ ] API keys stored in environment variables
- [ ] Input validation before database operations
- [ ] SQL injection prevention
- [ ] Proper error messages (don't leak info)
- [ ] Regular security audits

## Supabase Edge Functions

### Basic Edge Function Structure
```typescript
// supabase/functions/hello-world/index.ts
interface RequestPayload {
  name: string;
  email?: string;
}

console.info('Function started');

Deno.serve(async (req: Request) => {
  // CORS headers
  if (req.method === 'OPTIONS') {
    return new Response('ok', {
      headers: {
        'Access-Control-Allow-Origin': '*',
        'Access-Control-Allow-Headers': 'authorization, x-client-info, apikey, content-type',
      },
    });
  }

  try {
    const { name, email }: RequestPayload = await req.json();
    
    // Process request
    const data = {
      message: `Hello ${name}!`,
      timestamp: new Date().toISOString(),
    };

    return new Response(JSON.stringify(data), {
      headers: { 
        'Content-Type': 'application/json',
        'Access-Control-Allow-Origin': '*',
      },
      status: 200,
    });
  } catch (error) {
    return new Response(JSON.stringify({ error: error.message }), {
      headers: { 'Content-Type': 'application/json' },
      status: 400,
    });
  }
});
```

### Edge Function with Supabase Client
```typescript
// supabase/functions/user-profile/index.ts
import { createClient } from 'npm:@supabase/supabase-js@2.38.0';
import { corsHeaders } from '../_shared/cors.ts';

Deno.serve(async (req: Request) => {
  if (req.method === 'OPTIONS') {
    return new Response('ok', { headers: corsHeaders });
  }

  try {
    // Create Supabase client with auth header
    const authHeader = req.headers.get('Authorization')!;
    const supabaseClient = createClient(
      Deno.env.get('SUPABASE_URL') ?? '',
      Deno.env.get('SUPABASE_ANON_KEY') ?? '',
      {
        global: {
          headers: { Authorization: authHeader },
        },
      }
    );

    // Get authenticated user
    const { data: { user }, error: userError } = await supabaseClient.auth.getUser();
    if (userError || !user) {
      throw new Error('Unauthorized');
    }

    // Fetch user profile
    const { data: profile, error: profileError } = await supabaseClient
      .from('profiles')
      .select('*')
      .eq('id', user.id)
      .single();

    if (profileError) {
      throw profileError;
    }

    return new Response(JSON.stringify({ profile }), {
      headers: { ...corsHeaders, 'Content-Type': 'application/json' },
      status: 200,
    });
  } catch (error) {
    return new Response(JSON.stringify({ error: error.message }), {
      headers: { ...corsHeaders, 'Content-Type': 'application/json' },
      status: error.message === 'Unauthorized' ? 401 : 400,
    });
  }
});
```

### Shared Utilities
```typescript
// supabase/functions/_shared/cors.ts
export const corsHeaders = {
  'Access-Control-Allow-Origin': '*',
  'Access-Control-Allow-Headers': 'authorization, x-client-info, apikey, content-type',
};

// supabase/functions/_shared/supabase.ts
import { createClient } from 'npm:@supabase/supabase-js@2.38.0';

export const createServiceClient = () => {
  return createClient(
    Deno.env.get('SUPABASE_URL') ?? '',
    Deno.env.get('SUPABASE_SERVICE_ROLE_KEY') ?? '',
    {
      auth: {
        autoRefreshToken: false,
        persistSession: false,
      },
    }
  );
};

export const createAuthClient = (authHeader: string) => {
  return createClient(
    Deno.env.get('SUPABASE_URL') ?? '',
    Deno.env.get('SUPABASE_ANON_KEY') ?? '',
    {
      global: {
        headers: { Authorization: authHeader },
      },
      auth: {
        autoRefreshToken: false,
        persistSession: false,
      },
    }
  );
};
```

### Edge Function with File Upload
```typescript
// supabase/functions/upload-avatar/index.ts
import { createServiceClient } from '../_shared/supabase.ts';
import { corsHeaders } from '../_shared/cors.ts';

Deno.serve(async (req: Request) => {
  if (req.method === 'OPTIONS') {
    return new Response('ok', { headers: corsHeaders });
  }

  try {
    const formData = await req.formData();
    const file = formData.get('file') as File;
    const userId = formData.get('userId') as string;

    if (!file || !userId) {
      throw new Error('Missing file or userId');
    }

    // Validate file
    const validTypes = ['image/jpeg', 'image/png', 'image/webp'];
    if (!validTypes.includes(file.type)) {
      throw new Error('Invalid file type');
    }

    const maxSize = 5 * 1024 * 1024; // 5MB
    if (file.size > maxSize) {
      throw new Error('File too large');
    }

    // Upload to Supabase Storage
    const supabase = createServiceClient();
    const fileName = `${userId}/${Date.now()}-${file.name}`;
    
    const { data, error } = await supabase.storage
      .from('avatars')
      .upload(fileName, file, {
        contentType: file.type,
        upsert: false,
      });

    if (error) throw error;

    // Get public URL
    const { data: { publicUrl } } = supabase.storage
      .from('avatars')
      .getPublicUrl(fileName);

    // Update user profile
    const { error: updateError } = await supabase
      .from('profiles')
      .update({ avatar_url: publicUrl })
      .eq('id', userId);

    if (updateError) throw updateError;

    return new Response(
      JSON.stringify({ url: publicUrl }),
      {
        headers: { ...corsHeaders, 'Content-Type': 'application/json' },
        status: 200,
      }
    );
  } catch (error) {
    return new Response(
      JSON.stringify({ error: error.message }),
      {
        headers: { ...corsHeaders, 'Content-Type': 'application/json' },
        status: 400,
      }
    );
  }
});
```

### Background Tasks with EdgeRuntime
```typescript
// supabase/functions/process-order/index.ts
import { createServiceClient } from '../_shared/supabase.ts';

Deno.serve(async (req: Request) => {
  try {
    const { orderId } = await req.json();
    
    // Return immediate response
    const response = new Response(
      JSON.stringify({ message: 'Order processing started' }),
      { headers: { 'Content-Type': 'application/json' } }
    );

    // Process order in background
    EdgeRuntime.waitUntil(
      processOrderAsync(orderId)
    );

    return response;
  } catch (error) {
    return new Response(
      JSON.stringify({ error: error.message }),
      { status: 400 }
    );
  }
});

async function processOrderAsync(orderId: string) {
  const supabase = createServiceClient();
  
  try {
    // Update order status
    await supabase
      .from('orders')
      .update({ status: 'processing' })
      .eq('id', orderId);

    // Simulate processing
    await new Promise(resolve => setTimeout(resolve, 5000));

    // Complete order
    await supabase
      .from('orders')
      .update({ 
        status: 'completed',
        completed_at: new Date().toISOString()
      })
      .eq('id', orderId);

    // Send notification email
    await fetch('https://api.resend.com/emails', {
      method: 'POST',
      headers: {
        'Authorization': `Bearer ${Deno.env.get('RESEND_API_KEY')}`,
        'Content-Type': 'application/json',
      },
      body: JSON.stringify({
        from: 'orders@example.com',
        to: 'customer@example.com',
        subject: 'Order Completed',
        html: '<p>Your order has been processed!</p>',
      }),
    });
  } catch (error) {
    console.error('Background task error:', error);
    // Log to error tracking service
  }
}
```

### Edge Function Best Practices

#### Do's
- Use Web APIs instead of Node packages when possible
- Import npm packages with version numbers
- Store shared utilities in `_shared` folder
- Use environment variables for secrets
- Handle CORS properly
- Return appropriate status codes
- Use TypeScript for type safety
- Implement proper error handling
- Use `EdgeRuntime.waitUntil()` for background tasks
- Write to `/tmp` directory only

#### Don'ts
- Don't use bare specifiers for imports
- Don't import from deno.land/x unless necessary
- Don't create cross-dependencies between functions
- Don't expose sensitive data in responses
- Don't forget CORS headers
- Don't use deprecated `serve` from std
- Don't write files outside `/tmp`
- Don't block responses with long operations

#### Import Examples
```typescript
// ✅ Good imports
import express from 'npm:express@4.18.2';
import { z } from 'npm:zod@3.22.0';
import process from 'node:process';
import { randomBytes } from 'node:crypto';

// ❌ Bad imports
import express from 'express'; // No npm: prefix
import { z } from 'npm:zod'; // No version
import { serve } from 'https://deno.land/std@0.168.0/http/server.ts'; // Use Deno.serve
```