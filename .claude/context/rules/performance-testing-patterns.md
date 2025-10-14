---
description: Performance optimization, testing strategies, monitoring, and logging
alwaysApply: true
---

# Performance & Testing Patterns

Consolidated from: performance-optimization-strategies.mdc, testing-patterns.mdc, monitoring-patterns.mdc, logging-patterns.mdc

## Performance Optimization

### Bundle Size Optimization
```typescript
// next.config.js
module.exports = {
  // Enable SWC minification
  swcMinify: true,
  
  // Optimize images
  images: {
    domains: ['example.com'],
    formats: ['image/avif', 'image/webp'],
  },
  
  // Analyze bundle size
  webpack: (config, { isServer }) => {
    if (process.env.ANALYZE === 'true') {
      const BundleAnalyzerPlugin = require('webpack-bundle-analyzer')
        .BundleAnalyzerPlugin;
      
      config.plugins.push(
        new BundleAnalyzerPlugin({
          analyzerMode: 'static',
          reportFilename: isServer
            ? '../analyze/server.html'
            : './analyze/client.html',
        })
      );
    }
    return config;
  },
  
  // Experimental features
  experimental: {
    optimizePackageImports: [
      '@heroicons/react',
      'date-fns',
      'lodash-es',
      'react-icons',
    ],
  },
};
```

### Dynamic Imports & Code Splitting
```tsx
// Lazy load heavy components
import dynamic from 'next/dynamic';

const RichTextEditor = dynamic(
  () => import('@/components/RichTextEditor'),
  {
    loading: () => <div>Loading editor...</div>,
    ssr: false, // Disable SSR for client-only components
  }
);

const ChartComponent = dynamic(
  () => import('@/components/Analytics/Chart'),
  {
    loading: () => <ChartSkeleton />,
  }
);

// Conditional loading
export function Dashboard() {
  const [showAnalytics, setShowAnalytics] = useState(false);

  return (
    <div>
      <Button onClick={() => setShowAnalytics(true)}>
        Show Analytics
      </Button>
      
      {showAnalytics && (
        <Suspense fallback={<AnalyticsSkeleton />}>
          <ChartComponent />
        </Suspense>
      )}
    </div>
  );
}

// Route-based code splitting (automatic with App Router)
// Each page.tsx is automatically code-split
```

### Image Optimization
```tsx
// Using next/image for optimization
import Image from 'next/image';

export function OptimizedImage({ src, alt }: { src: string; alt: string }) {
  return (
    <Image
      src={src}
      alt={alt}
      width={800}
      height={600}
      // Responsive sizing
      sizes="(max-width: 640px) 100vw, (max-width: 1024px) 50vw, 33vw"
      // Loading strategy
      loading="lazy" // or "eager" for above-the-fold
      // Placeholder while loading
      placeholder="blur"
      blurDataURL={shimmerDataUrl}
      // Quality setting
      quality={85}
      // Format optimization
      formats={['image/avif', 'image/webp']}
    />
  );
}

// Generate shimmer placeholder
const shimmer = (w: number, h: number) => `
<svg width="${w}" height="${h}" version="1.1" xmlns="http://www.w3.org/2000/svg">
  <defs>
    <linearGradient id="g">
      <stop stop-color="#333" offset="20%" />
      <stop stop-color="#222" offset="50%" />
      <stop stop-color="#333" offset="70%" />
    </linearGradient>
  </defs>
  <rect width="${w}" height="${h}" fill="#333" />
  <rect id="r" width="${w}" height="${h}" fill="url(#g)" />
  <animate xlink:href="#r" attributeName="x" from="-${w}" to="${w}" dur="1s" repeatCount="indefinite"  />
</svg>`;

const toBase64 = (str: string) =>
  typeof window === 'undefined'
    ? Buffer.from(str).toString('base64')
    : window.btoa(str);

const shimmerDataUrl = `data:image/svg+xml;base64,${toBase64(shimmer(800, 600))}`;
```

### React Performance Patterns
```tsx
// Memoization for expensive computations
const ExpensiveComponent = memo(({ data }: { data: ComplexData }) => {
  const processedData = useMemo(() => {
    // Expensive computation
    return data.items
      .filter(item => item.active)
      .sort((a, b) => b.score - a.score)
      .map(item => ({
        ...item,
        formattedScore: formatScore(item.score),
        percentile: calculatePercentile(item.score, data.items),
      }));
  }, [data]);

  return <DataVisualization data={processedData} />;
});

// Callback memoization
export function SearchInput({ onSearch }: { onSearch: (q: string) => void }) {
  const [query, setQuery] = useState('');
  
  // Debounced search
  const debouncedSearch = useMemo(
    () => debounce(onSearch, 300),
    [onSearch]
  );

  // Memoize event handler
  const handleChange = useCallback(
    (e: React.ChangeEvent<HTMLInputElement>) => {
      const value = e.target.value;
      setQuery(value);
      debouncedSearch(value);
    },
    [debouncedSearch]
  );

  return <input value={query} onChange={handleChange} />;
}

// Virtual scrolling for long lists
import { FixedSizeList } from 'react-window';

export function VirtualList({ items }: { items: Item[] }) {
  const Row = ({ index, style }: { index: number; style: React.CSSProperties }) => (
    <div style={style}>
      <ItemCard item={items[index]} />
    </div>
  );

  return (
    <FixedSizeList
      height={600}
      itemCount={items.length}
      itemSize={100}
      width="100%"
    >
      {Row}
    </FixedSizeList>
  );
}
```

### Database Query Optimization
```typescript
// Optimize with proper indexes
await db.$executeRaw`
  CREATE INDEX idx_posts_user_created 
  ON posts(user_id, created_at DESC)
  WHERE published = true;
`;

// Use select to limit fields
const users = await db.user.findMany({
  select: {
    id: true,
    name: true,
    email: true,
    // Don't select unused fields
  },
});

// Batch operations
const updatePromises = ids.map(id =>
  db.post.update({
    where: { id },
    data: { viewCount: { increment: 1 } },
  })
);
await db.$transaction(updatePromises);

// Pagination with cursor
const posts = await db.post.findMany({
  take: 20,
  skip: 1,
  cursor: {
    id: lastPostId,
  },
  orderBy: {
    createdAt: 'desc',
  },
});

// Aggregate efficiently
const stats = await db.post.aggregate({
  _count: true,
  _avg: {
    viewCount: true,
  },
  where: {
    published: true,
    createdAt: {
      gte: new Date('2024-01-01'),
    },
  },
});
```

### Caching Strategies
```typescript
// Server-side caching with React cache
import { cache } from 'react';

export const getUser = cache(async (id: string) => {
  const user = await db.user.findUnique({
    where: { id },
    include: { profile: true },
  });
  return user;
});

// Client-side caching with TanStack Query
export function useUser(id: string) {
  return useQuery({
    queryKey: ['user', id],
    queryFn: () => fetchUser(id),
    staleTime: 5 * 60 * 1000, // 5 minutes
    cacheTime: 10 * 60 * 1000, // 10 minutes
  });
}

// Edge caching with headers
export async function GET(request: NextRequest) {
  const data = await fetchData();
  
  return NextResponse.json(data, {
    headers: {
      'Cache-Control': 'public, s-maxage=60, stale-while-revalidate=300',
    },
  });
}

// Incremental Static Regeneration
export const revalidate = 3600; // Revalidate every hour

export default async function Page() {
  const data = await fetchData();
  return <Component data={data} />;
}
```

## Testing Patterns

### Unit Testing with Vitest
```typescript
// vitest.config.ts
import { defineConfig } from 'vitest/config';
import react from '@vitejs/plugin-react';
import path from 'path';

export default defineConfig({
  plugins: [react()],
  test: {
    environment: 'jsdom',
    globals: true,
    setupFiles: ['./test/setup.ts'],
    coverage: {
      reporter: ['text', 'json', 'html'],
      exclude: [
        'node_modules/',
        'test/',
        '**/*.d.ts',
        '**/*.config.*',
        '**/mockData.ts',
      ],
    },
  },
  resolve: {
    alias: {
      '@': path.resolve(__dirname, './src'),
    },
  },
});

// test/setup.ts
import '@testing-library/jest-dom';
import { cleanup } from '@testing-library/react';
import { afterEach } from 'vitest';

afterEach(() => {
  cleanup();
});

// Mock Next.js router
vi.mock('next/navigation', () => ({
  useRouter() {
    return {
      push: vi.fn(),
      replace: vi.fn(),
      prefetch: vi.fn(),
      back: vi.fn(),
    };
  },
  useSearchParams() {
    return new URLSearchParams();
  },
  usePathname() {
    return '/';
  },
}));
```

### Component Testing
```typescript
// components/__tests__/Button.test.tsx
import { render, screen, fireEvent } from '@testing-library/react';
import { Button } from '@/components/ui/button';

describe('Button', () => {
  it('renders with text', () => {
    render(<Button>Click me</Button>);
    expect(screen.getByRole('button')).toHaveTextContent('Click me');
  });

  it('handles click events', () => {
    const handleClick = vi.fn();
    render(<Button onClick={handleClick}>Click me</Button>);
    
    fireEvent.click(screen.getByRole('button'));
    expect(handleClick).toHaveBeenCalledTimes(1);
  });

  it('can be disabled', () => {
    render(<Button disabled>Click me</Button>);
    expect(screen.getByRole('button')).toBeDisabled();
  });

  it('shows loading state', () => {
    render(<Button loading>Click me</Button>);
    expect(screen.getByRole('button')).toHaveAttribute('aria-busy', 'true');
    expect(screen.getByTestId('spinner')).toBeInTheDocument();
  });
});

// Custom hooks testing
import { renderHook, act } from '@testing-library/react';
import { useCounter } from '@/hooks/useCounter';

describe('useCounter', () => {
  it('increments counter', () => {
    const { result } = renderHook(() => useCounter());
    
    expect(result.current.count).toBe(0);
    
    act(() => {
      result.current.increment();
    });
    
    expect(result.current.count).toBe(1);
  });
});
```

### Integration Testing
```typescript
// __tests__/api/users.test.ts
import { createMocks } from 'node-mocks-http';
import { GET, POST } from '@/app/api/users/route';
import { db } from '@/lib/db';

vi.mock('@/lib/db', () => ({
  db: {
    user: {
      findMany: vi.fn(),
      create: vi.fn(),
    },
  },
}));

describe('/api/users', () => {
  beforeEach(() => {
    vi.clearAllMocks();
  });

  describe('GET', () => {
    it('returns users list', async () => {
      const mockUsers = [
        { id: '1', name: 'John', email: 'john@example.com' },
        { id: '2', name: 'Jane', email: 'jane@example.com' },
      ];

      db.user.findMany.mockResolvedValue(mockUsers);

      const { req } = createMocks({
        method: 'GET',
      });

      const response = await GET(req);
      const json = await response.json();

      expect(response.status).toBe(200);
      expect(json).toEqual(mockUsers);
    });

    it('handles errors', async () => {
      db.user.findMany.mockRejectedValue(new Error('Database error'));

      const { req } = createMocks({
        method: 'GET',
      });

      const response = await GET(req);

      expect(response.status).toBe(500);
      expect(await response.json()).toEqual({
        error: 'Internal Server Error',
      });
    });
  });
});
```

### E2E Testing with Playwright
```typescript
// playwright.config.ts
import { defineConfig, devices } from '@playwright/test';

export default defineConfig({
  testDir: './e2e',
  fullyParallel: true,
  forbidOnly: !!process.env.CI,
  retries: process.env.CI ? 2 : 0,
  workers: process.env.CI ? 1 : undefined,
  reporter: 'html',
  use: {
    baseURL: 'http://localhost:3000',
    trace: 'on-first-retry',
    screenshot: 'only-on-failure',
  },
  projects: [
    {
      name: 'chromium',
      use: { ...devices['Desktop Chrome'] },
    },
    {
      name: 'firefox',
      use: { ...devices['Desktop Firefox'] },
    },
    {
      name: 'webkit',
      use: { ...devices['Desktop Safari'] },
    },
    {
      name: 'Mobile Chrome',
      use: { ...devices['Pixel 5'] },
    },
  ],
  webServer: {
    command: 'npm run dev',
    port: 3000,
    reuseExistingServer: !process.env.CI,
  },
});

// e2e/auth.spec.ts
import { test, expect } from '@playwright/test';

test.describe('Authentication', () => {
  test('user can sign up', async ({ page }) => {
    await page.goto('/sign-up');
    
    // Fill form
    await page.fill('input[name="email"]', 'test@example.com');
    await page.fill('input[name="password"]', 'password123');
    await page.fill('input[name="confirmPassword"]', 'password123');
    
    // Submit
    await page.click('button[type="submit"]');
    
    // Wait for redirect
    await page.waitForURL('/onboarding');
    
    // Check welcome message
    await expect(page.locator('h1')).toContainText('Welcome');
  });

  test('user can log in', async ({ page }) => {
    await page.goto('/login');
    
    await page.fill('input[name="email"]', 'existing@example.com');
    await page.fill('input[name="password"]', 'password123');
    
    await page.click('button[type="submit"]');
    
    await page.waitForURL('/dashboard');
    await expect(page.locator('[data-testid="user-menu"]')).toBeVisible();
  });
});

// e2e/helpers/auth.ts
export async function loginAs(page: Page, email: string, password: string) {
  await page.goto('/login');
  await page.fill('input[name="email"]', email);
  await page.fill('input[name="password"]', password);
  await page.click('button[type="submit"]');
  await page.waitForURL('/dashboard');
}
```

## Monitoring & Observability

### Error Monitoring with Sentry
```typescript
// sentry.client.config.ts
import * as Sentry from '@sentry/nextjs';

Sentry.init({
  dsn: process.env.NEXT_PUBLIC_SENTRY_DSN,
  environment: process.env.NODE_ENV,
  tracesSampleRate: process.env.NODE_ENV === 'production' ? 0.1 : 1.0,
  debug: false,
  replaysOnErrorSampleRate: 1.0,
  replaysSessionSampleRate: 0.1,
  integrations: [
    new Sentry.Replay({
      maskAllText: true,
      blockAllMedia: true,
    }),
  ],
  beforeSend(event, hint) {
    // Filter out specific errors
    if (event.exception && event.exception.values?.[0]?.type === 'NetworkError') {
      return null;
    }
    return event;
  },
});

// Error boundary with Sentry
export function ErrorBoundary({ children }: { children: React.ReactNode }) {
  return (
    <Sentry.ErrorBoundary
      fallback={({ error, resetError }) => (
        <div className="error-fallback">
          <h2>Something went wrong</h2>
          <pre>{error.message}</pre>
          <button onClick={resetError}>Try again</button>
        </div>
      )}
      showDialog
    >
      {children}
    </Sentry.ErrorBoundary>
  );
}

// Custom error tracking
export function trackError(error: Error, context?: Record<string, any>) {
  Sentry.captureException(error, {
    tags: {
      section: 'user-action',
    },
    contexts: {
      custom: context,
    },
  });
}
```

### Performance Monitoring
```typescript
// lib/performance.ts
export function measurePerformance(name: string, fn: () => void) {
  if (typeof window === 'undefined') return fn();
  
  performance.mark(`${name}-start`);
  fn();
  performance.mark(`${name}-end`);
  performance.measure(name, `${name}-start`, `${name}-end`);
  
  const measure = performance.getEntriesByName(name)[0];
  console.log(`${name}: ${measure.duration}ms`);
  
  // Send to analytics
  if (window.analytics) {
    window.analytics.track('Performance', {
      metric: name,
      duration: measure.duration,
    });
  }
}

// Web Vitals reporting
export function reportWebVitals(metric: any) {
  const body = JSON.stringify({
    name: metric.name,
    value: metric.value,
    id: metric.id,
    label: metric.label,
  });

  // Send to analytics endpoint
  if (navigator.sendBeacon) {
    navigator.sendBeacon('/api/analytics', body);
  }
}

// Component performance tracking
export function PerformanceWrapper({ 
  children, 
  name 
}: { 
  children: React.ReactNode;
  name: string;
}) {
  useEffect(() => {
    const startTime = performance.now();
    
    return () => {
      const endTime = performance.now();
      const duration = endTime - startTime;
      
      console.log(`Component ${name} rendered in ${duration}ms`);
      
      // Track slow renders
      if (duration > 100) {
        trackError(new Error(`Slow render: ${name}`), {
          duration,
          component: name,
        });
      }
    };
  }, [name]);

  return <>{children}</>;
}
```

### Logging Patterns
```typescript
// lib/logger.ts
import pino from 'pino';

const logger = pino({
  level: process.env.LOG_LEVEL || 'info',
  transport:
    process.env.NODE_ENV === 'development'
      ? {
          target: 'pino-pretty',
          options: {
            colorize: true,
          },
        }
      : undefined,
  serializers: {
    req: (req) => ({
      method: req.method,
      url: req.url,
      headers: req.headers,
    }),
    res: (res) => ({
      statusCode: res.statusCode,
    }),
    err: pino.stdSerializers.err,
  },
});

// Structured logging
export function logRequest(req: NextRequest, res: NextResponse) {
  logger.info({
    req,
    res,
    duration: Date.now() - req.startTime,
    userId: req.userId,
  }, 'Request completed');
}

// Context-aware logging
export class ContextLogger {
  constructor(private context: Record<string, any>) {}

  info(message: string, data?: Record<string, any>) {
    logger.info({ ...this.context, ...data }, message);
  }

  error(message: string, error?: Error, data?: Record<string, any>) {
    logger.error({ ...this.context, ...data, err: error }, message);
  }

  child(context: Record<string, any>) {
    return new ContextLogger({ ...this.context, ...context });
  }
}

// Usage
const requestLogger = new ContextLogger({
  requestId: crypto.randomUUID(),
  userId: session?.user?.id,
});

requestLogger.info('Processing payment', { amount: 100 });
```

### Analytics Integration
```typescript
// lib/analytics.ts
import { Analytics } from '@segment/analytics-node';
import posthog from 'posthog-js';

// Server-side analytics
const analytics = new Analytics({
  writeKey: process.env.SEGMENT_WRITE_KEY!,
});

export async function trackServerEvent(
  userId: string,
  event: string,
  properties?: Record<string, any>
) {
  await analytics.track({
    userId,
    event,
    properties,
    context: {
      app: {
        name: 'MyApp',
        version: process.env.APP_VERSION,
      },
    },
  });
}

// Client-side analytics
export function initAnalytics() {
  if (typeof window !== 'undefined') {
    posthog.init(process.env.NEXT_PUBLIC_POSTHOG_KEY!, {
      api_host: process.env.NEXT_PUBLIC_POSTHOG_HOST,
      capture_pageview: false, // Manual pageview tracking
      persistence: 'localStorage',
    });
  }
}

// Custom hooks for tracking
export function usePageView() {
  const pathname = usePathname();
  const searchParams = useSearchParams();

  useEffect(() => {
    if (pathname) {
      posthog.capture('$pageview', {
        $current_url: pathname + searchParams.toString(),
      });
    }
  }, [pathname, searchParams]);
}

export function useTrackEvent() {
  return useCallback((event: string, properties?: Record<string, any>) => {
    posthog.capture(event, properties);
  }, []);
}
```

## Best Practices

### Performance Checklist
- [ ] Enable Next.js compiler optimizations
- [ ] Implement proper code splitting
- [ ] Optimize images with next/image
- [ ] Use React.memo for expensive components
- [ ] Implement virtual scrolling for long lists
- [ ] Add database indexes
- [ ] Enable caching headers
- [ ] Monitor bundle size
- [ ] Implement progressive enhancement
- [ ] Use Web Workers for heavy computations
- [ ] Optimize third-party scripts
- [ ] Implement resource hints (prefetch, preconnect)

### Testing Best Practices
- [ ] Write tests alongside code
- [ ] Test user behavior, not implementation
- [ ] Mock external dependencies
- [ ] Use test fixtures and factories
- [ ] Implement visual regression testing
- [ ] Test error scenarios
- [ ] Test accessibility
- [ ] Run tests in CI/CD
- [ ] Maintain good test coverage (>80%)
- [ ] Use meaningful test descriptions

### Monitoring Best Practices
- [ ] Track errors with context
- [ ] Monitor performance metrics
- [ ] Set up alerts for anomalies
- [ ] Log structured data
- [ ] Implement distributed tracing
- [ ] Monitor business metrics
- [ ] Track user behavior
- [ ] Set up dashboards
- [ ] Regular performance audits
- [ ] Monitor third-party service health