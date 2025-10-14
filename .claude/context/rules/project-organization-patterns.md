---
description: Project organization and structure guidelines for Next.js applications
globs: **/*.{ts,tsx}
alwaysApply: false
---

You are an expert Next.js architect with deep knowledge of project organization, file structure, and code organization in modern Next.js applications.

# Project Organization Guidelines

## Overview
This document outlines the best practices and patterns for organizing a Next.js 15+ application. It covers directory structure, file naming conventions, code organization, and project architecture to ensure consistency and maintainability.

## 1. Directory Structure

### Root-Level Organization
```
/
├── src/                  # All application code
│   ├── app/              # Next.js App Router pages
│   ├── components/       # React components
│   ├── lib/              # Utility functions and libraries
│   ├── styles/           # Global styles
│   ├── types/            # TypeScript type definitions
│   ├── utils/            # Utility functions
│   ├── hooks/            # Custom hooks
│   ├── contexts/         # React contexts
│   └── supabase-clients/ # Supabase client configurations
├── public/               # Static assets
├── supabase/             # Supabase configurations and migrations
├── emails/               # Email templates
├── e2e/                  # End-to-end tests
├── .ai-rules/            # AI assistant rules and configurations
└── .claude/              # Claude Code configuration and patterns
```

### src/app Directory
```
src/app/
├── api/                    # API route handlers
│   └── [domain]/
│       └── route.ts        # Route handler implementation
├── (dynamic-pages)/        # Dynamic routes grouped
│   └── [slug]/
│       ├── page.tsx        # Page component
│       └── layout.tsx      # Layout component
├── (external-pages)/       # External/public pages grouped
├── layout.tsx              # Root layout
├── page.tsx                # Home page
├── error.tsx               # Error boundary
└── not-found.tsx           # 404 page
```

### src/components Directory
```
src/components/
├── ui/                     # Shadcn UI components
├── form-components/        # Form-related components
├── auth-form-components/   # Auth-specific components
├── [domain]/               # Domain-specific components
└── [feature]/              # Feature-specific components
```

### src/lib Directory
```
src/lib/
├── database.types.ts       # Supabase database types
├── utils.ts                # General utilities
├── safe-action.ts          # Server action utilities
└── [domain]/               # Domain-specific utilities
```

## 2. File Naming Conventions

### General Rules
- Use kebab-case for directory names: `auth-form-components`
- Use PascalCase for component files: `UserProfile.tsx`
- Use camelCase for utility files: `formatDate.ts`
- Use kebab-case for page routes: `user-profile`
- Append `.test.ts` or `.spec.ts` for test files
- Append `.d.ts` for type declaration files

### Component Files
- Use PascalCase for component files: `Button.tsx`, `UserCard.tsx`
- Use index.ts files for re-exporting components from directories
- Group related components in directories: `UserProfile/index.ts`, `UserProfile/UserAvatar.tsx`

```typescript
// Example component file structure
// src/components/UserProfile/index.ts
export { UserProfile } from './UserProfile';

// src/components/UserProfile/UserProfile.tsx
import { UserAvatar } from './UserAvatar';
import { UserInfo } from './UserInfo';

export function UserProfile() {
  // Component implementation
}
```

### Utility Files
- Use descriptive names for utility files: `formatCurrency.ts`, `dateUtils.ts`
- Group related utilities in directories with index.ts exports
- Use domain prefixes for specialized utilities: `auth-utils.ts`, `form-utils.ts`

```typescript
// Example utility file structure
// src/utils/date/index.ts
export { formatDate } from './formatDate';
export { calculateDateDifference } from './calculateDateDifference';

// src/utils/date/formatDate.ts
export function formatDate(date: Date, format: string): string {
  // Implementation
}
```

## 3. Code Organization

### Component Organization
- Split large components into smaller, focused components
- Keep component files under 300 lines
- Co-locate related components in feature directories
- Use composition over prop drilling

```typescript
// Example component organization
// src/components/Dashboard/index.ts
export { Dashboard } from './Dashboard';

// src/components/Dashboard/Dashboard.tsx
import { DashboardHeader } from './DashboardHeader';
import { DashboardContent } from './DashboardContent';
import { DashboardSidebar } from './DashboardSidebar';

export function Dashboard() {
  return (
    <div className="dashboard-container">
      <DashboardHeader />
      <div className="dashboard-layout">
        <DashboardSidebar />
        <DashboardContent />
      </div>
    </div>
  );
}
```

### Page Organization
- Keep page components simple
- Fetch data in page components or layouts
- Handle errors at the page or layout level
- Delegate UI rendering to components

```typescript
// Example page organization
// src/app/dashboard/page.tsx
import { Dashboard } from '@/components/Dashboard';
import { getDashboardData } from '@/rsc-data/dashboard';

export default async function DashboardPage() {
  const dashboardData = await getDashboardData();
  
  return <Dashboard data={dashboardData} />;
}

// src/app/dashboard/error.tsx
"use client";

import { ErrorDisplay } from '@/components/ErrorDisplay';

export default function DashboardError({
  error,
  reset,
}: {
  error: Error;
  reset: () => void;
}) {
  return <ErrorDisplay error={error} reset={reset} />;
}
```

## 4. Module Imports and Exports

### Import Order
- React and Next.js imports first
- External libraries imports next
- Internal absolute imports next
- Internal relative imports last
- Separate import groups with blank lines

```typescript
// Example import order
import { useState, useEffect } from 'react';
import { useRouter } from 'next/navigation';

import { zodResolver } from '@hookform/resolvers/zod';
import { useForm } from 'react-hook-form';

import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { createUserAction } from '@/data/user/actions';

import { UserSchema } from './schema';
import { ProfileSection } from './ProfileSection';
```

### Export Patterns
- Use named exports by default
- Use index.ts files for re-exporting
- Limit default exports to page components
- Export types alongside their implementations

```typescript
// Example export patterns
// src/components/ui/index.ts
export { Button } from './Button';
export { Input } from './Input';
export { Select } from './Select';

// src/components/ui/Button.tsx
export interface ButtonProps {
  // Props definition
}

export function Button(props: ButtonProps) {
  // Implementation
}
```

## 5. Data Fetching Organization

### Server Component Data Fetching
- Keep data fetching logic in `rsc-data` directory
- Organize by domain/entity
- Use consistent error handling
- Return fully typed data

```typescript
// Example data fetching organization
// src/rsc-data/dashboard/index.ts
export { getDashboardData } from './getDashboardData';

// src/rsc-data/dashboard/getDashboardData.ts
import { createSupabaseUserServerComponentClient } from '@/supabase-clients/user/createSupabaseUserServerComponentClient';
import type { DashboardData } from '@/types/dashboard';

export async function getDashboardData(): Promise<DashboardData> {
  const supabase = await createSupabaseUserServerComponentClient();
  
  // Implementation to fetch dashboard data
  
  return data; // Fully typed return value
}
```

### Server Actions
- Keep server actions in `data` directory
- Organize by domain/entity and action type
- Use safe-action for validation and type safety
- Handle errors consistently

```typescript
// Example server action organization
// src/data/user/index.ts
export { createUserAction, updateUserAction, deleteUserAction } from './actions';

// src/data/user/actions.ts
"use server";

import { authActionClient } from '@/lib/safe-action';
import { createUserSchema, updateUserSchema } from './schemas';

export const createUserAction = authActionClient
  .schema(createUserSchema)
  .action(async ({ parsedInput, ctx }) => {
    // Implementation
  });

export const updateUserAction = authActionClient
  .schema(updateUserSchema)
  .action(async ({ parsedInput, ctx }) => {
    // Implementation
  });
```

## 6. State Management

### Local State
- Use React's useState and useReducer for local component state
- Co-locate state with the components that use it
- Lift state up when needed by multiple components

### Global State
- Use React Context for shared application state
- Keep contexts focused on specific domains
- Provide proper TypeScript types for context values

```typescript
// Example context organization
// src/contexts/AuthContext.tsx
"use client";

import { createContext, useContext, useState, useEffect } from 'react';
import type { User } from '@supabase/supabase-js';

interface AuthContextValue {
  user: User | null;
  loading: boolean;
}

const AuthContext = createContext<AuthContextValue | undefined>(undefined);

export function AuthProvider({ children }: { children: React.ReactNode }) {
  // Implementation
  
  return (
    <AuthContext.Provider value={{ user, loading }}>
      {children}
    </AuthContext.Provider>
  );
}

export function useAuth() {
  const context = useContext(AuthContext);
  
  if (context === undefined) {
    throw new Error('useAuth must be used within an AuthProvider');
  }
  
  return context;
}
```

## 7. Configuration Files

### Environment Variables
- Use `.env.local` for local environment variables
- Document required variables in `.env.example`
- Type environment variables in `environment.d.ts`
- Group related variables with common prefixes

```typescript
// Example environment.d.ts
declare namespace NodeJS {
  interface ProcessEnv {
    NODE_ENV: 'development' | 'production' | 'test';
    NEXT_PUBLIC_SUPABASE_URL: string;
    NEXT_PUBLIC_SUPABASE_ANON_KEY: string;
    SUPABASE_SERVICE_ROLE_KEY: string;
    // Other variables
  }
}
```

### External Service Configs
- Keep service configurations in dedicated files
- Store configuration in `src/lib/[service]` directories
- Use environment variables for sensitive values
- Document configuration options

```typescript
// Example configuration organization
// src/lib/sentry/index.ts
export const sentryConfig = {
  dsn: process.env.NEXT_PUBLIC_SENTRY_DSN,
  environment: process.env.NODE_ENV,
  tracesSampleRate: process.env.NODE_ENV === 'production' ? 0.2 : 1.0,
};

// src/lib/stripe/index.ts
import Stripe from 'stripe';

export const stripe = new Stripe(process.env.STRIPE_SECRET_KEY!, {
  apiVersion: '2022-11-15',
  appInfo: {
    name: 'My App',
    version: '1.0.0',
  },
});
```

## 8. Testing Organization

### Unit Tests
- Co-locate test files with the code they test
- Use `.test.ts` or `.spec.ts` suffixes
- Group tests by feature or component
- Use descriptive test names

```typescript
// Example test organization
// src/utils/formatCurrency.test.ts
import { formatCurrency } from './formatCurrency';

describe('formatCurrency', () => {
  it('formats USD correctly', () => {
    expect(formatCurrency(1000, 'USD')).toBe('$1,000.00');
  });
  
  it('formats EUR correctly', () => {
    expect(formatCurrency(1000, 'EUR')).toBe('€1,000.00');
  });
});
```

### Integration and E2E Tests
- Keep integration tests in a `__tests__` directory
- Keep E2E tests in the `e2e` directory
- Organize tests by feature or user flow
- Use descriptive file names

```typescript
// Example E2E test organization
// e2e/auth/signup.spec.ts
import { test, expect } from '@playwright/test';

test.describe('Sign Up', () => {
  test('user can sign up with valid credentials', async ({ page }) => {
    // Test implementation
  });
  
  test('shows validation errors with invalid data', async ({ page }) => {
    // Test implementation
  });
});
```

## 9. Documentation

### Code Comments
- Use JSDoc comments for functions, interfaces, and types
- Add inline comments for complex logic
- Document workarounds and edge cases

```typescript
/**
 * Formats a number as a currency string.
 * 
 * @param amount - The amount to format
 * @param currency - The currency code (ISO 4217)
 * @returns The formatted currency string
 */
export function formatCurrency(amount: number, currency: string): string {
  // Implementation
}
```

### README Files
- Include a root README.md with project overview
- Add README.md files to complex directories
- Document setup steps, dependencies, and usage

## 10. Do's and Don'ts

### Do ✅
- Follow consistent naming conventions
- Keep directory structure flat when possible
- Co-locate related code
- Use index.ts files for clean exports
- Document complex code and configurations
- Split large files into smaller ones
- Use appropriate abstractions

### Don't ❌
- Create deeply nested directory structures
- Mix different concerns in the same file
- Create overly large files
- Duplicate code across the project
- Use ambiguous or abbreviated names
- Break established project patterns
- Mix unrelated functionality in the same directory

---

Follow these project organization guidelines consistently to ensure a maintainable, scalable, and developer-friendly codebase for your Next.js application.