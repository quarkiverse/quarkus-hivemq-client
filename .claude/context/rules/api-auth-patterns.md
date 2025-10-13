---
description: API routes, authentication, authorization, and security patterns
alwaysApply: true
---

# API & Authentication Patterns

Consolidated from: api-route-handlers.mdc, auth-and-authorization.mdc, security-hardening-checklist.mdc

**Enhanced with NextBase-specific patterns from existing .claude/context/rules/ files**

## NextBase Server Actions (Preferred)

```typescript
// ✅ NextBase Server Action Pattern (Recommended over API routes)
'use server';
import { z } from 'zod';
import { authenticatedAction } from '@/lib/safe-action';
import { createSupabaseUserServerActionClient } from '@/supabase-clients/user/createSupabaseUserServerActionClient';
import { revalidatePath } from 'next/cache';

const createProjectSchema = z.object({
  name: z.string().min(1),
  description: z.string().optional(),
  workspaceId: z.string().uuid()
});

export const createProjectAction = authenticatedAction
  .schema(createProjectSchema)
  .action(async ({ parsedInput, ctx }) => {
    const { user } = ctx; // User is guaranteed to exist
    const supabase = createSupabaseUserServerActionClient();
    
    // Permission check
    const { data: membership } = await supabase
      .from('workspace_members')
      .select('role')
      .eq('workspace_id', parsedInput.workspaceId)
      .eq('user_id', user.id)
      .single();
      
    if (!membership) {
      throw new Error('Access denied to workspace');
    }
    
    const { data, error } = await supabase
      .from('projects')
      .insert({
        ...parsedInput,
        created_by: user.id
      })
      .select()
      .single();
      
    if (error) {
      throw new Error(`Failed to create project: ${error.message}`);
    }
    
    // Revalidate relevant pages
    revalidatePath('/dashboard');
    revalidatePath(`/workspace/${parsedInput.workspaceId}`);
    
    return data;
  });
```

## NextBase API Route Handlers

### Basic API Routes
```typescript
// ✅ NextBase API Route Handler Pattern
import { createSupabaseUserRouteHandlerClient } from '@/supabase-clients/user/createSupabaseUserRouteHandlerClient';
import { NextRequest, NextResponse } from 'next/server';
import { z } from 'zod';

// Consistent API response types
export interface ApiResponse<T = any> {
  data?: T;
  error?: string;
  message?: string;
}

export interface PaginatedResponse<T> extends ApiResponse<T[]> {
  pagination: {
    page: number;
    pageSize: number;
    total: number;
    totalPages: number;
  };
}

const requestSchema = z.object({
  name: z.string().min(1),
  workspaceId: z.string().uuid()
});

// GET /api/projects
export async function GET(request: NextRequest) {
  try {
    const supabase = createSupabaseUserRouteHandlerClient();
    
    // Get authenticated user
    const { data: { user }, error: authError } = await supabase.auth.getUser();
    if (authError || !user) {
      return NextResponse.json({ error: 'Unauthorized' }, { status: 401 });
    }

    const { searchParams } = new URL(request.url);
    const workspaceId = searchParams.get('workspaceId');
    
    const { data, error } = await supabase
      .from('projects')
      .select('*')
      .eq('workspace_id', workspaceId)
      .eq('created_by', user.id);
      
    if (error) {
      return NextResponse.json({ error: error.message }, { status: 400 });
    }
    
    return NextResponse.json<ApiResponse<Project[]>>({ data });
  } catch (error) {
    console.error('API Error:', error);
    return NextResponse.json(
      { error: 'Internal server error' },
      { status: 500 }
    );
  }
}

export async function POST(request: NextRequest) {
  try {
    const supabase = createSupabaseUserRouteHandlerClient();
    
    // Get authenticated user
    const { data: { user }, error: authError } = await supabase.auth.getUser();
    if (authError || !user) {
      return NextResponse.json({ error: 'Unauthorized' }, { status: 401 });
    }
    
    // Validate request body
    const body = await request.json();
    const validatedData = requestSchema.parse(body);
    
    // Business logic
    const { data, error } = await supabase
      .from('projects')
      .insert({
        ...validatedData,
        created_by: user.id
      })
      .select()
      .single();
      
    if (error) {
      return NextResponse.json({ error: error.message }, { status: 400 });
    }
    
    return NextResponse.json<ApiResponse<Project>>({ data });
    
  } catch (error) {
    if (error instanceof z.ZodError) {
      return NextResponse.json({ 
        error: 'Validation failed', 
        details: error.errors 
      }, { status: 400 });
    }
    
    console.error('API Error:', error);
    return NextResponse.json({ 
      error: 'Internal server error' 
    }, { status: 500 });
  }
}
```

### Legacy Pattern (for reference)
```typescript
// Standard Next.js pattern (use NextBase pattern above instead)
export async function GET(request: NextRequest) {
  try {
    // Get auth session
    const session = await getServerSession();
    if (!session) {
      return NextResponse.json(
        { error: 'Unauthorized' },
        { status: 401 }
      );
    }

    // Parse query params
    const { searchParams } = new URL(request.url);
    const page = parseInt(searchParams.get('page') || '1');
    const limit = parseInt(searchParams.get('limit') || '10');
    const search = searchParams.get('search') || '';

    // Fetch data using Supabase instead of Prisma
    const { data: users, error, count } = await supabase
      .from('users')
      .select('*', { count: 'exact' })
      .or(`name.ilike.%${search}%,email.ilike.%${search}%`)
      .range((page - 1) * limit, page * limit - 1)
      .order('created_at', { ascending: false });

    if (error) {
      return NextResponse.json(
        { error: error.message },
        { status: 400 }
      );
    }

    return NextResponse.json({
      users,
      pagination: {
        page,
        limit,
        total: count || 0,
        totalPages: Math.ceil((count || 0) / limit),
      },
    });
  } catch (error) {
    console.error('API Error:', error);
    return NextResponse.json(
      { error: 'Internal Server Error' },
      { status: 500 }
    );
  }
}

// POST /api/users (NextBase version)
const createUserSchema = z.object({
  email: z.string().email(),
  name: z.string().min(1).max(100),
  role: z.enum(['admin', 'user']).default('user'),
});

export async function POST(request: NextRequest) {
  try {
    const supabase = createSupabaseUserRouteHandlerClient();
    
    const { data: { user: currentUser }, error: authError } = await supabase.auth.getUser();
    if (authError || !currentUser) {
      return NextResponse.json(
        { error: 'Unauthorized' },
        { status: 401 }
      );
    }

    // Check if user is admin
    const { data: profile } = await supabase
      .from('profiles')
      .select('is_application_admin')
      .eq('id', currentUser.id)
      .single();
      
    if (!profile?.is_application_admin) {
      return NextResponse.json(
        { error: 'Forbidden' },
        { status: 403 }
      );
    }

    const body = await request.json();
    const validatedData = createUserSchema.parse(body);

    const { data: newUser, error } = await supabase
      .from('profiles')
      .insert(validatedData)
      .select()
      .single();
      
    if (error) {
      return NextResponse.json(
        { error: error.message },
        { status: 400 }
      );
    }

    return NextResponse.json(newUser, { status: 201 });
  } catch (error) {
    if (error instanceof z.ZodError) {
      return NextResponse.json(
        { error: 'Validation failed', details: error.errors },
        { status: 422 }
      );
    }

    return NextResponse.json(
      { error: 'Internal Server Error' },
      { status: 500 }
    );
  }
}
```

### Dynamic Route Handlers
```typescript
// app/api/users/[id]/route.ts
interface RouteParams {
  params: {
    id: string;
  };
}

// GET /api/users/[id]
export async function GET(request: NextRequest, { params }: RouteParams) {
  try {
    const session = await getServerSession();
    if (!session) {
      return NextResponse.json(
        { error: 'Unauthorized' },
        { status: 401 }
      );
    }

    const user = await db.user.findUnique({
      where: { id: params.id },
      include: {
        profile: true,
        posts: {
          take: 5,
          orderBy: { createdAt: 'desc' },
        },
      },
    });

    if (!user) {
      return NextResponse.json(
        { error: 'User not found' },
        { status: 404 }
      );
    }

    // Check if user can view this profile
    if (user.id !== session.user.id && !user.publicProfile) {
      return NextResponse.json(
        { error: 'Forbidden' },
        { status: 403 }
      );
    }

    return NextResponse.json(user);
  } catch (error) {
    return NextResponse.json(
      { error: 'Internal Server Error' },
      { status: 500 }
    );
  }
}

// PATCH /api/users/[id]
const updateUserSchema = z.object({
  name: z.string().min(1).max(100).optional(),
  bio: z.string().max(500).optional(),
  publicProfile: z.boolean().optional(),
});

export async function PATCH(request: NextRequest, { params }: RouteParams) {
  try {
    const session = await getServerSession();
    if (!session) {
      return NextResponse.json(
        { error: 'Unauthorized' },
        { status: 401 }
      );
    }

    // Check if user can update this profile
    if (params.id !== session.user.id && session.user.role !== 'admin') {
      return NextResponse.json(
        { error: 'Forbidden' },
        { status: 403 }
      );
    }

    const body = await request.json();
    const validatedData = updateUserSchema.parse(body);

    const user = await db.user.update({
      where: { id: params.id },
      data: validatedData,
    });

    return NextResponse.json(user);
  } catch (error) {
    if (error instanceof z.ZodError) {
      return NextResponse.json(
        { error: 'Validation failed', details: error.errors },
        { status: 422 }
      );
    }

    return NextResponse.json(
      { error: 'Internal Server Error' },
      { status: 500 }
    );
  }
}

// DELETE /api/users/[id]
export async function DELETE(request: NextRequest, { params }: RouteParams) {
  try {
    const session = await getServerSession();
    if (!session || session.user.role !== 'admin') {
      return NextResponse.json(
        { error: 'Forbidden' },
        { status: 403 }
      );
    }

    await db.user.delete({
      where: { id: params.id },
    });

    return new NextResponse(null, { status: 204 });
  } catch (error) {
    return NextResponse.json(
      { error: 'Internal Server Error' },
      { status: 500 }
    );
  }
}
```

### File Upload API Routes
```typescript
// app/api/upload/route.ts
import { writeFile } from 'fs/promises';
import { join } from 'path';

export async function POST(request: NextRequest) {
  try {
    const session = await getServerSession();
    if (!session) {
      return NextResponse.json(
        { error: 'Unauthorized' },
        { status: 401 }
      );
    }

    const formData = await request.formData();
    const file = formData.get('file') as File;

    if (!file) {
      return NextResponse.json(
        { error: 'No file provided' },
        { status: 400 }
      );
    }

    // Validate file
    const validTypes = ['image/jpeg', 'image/png', 'image/webp'];
    if (!validTypes.includes(file.type)) {
      return NextResponse.json(
        { error: 'Invalid file type' },
        { status: 400 }
      );
    }

    const maxSize = 5 * 1024 * 1024; // 5MB
    if (file.size > maxSize) {
      return NextResponse.json(
        { error: 'File too large' },
        { status: 400 }
      );
    }

    // Save file
    const bytes = await file.arrayBuffer();
    const buffer = Buffer.from(bytes);

    const filename = `${Date.now()}-${file.name}`;
    const filepath = join(process.cwd(), 'public/uploads', filename);

    await writeFile(filepath, buffer);

    // Save to database
    const upload = await db.upload.create({
      data: {
        filename,
        originalName: file.name,
        mimeType: file.type,
        size: file.size,
        userId: session.user.id,
      },
    });

    return NextResponse.json({
      id: upload.id,
      url: `/uploads/${filename}`,
    });
  } catch (error) {
    return NextResponse.json(
      { error: 'Upload failed' },
      { status: 500 }
    );
  }
}
```

### Streaming Responses
```typescript
// app/api/stream/route.ts
export async function GET(request: NextRequest) {
  const encoder = new TextEncoder();
  const stream = new TransformStream();
  const writer = stream.writable.getWriter();

  // Start async work
  (async () => {
    try {
      for (let i = 0; i < 10; i++) {
        const data = JSON.stringify({ 
          message: `Data chunk ${i}`, 
          timestamp: new Date().toISOString() 
        });
        
        await writer.write(
          encoder.encode(`data: ${data}\n\n`)
        );
        
        // Simulate delay
        await new Promise(resolve => setTimeout(resolve, 1000));
      }
      
      await writer.write(encoder.encode('data: [DONE]\n\n'));
    } catch (error) {
      console.error('Stream error:', error);
    } finally {
      await writer.close();
    }
  })();

  return new Response(stream.readable, {
    headers: {
      'Content-Type': 'text/event-stream',
      'Cache-Control': 'no-cache',
      'Connection': 'keep-alive',
    },
  });
}
```

## NextBase Authentication Patterns

### Server-Side Auth
```typescript
// ✅ NextBase Server Component Auth Check
import { getLoggedInUser } from '@/utils/server/serverGetLoggedInUser';
import { redirect } from 'next/navigation';

export default async function ProtectedPage() {
  const user = await getLoggedInUser();
  
  if (!user) {
    redirect('/login');
  }
  
  return <PageContent user={user} />;
}

// ✅ Optional Auth Check
import { serverGetLoggedInUser } from '@/utils/server/serverGetLoggedInUser';

export default async function OptionalAuthPage() {
  const user = await serverGetLoggedInUser();
  
  return <PageContent user={user} />; // user can be null
}
```

### Layout-Level Protection
```typescript
// ✅ NextBase Protected layout pattern
import { getLoggedInUser } from '@/utils/server/serverGetLoggedInUser';
import { redirect } from 'next/navigation';

export default async function AuthenticatedLayout({
  children,
}: {
  children: React.ReactNode;
}) {
  const user = await getLoggedInUser();
  
  if (!user) {
    redirect('/login');
  }
  
  return (
    <LoggedInUserProvider user={user}>
      {children}
    </LoggedInUserProvider>
  );
}
```

### Client-Side Auth Context
```typescript
// ✅ NextBase Using auth context in Client Components
'use client';
import { useLoggedInUser } from '@/hooks/useLoggedInUser';

export function UserProfile() {
  const { user, isLoading } = useLoggedInUser();
  
  if (isLoading) return <div>Loading...</div>;
  if (!user) return <div>Please log in</div>;
  
  return <div>Welcome, {user.email}</div>;
}
```

### NextAuth.js Configuration (Alternative)
```typescript
// app/api/auth/[...nextauth]/route.ts
import NextAuth from 'next-auth';
import type { NextAuthOptions } from 'next-auth';
import GithubProvider from 'next-auth/providers/github';
import GoogleProvider from 'next-auth/providers/google';
import CredentialsProvider from 'next-auth/providers/credentials';
import { PrismaAdapter } from '@auth/prisma-adapter';
import bcrypt from 'bcryptjs';

export const authOptions: NextAuthOptions = {
  adapter: PrismaAdapter(db),
  session: {
    strategy: 'jwt',
  },
  pages: {
    signIn: '/login',
    signOut: '/logout',
    error: '/auth/error',
    verifyRequest: '/auth/verify',
    newUser: '/onboarding',
  },
  providers: [
    GithubProvider({
      clientId: process.env.GITHUB_ID!,
      clientSecret: process.env.GITHUB_SECRET!,
    }),
    GoogleProvider({
      clientId: process.env.GOOGLE_ID!,
      clientSecret: process.env.GOOGLE_SECRET!,
    }),
    CredentialsProvider({
      name: 'credentials',
      credentials: {
        email: { label: 'Email', type: 'email' },
        password: { label: 'Password', type: 'password' },
      },
      async authorize(credentials) {
        if (!credentials?.email || !credentials?.password) {
          throw new Error('Invalid credentials');
        }

        const user = await db.user.findUnique({
          where: { email: credentials.email },
        });

        if (!user || !user.password) {
          throw new Error('Invalid credentials');
        }

        const isValid = await bcrypt.compare(
          credentials.password,
          user.password
        );

        if (!isValid) {
          throw new Error('Invalid credentials');
        }

        return {
          id: user.id,
          email: user.email,
          name: user.name,
          role: user.role,
        };
      },
    }),
  ],
  callbacks: {
    async jwt({ token, user, account, trigger, session }) {
      if (trigger === 'update') {
        return { ...token, ...session.user };
      }

      if (user) {
        token.id = user.id;
        token.role = user.role;
      }

      if (account) {
        token.accessToken = account.access_token;
        token.refreshToken = account.refresh_token;
      }

      return token;
    },
    async session({ session, token }) {
      if (token && session.user) {
        session.user.id = token.id as string;
        session.user.role = token.role as string;
      }

      return session;
    },
  },
  events: {
    async signIn({ user, account, profile }) {
      // Log sign in
      await db.auditLog.create({
        data: {
          action: 'SIGN_IN',
          userId: user.id,
          metadata: {
            provider: account?.provider,
            ip: request.headers.get('x-forwarded-for'),
          },
        },
      });
    },
  },
};

const handler = NextAuth(authOptions);
export { handler as GET, handler as POST };
```

### Protected API Routes
```typescript
// lib/auth.ts
import { getServerSession } from 'next-auth';
import { authOptions } from '@/app/api/auth/[...nextauth]/route';

export async function getCurrentUser() {
  const session = await getServerSession(authOptions);
  return session?.user;
}

export async function requireAuth() {
  const user = await getCurrentUser();
  if (!user) {
    throw new Error('Unauthorized');
  }
  return user;
}

export async function requireRole(role: string) {
  const user = await requireAuth();
  if (user.role !== role) {
    throw new Error('Forbidden');
  }
  return user;
}

// Usage in API route
export async function GET(request: NextRequest) {
  try {
    const user = await requireAuth();
    // User is authenticated
    
    // For admin only
    await requireRole('admin');
    // User is admin
    
    // Continue with logic
  } catch (error) {
    if (error.message === 'Unauthorized') {
      return NextResponse.json(
        { error: 'Authentication required' },
        { status: 401 }
      );
    }
    if (error.message === 'Forbidden') {
      return NextResponse.json(
        { error: 'Insufficient permissions' },
        { status: 403 }
      );
    }
    throw error;
  }
}
```

### JWT Token Management
```typescript
// lib/jwt.ts
import jwt from 'jsonwebtoken';

const secret = process.env.JWT_SECRET!;

export interface TokenPayload {
  userId: string;
  email: string;
  role: string;
}

export function signToken(payload: TokenPayload): string {
  return jwt.sign(payload, secret, {
    expiresIn: '7d',
  });
}

export function verifyToken(token: string): TokenPayload {
  return jwt.verify(token, secret) as TokenPayload;
}

// API route with custom JWT
export async function POST(request: NextRequest) {
  try {
    const { email, password } = await request.json();
    
    // Validate credentials
    const user = await validateCredentials(email, password);
    if (!user) {
      return NextResponse.json(
        { error: 'Invalid credentials' },
        { status: 401 }
      );
    }

    // Generate tokens
    const accessToken = signToken({
      userId: user.id,
      email: user.email,
      role: user.role,
    });

    const refreshToken = await generateRefreshToken(user.id);

    // Set cookies
    const response = NextResponse.json({
      user: {
        id: user.id,
        email: user.email,
        name: user.name,
      },
    });

    response.cookies.set('access-token', accessToken, {
      httpOnly: true,
      secure: process.env.NODE_ENV === 'production',
      sameSite: 'lax',
      maxAge: 60 * 60 * 24 * 7, // 7 days
    });

    response.cookies.set('refresh-token', refreshToken, {
      httpOnly: true,
      secure: process.env.NODE_ENV === 'production',
      sameSite: 'lax',
      maxAge: 60 * 60 * 24 * 30, // 30 days
    });

    return response;
  } catch (error) {
    return NextResponse.json(
      { error: 'Authentication failed' },
      { status: 500 }
    );
  }
}
```

## Authorization Patterns

### Role-Based Access Control (RBAC)
```typescript
// lib/rbac.ts
export const permissions = {
  admin: [
    'users.read',
    'users.write',
    'users.delete',
    'posts.read',
    'posts.write',
    'posts.delete',
    'posts.publish',
    'settings.read',
    'settings.write',
  ],
  editor: [
    'posts.read',
    'posts.write',
    'posts.publish',
    'users.read',
  ],
  user: [
    'posts.read',
    'posts.write',
  ],
} as const;

export function hasPermission(
  userRole: keyof typeof permissions,
  permission: string
): boolean {
  return permissions[userRole]?.includes(permission) ?? false;
}

// Middleware to check permissions
export function requirePermission(permission: string) {
  return async (request: NextRequest) => {
    const user = await getCurrentUser();
    if (!user) {
      return NextResponse.json(
        { error: 'Unauthorized' },
        { status: 401 }
      );
    }

    if (!hasPermission(user.role, permission)) {
      return NextResponse.json(
        { error: 'Insufficient permissions' },
        { status: 403 }
      );
    }

    return NextResponse.next();
  };
}
```

### Resource-Based Access Control
```typescript
// lib/authorization.ts
export async function canUserAccessResource(
  userId: string,
  resourceType: 'post' | 'project' | 'team',
  resourceId: string,
  action: 'read' | 'write' | 'delete'
): Promise<boolean> {
  switch (resourceType) {
    case 'post':
      const post = await db.post.findUnique({
        where: { id: resourceId },
      });
      
      if (!post) return false;
      
      // Owner can do anything
      if (post.authorId === userId) return true;
      
      // Published posts can be read by anyone
      if (action === 'read' && post.published) return true;
      
      // Check if user is editor
      const user = await db.user.findUnique({
        where: { id: userId },
      });
      
      if (user?.role === 'admin' || user?.role === 'editor') {
        return true;
      }
      
      return false;

    case 'project':
      // Check if user is project member
      const membership = await db.projectMember.findUnique({
        where: {
          projectId_userId: {
            projectId: resourceId,
            userId,
          },
        },
      });
      
      if (!membership) return false;
      
      // Check role permissions
      if (action === 'read') return true;
      if (action === 'write' && ['admin', 'editor'].includes(membership.role)) {
        return true;
      }
      if (action === 'delete' && membership.role === 'admin') {
        return true;
      }
      
      return false;

    default:
      return false;
  }
}

// Usage in API route
export async function PATCH(
  request: NextRequest,
  { params }: { params: { id: string } }
) {
  const user = await requireAuth();
  
  const canAccess = await canUserAccessResource(
    user.id,
    'post',
    params.id,
    'write'
  );
  
  if (!canAccess) {
    return NextResponse.json(
      { error: 'Access denied' },
      { status: 403 }
    );
  }
  
  // Continue with update
}
```

## Security Best Practices

### Input Validation & Sanitization
```typescript
// lib/validation.ts
import { z } from 'zod';
import DOMPurify from 'isomorphic-dompurify';

// Strict input validation schemas
export const userInputSchema = z.object({
  email: z.string().email().toLowerCase().trim(),
  name: z.string()
    .min(1)
    .max(100)
    .trim()
    .regex(/^[a-zA-Z0-9\s\-']+$/, 'Invalid characters in name'),
  bio: z.string()
    .max(500)
    .transform(val => DOMPurify.sanitize(val))
    .optional(),
  website: z.string()
    .url()
    .startsWith('https://')
    .optional(),
});

// SQL injection prevention (using parameterized queries)
export async function getUserByEmail(email: string) {
  // Good - parameterized query
  return await db.user.findUnique({
    where: { email },
  });
  
  // Bad - string concatenation
  // const query = `SELECT * FROM users WHERE email = '${email}'`;
}

// XSS prevention
export function sanitizeHtml(input: string): string {
  return DOMPurify.sanitize(input, {
    ALLOWED_TAGS: ['b', 'i', 'em', 'strong', 'a', 'p', 'br'],
    ALLOWED_ATTR: ['href'],
  });
}
```

### Rate Limiting
```typescript
// lib/rate-limit.ts
import { LRUCache } from 'lru-cache';

type Options = {
  uniqueTokenPerInterval?: number;
  interval?: number;
};

export function rateLimit(options?: Options) {
  const tokenCache = new LRUCache({
    max: options?.uniqueTokenPerInterval || 500,
    ttl: options?.interval || 60000, // 60 seconds
  });

  return {
    check: (limit: number, token: string) =>
      new Promise<void>((resolve, reject) => {
        const tokenCount = (tokenCache.get(token) as number[]) || [0];
        if (tokenCount[0] === 0) {
          tokenCache.set(token, tokenCount);
        }
        tokenCount[0] += 1;

        const currentUsage = tokenCount[0];
        const isRateLimited = currentUsage >= limit;

        return isRateLimited ? reject() : resolve();
      }),
  };
}

// Usage in API route
const limiter = rateLimit({
  interval: 60 * 1000, // 60 seconds
  uniqueTokenPerInterval: 500,
});

export async function POST(request: NextRequest) {
  try {
    const ip = request.ip ?? '127.0.0.1';
    await limiter.check(10, ip); // 10 requests per minute
  } catch {
    return NextResponse.json(
      { error: 'Too many requests' },
      { status: 429 }
    );
  }

  // Continue with request handling
}
```

### CSRF Protection
```typescript
// lib/csrf.ts
import { randomBytes } from 'crypto';

export function generateCSRFToken(): string {
  return randomBytes(32).toString('hex');
}

export async function validateCSRFToken(
  request: NextRequest,
  sessionToken: string
): Promise<boolean> {
  const headerToken = request.headers.get('x-csrf-token');
  const bodyToken = (await request.json()).csrfToken;
  
  return (
    headerToken === sessionToken || 
    bodyToken === sessionToken
  );
}

// Middleware for CSRF protection
export async function csrfProtection(request: NextRequest) {
  if (['POST', 'PUT', 'DELETE', 'PATCH'].includes(request.method)) {
    const session = await getServerSession();
    if (!session?.csrfToken) {
      return NextResponse.json(
        { error: 'No CSRF token' },
        { status: 403 }
      );
    }

    const isValid = await validateCSRFToken(request, session.csrfToken);
    if (!isValid) {
      return NextResponse.json(
        { error: 'Invalid CSRF token' },
        { status: 403 }
      );
    }
  }

  return NextResponse.next();
}
```

### Security Headers
```typescript
// middleware.ts
export function middleware(request: NextRequest) {
  const response = NextResponse.next();

  // Security headers
  response.headers.set(
    'Strict-Transport-Security',
    'max-age=31536000; includeSubDomains'
  );
  response.headers.set(
    'Content-Security-Policy',
    "default-src 'self'; img-src 'self' data: https:; script-src 'self' 'unsafe-inline' 'unsafe-eval';"
  );
  response.headers.set('X-Frame-Options', 'DENY');
  response.headers.set('X-Content-Type-Options', 'nosniff');
  response.headers.set('Referrer-Policy', 'strict-origin-when-cross-origin');
  response.headers.set(
    'Permissions-Policy',
    'camera=(), microphone=(), geolocation=()'
  );

  return response;
}
```

## API Error Handling

### Consistent Error Responses
```typescript
// lib/api-errors.ts
export class ApiError extends Error {
  constructor(
    public statusCode: number,
    message: string,
    public code?: string
  ) {
    super(message);
    this.name = 'ApiError';
  }
}

export const ErrorCode = {
  UNAUTHORIZED: 'UNAUTHORIZED',
  FORBIDDEN: 'FORBIDDEN',
  NOT_FOUND: 'NOT_FOUND',
  VALIDATION_ERROR: 'VALIDATION_ERROR',
  RATE_LIMITED: 'RATE_LIMITED',
  INTERNAL_ERROR: 'INTERNAL_ERROR',
} as const;

export function handleApiError(error: unknown): NextResponse {
  console.error('API Error:', error);

  if (error instanceof ApiError) {
    return NextResponse.json(
      {
        error: {
          message: error.message,
          code: error.code,
        },
      },
      { status: error.statusCode }
    );
  }

  if (error instanceof z.ZodError) {
    return NextResponse.json(
      {
        error: {
          message: 'Validation failed',
          code: ErrorCode.VALIDATION_ERROR,
          details: error.errors,
        },
      },
      { status: 422 }
    );
  }

  // Don't leak internal errors
  return NextResponse.json(
    {
      error: {
        message: 'Internal server error',
        code: ErrorCode.INTERNAL_ERROR,
      },
    },
    { status: 500 }
  );
}

// Usage
export async function GET(request: NextRequest) {
  try {
    const user = await getCurrentUser();
    if (!user) {
      throw new ApiError(401, 'Authentication required', ErrorCode.UNAUTHORIZED);
    }
    
    // ... rest of logic
  } catch (error) {
    return handleApiError(error);
  }
}
```

## Best Practices

### Security Checklist
- [ ] Implement authentication for all protected routes
- [ ] Use HTTPS in production
- [ ] Validate and sanitize all inputs
- [ ] Implement rate limiting
- [ ] Use parameterized queries
- [ ] Set security headers
- [ ] Implement CSRF protection
- [ ] Use secure session management
- [ ] Hash passwords with bcrypt
- [ ] Implement proper error handling
- [ ] Log security events
- [ ] Regular security audits
- [ ] Keep dependencies updated
- [ ] Use environment variables for secrets
- [ ] Implement least privilege principle

### Do's
- Use type-safe API routes
- Implement proper authentication
- Validate all inputs with Zod
- Use consistent error responses
- Implement rate limiting
- Log security events
- Use HTTPS everywhere
- Keep secrets secure

### Don'ts
- Don't trust client input
- Don't expose sensitive errors
- Don't use eval() or similar
- Don't store passwords in plain text
- Don't expose internal IDs
- Don't skip authentication checks
- Don't ignore security headers
- Don't use deprecated crypto