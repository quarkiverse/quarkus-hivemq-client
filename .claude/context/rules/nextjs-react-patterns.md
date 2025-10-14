---
description: Consolidated Next.js 15 and React 18/19 patterns and best practices
alwaysApply: true
---

# Next.js & React Best Practices

Consolidated from: next-coding-standards.mdc, react-18-supabase.mdc, next-shadcn-coding-standards.mdc, server-actions-patterns.mdc, component-architecture.mdc, navigation-patterns.mdc

## Next.js 15 App Router

### Project Structure
```
app/
├── (auth)/                 # Route groups for organization
│   ├── login/
│   └── register/
├── (dashboard)/
│   ├── layout.tsx         # Shared layout
│   ├── page.tsx          # Dashboard home
│   └── settings/
├── api/                   # API routes
│   └── webhooks/
├── layout.tsx            # Root layout
└── global.css           # Global styles
```

### Server vs Client Components

#### Server Components (Default)
```typescript
// app/users/page.tsx - Server Component by default
import { db } from '@/lib/db';

export default async function UsersPage() {
  const users = await db.query.users.findMany();
  
  return (
    <div>
      {users.map(user => (
        <UserCard key={user.id} user={user} />
      ))}
    </div>
  );
}
```

#### Client Components
```typescript
'use client';

// components/interactive-chart.tsx
import { useState, useEffect } from 'react';
import { LineChart } from '@/components/ui/chart';

export function InteractiveChart({ data }) {
  const [filter, setFilter] = useState('all');
  
  // Client-side interactivity
  return <LineChart data={filteredData} />;
}
```

### Data Fetching Patterns

#### Parallel Data Fetching
```typescript
// app/dashboard/page.tsx
export default async function DashboardPage() {
  // Fetch in parallel for better performance
  const [user, projects, analytics] = await Promise.all([
    getUser(),
    getProjects(),
    getAnalytics(),
  ]);

  return <Dashboard user={user} projects={projects} analytics={analytics} />;
}
```

#### Streaming with Suspense
```typescript
// app/products/page.tsx
import { Suspense } from 'react';
import { ProductList } from './product-list';
import { ProductListSkeleton } from './product-skeleton';

export default function ProductsPage() {
  return (
    <div>
      <h1>Products</h1>
      <Suspense fallback={<ProductListSkeleton />}>
        <ProductList />
      </Suspense>
    </div>
  );
}
```

### Server Actions

#### Basic Server Action
```typescript
// app/actions/user.ts
'use server';

import { revalidatePath } from 'next/cache';
import { z } from 'zod';

const updateUserSchema = z.object({
  id: z.string(),
  name: z.string().min(1),
  email: z.string().email(),
});

export async function updateUser(formData: FormData) {
  const validatedFields = updateUserSchema.safeParse({
    id: formData.get('id'),
    name: formData.get('name'),
    email: formData.get('email'),
  });

  if (!validatedFields.success) {
    return {
      error: 'Invalid fields',
      issues: validatedFields.error.flatten(),
    };
  }

  try {
    await db.update(users)
      .set(validatedFields.data)
      .where(eq(users.id, validatedFields.data.id));
    
    revalidatePath('/users');
    return { success: true };
  } catch (error) {
    return { error: 'Failed to update user' };
  }
}
```

#### With next-safe-action
```typescript
// lib/safe-action.ts
import { createSafeActionClient } from 'next-safe-action';

export const action = createSafeActionClient({
  handleReturnedServerError(e) {
    if (e instanceof Error) {
      return e.message;
    }
    return 'An unexpected error occurred';
  },
});

// app/actions/create-post.ts
import { action } from '@/lib/safe-action';
import { z } from 'zod';

const schema = z.object({
  title: z.string().min(1).max(100),
  content: z.string().min(1),
});

export const createPost = action
  .schema(schema)
  .action(async ({ parsedInput }) => {
    const post = await db.insert(posts).values(parsedInput);
    revalidatePath('/posts');
    return { post };
  });
```

### Layouts and Templates

#### Root Layout with Providers
```typescript
// app/layout.tsx
import { Inter } from 'next/font/google';
import { ThemeProvider } from '@/components/theme-provider';
import { Toaster } from '@/components/ui/toaster';

const inter = Inter({ subsets: ['latin'] });

export default function RootLayout({
  children,
}: {
  children: React.ReactNode;
}) {
  return (
    <html lang="en" suppressHydrationWarning>
      <body className={inter.className}>
        <ThemeProvider
          attribute="class"
          defaultTheme="system"
          enableSystem
          disableTransitionOnChange
        >
          {children}
          <Toaster />
        </ThemeProvider>
      </body>
    </html>
  );
}
```

#### Nested Layout with Auth
```typescript
// app/(dashboard)/layout.tsx
import { redirect } from 'next/navigation';
import { getServerSession } from '@/lib/auth';
import { DashboardNav } from '@/components/dashboard-nav';

export default async function DashboardLayout({
  children,
}: {
  children: React.ReactNode;
}) {
  const session = await getServerSession();
  
  if (!session) {
    redirect('/login');
  }

  return (
    <div className="flex h-screen">
      <DashboardNav />
      <main className="flex-1 overflow-y-auto">
        {children}
      </main>
    </div>
  );
}
```

### Route Handlers

#### RESTful API Routes
```typescript
// app/api/posts/route.ts
import { NextRequest, NextResponse } from 'next/server';
import { getServerSession } from '@/lib/auth';

export async function GET(request: NextRequest) {
  const session = await getServerSession();
  if (!session) {
    return NextResponse.json({ error: 'Unauthorized' }, { status: 401 });
  }

  const posts = await db.query.posts.findMany({
    where: eq(posts.userId, session.user.id),
  });

  return NextResponse.json(posts);
}

export async function POST(request: NextRequest) {
  const session = await getServerSession();
  if (!session) {
    return NextResponse.json({ error: 'Unauthorized' }, { status: 401 });
  }

  const json = await request.json();
  const post = await db.insert(posts).values({
    ...json,
    userId: session.user.id,
  });

  return NextResponse.json(post, { status: 201 });
}
```

### Error Handling

#### Error Boundary
```typescript
// app/error.tsx
'use client';

import { useEffect } from 'react';
import { Button } from '@/components/ui/button';

export default function Error({
  error,
  reset,
}: {
  error: Error & { digest?: string };
  reset: () => void;
}) {
  useEffect(() => {
    console.error(error);
  }, [error]);

  return (
    <div className="flex h-[50vh] flex-col items-center justify-center">
      <h2 className="text-2xl font-bold">Something went wrong!</h2>
      <Button onClick={reset} className="mt-4">
        Try again
      </Button>
    </div>
  );
}
```

#### Not Found Page
```typescript
// app/not-found.tsx
import Link from 'next/link';
import { Button } from '@/components/ui/button';

export default function NotFound() {
  return (
    <div className="flex h-[50vh] flex-col items-center justify-center">
      <h2 className="text-2xl font-bold">404 - Page Not Found</h2>
      <p className="text-muted-foreground mt-2">
        The page you're looking for doesn't exist.
      </p>
      <Button asChild className="mt-4">
        <Link href="/">Go Home</Link>
      </Button>
    </div>
  );
}
```

## React 18/19 Patterns

### Component Patterns

#### Compound Components
```typescript
// components/tabs.tsx
import { createContext, useContext, useState } from 'react';

const TabsContext = createContext<{
  activeTab: string;
  setActiveTab: (tab: string) => void;
} | null>(null);

export function Tabs({ children, defaultValue }: TabsProps) {
  const [activeTab, setActiveTab] = useState(defaultValue);
  
  return (
    <TabsContext.Provider value={{ activeTab, setActiveTab }}>
      <div className="tabs">{children}</div>
    </TabsContext.Provider>
  );
}

export function TabsList({ children }: TabsListProps) {
  return <div className="tabs-list">{children}</div>;
}

export function TabsTrigger({ value, children }: TabsTriggerProps) {
  const context = useContext(TabsContext);
  if (!context) throw new Error('TabsTrigger must be used within Tabs');
  
  return (
    <button
      className={cn('tab-trigger', {
        'active': context.activeTab === value,
      })}
      onClick={() => context.setActiveTab(value)}
    >
      {children}
    </button>
  );
}

export function TabsContent({ value, children }: TabsContentProps) {
  const context = useContext(TabsContext);
  if (!context) throw new Error('TabsContent must be used within Tabs');
  
  if (context.activeTab !== value) return null;
  
  return <div className="tabs-content">{children}</div>;
}
```

#### Render Props Pattern
```typescript
// components/data-fetcher.tsx
interface DataFetcherProps<T> {
  url: string;
  children: (data: T | null, loading: boolean, error: Error | null) => React.ReactNode;
}

export function DataFetcher<T>({ url, children }: DataFetcherProps<T>) {
  const [data, setData] = useState<T | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<Error | null>(null);

  useEffect(() => {
    fetch(url)
      .then(res => res.json())
      .then(setData)
      .catch(setError)
      .finally(() => setLoading(false));
  }, [url]);

  return <>{children(data, loading, error)}</>;
}

// Usage
<DataFetcher url="/api/users">
  {(users, loading, error) => {
    if (loading) return <Spinner />;
    if (error) return <Error message={error.message} />;
    return <UserList users={users} />;
  }}
</DataFetcher>
```

### Hooks Patterns

#### Custom Hook with Cleanup
```typescript
// hooks/use-websocket.ts
export function useWebSocket(url: string) {
  const [isConnected, setIsConnected] = useState(false);
  const [lastMessage, setLastMessage] = useState<any>(null);
  const ws = useRef<WebSocket | null>(null);

  useEffect(() => {
    ws.current = new WebSocket(url);

    ws.current.onopen = () => setIsConnected(true);
    ws.current.onclose = () => setIsConnected(false);
    ws.current.onmessage = (event) => {
      setLastMessage(JSON.parse(event.data));
    };

    return () => {
      ws.current?.close();
    };
  }, [url]);

  const sendMessage = useCallback((message: any) => {
    if (ws.current?.readyState === WebSocket.OPEN) {
      ws.current.send(JSON.stringify(message));
    }
  }, []);

  return { isConnected, lastMessage, sendMessage };
}
```

#### Debounced State Hook
```typescript
// hooks/use-debounce.ts
export function useDebounce<T>(value: T, delay: number): T {
  const [debouncedValue, setDebouncedValue] = useState(value);

  useEffect(() => {
    const handler = setTimeout(() => {
      setDebouncedValue(value);
    }, delay);

    return () => {
      clearTimeout(handler);
    };
  }, [value, delay]);

  return debouncedValue;
}

// Usage in search
export function SearchInput() {
  const [search, setSearch] = useState('');
  const debouncedSearch = useDebounce(search, 500);
  
  const { data } = useSWR(
    debouncedSearch ? `/api/search?q=${debouncedSearch}` : null
  );

  return (
    <Input
      value={search}
      onChange={(e) => setSearch(e.target.value)}
      placeholder="Search..."
    />
  );
}
```

### Performance Optimization

#### React.memo with Custom Comparison
```typescript
// components/expensive-list.tsx
interface ListItemProps {
  item: {
    id: string;
    name: string;
    selected: boolean;
  };
  onSelect: (id: string) => void;
}

const ListItem = React.memo(
  ({ item, onSelect }: ListItemProps) => {
    console.log('ListItem render:', item.id);
    
    return (
      <div 
        className={cn('list-item', { selected: item.selected })}
        onClick={() => onSelect(item.id)}
      >
        {item.name}
      </div>
    );
  },
  (prevProps, nextProps) => {
    // Custom comparison - only re-render if these specific props change
    return (
      prevProps.item.id === nextProps.item.id &&
      prevProps.item.name === nextProps.item.name &&
      prevProps.item.selected === nextProps.item.selected
    );
  }
);
```

#### useMemo for Expensive Calculations
```typescript
// components/analytics-dashboard.tsx
export function AnalyticsDashboard({ data }: { data: DataPoint[] }) {
  const chartData = useMemo(() => {
    // Expensive data transformation
    return data
      .filter(point => point.value > 0)
      .map(point => ({
        x: new Date(point.timestamp).toLocaleDateString(),
        y: point.value,
        label: point.label,
      }))
      .sort((a, b) => a.x.localeCompare(b.x));
  }, [data]);

  const summary = useMemo(() => {
    const total = data.reduce((sum, point) => sum + point.value, 0);
    const average = total / data.length;
    const max = Math.max(...data.map(p => p.value));
    const min = Math.min(...data.map(p => p.value));
    
    return { total, average, max, min };
  }, [data]);

  return (
    <div>
      <SummaryCards {...summary} />
      <Chart data={chartData} />
    </div>
  );
}
```

### Concurrent Features

#### useTransition for Non-Urgent Updates
```typescript
// components/search-results.tsx
export function SearchResults() {
  const [query, setQuery] = useState('');
  const [results, setResults] = useState<Result[]>([]);
  const [isPending, startTransition] = useTransition();

  const handleSearch = (e: React.ChangeEvent<HTMLInputElement>) => {
    const value = e.target.value;
    setQuery(value); // Urgent update - input field

    startTransition(() => {
      // Non-urgent update - search results
      if (value) {
        fetchResults(value).then(setResults);
      } else {
        setResults([]);
      }
    });
  };

  return (
    <div>
      <Input value={query} onChange={handleSearch} />
      {isPending && <div>Searching...</div>}
      <ResultsList results={results} />
    </div>
  );
}
```

#### useDeferredValue for Performance
```typescript
// components/filtered-list.tsx
export function FilteredList({ items }: { items: Item[] }) {
  const [filter, setFilter] = useState('');
  const deferredFilter = useDeferredValue(filter);
  
  const filteredItems = useMemo(
    () => items.filter(item => 
      item.name.toLowerCase().includes(deferredFilter.toLowerCase())
    ),
    [items, deferredFilter]
  );

  return (
    <div>
      <Input 
        value={filter} 
        onChange={(e) => setFilter(e.target.value)}
        placeholder="Filter items..."
      />
      <div className={filter !== deferredFilter ? 'opacity-50' : ''}>
        {filteredItems.map(item => (
          <ItemCard key={item.id} item={item} />
        ))}
      </div>
    </div>
  );
}
```

## Navigation Patterns

### Programmatic Navigation
```typescript
// components/auth-form.tsx
import { useRouter } from 'next/navigation';

export function LoginForm() {
  const router = useRouter();
  
  const handleSubmit = async (e: FormEvent) => {
    e.preventDefault();
    
    const result = await signIn(credentials);
    if (result.success) {
      router.push('/dashboard');
      router.refresh(); // Refresh server components
    }
  };
}
```

### Link Component Best Practices
```typescript
// components/nav-link.tsx
import Link from 'next/link';
import { usePathname } from 'next/navigation';

export function NavLink({ href, children }: NavLinkProps) {
  const pathname = usePathname();
  const isActive = pathname === href;
  
  return (
    <Link 
      href={href}
      className={cn(
        'nav-link',
        isActive && 'nav-link-active'
      )}
      prefetch={true} // Default, prefetches on hover
    >
      {children}
    </Link>
  );
}
```

### Parallel Routes Navigation
```typescript
// app/@modal/(.)photos/[id]/page.tsx
export default function PhotoModal({ params }: { params: { id: string } }) {
  return (
    <Modal>
      <PhotoDetail id={params.id} />
    </Modal>
  );
}

// components/photo-grid.tsx
import Link from 'next/link';

export function PhotoGrid({ photos }) {
  return (
    <div className="grid">
      {photos.map(photo => (
        <Link 
          key={photo.id} 
          href={`/photos/${photo.id}`}
          className="photo-card"
        >
          <img src={photo.url} alt={photo.title} />
        </Link>
      ))}
    </div>
  );
}
```

## Best Practices Summary

### Do's
- Use Server Components by default
- Fetch data in parallel when possible
- Implement proper error boundaries
- Use Suspense for better UX
- Validate inputs with Zod
- Cache aggressively with proper invalidation
- Use TypeScript for type safety
- Implement proper loading states

### Don'ts
- Don't use 'use client' unnecessarily
- Don't fetch data in client components when server components can do it
- Don't block rendering with synchronous operations
- Don't ignore error handling
- Don't mutate props or state directly
- Don't use useEffect for data fetching in client components
- Don't ignore accessibility
- Don't bundle unnecessary client-side JavaScript