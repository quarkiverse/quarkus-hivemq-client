---
description: TypeScript best practices and advanced patterns
alwaysApply: true
---

# TypeScript Best Practices

Consolidated from: typescript-best-practices.mdc

## Type Definitions

### Basic Types and Interfaces
```typescript
// Prefer interfaces for objects
interface User {
  id: string;
  name: string;
  email: string;
  role: 'admin' | 'user' | 'guest';
  createdAt: Date;
  metadata?: Record<string, unknown>;
}

// Use type for unions, intersections, and primitives
type ID = string | number;
type Status = 'pending' | 'active' | 'inactive';
type UserWithPosts = User & { posts: Post[] };

// Enum alternatives - prefer const objects
const UserRole = {
  ADMIN: 'admin',
  USER: 'user',
  GUEST: 'guest',
} as const;

type UserRole = typeof UserRole[keyof typeof UserRole];
```

### Generic Types
```typescript
// Generic interfaces
interface ApiResponse<T> {
  data: T;
  error: string | null;
  status: number;
  timestamp: Date;
}

// Generic functions
function getProperty<T, K extends keyof T>(obj: T, key: K): T[K] {
  return obj[key];
}

// Generic constraints
interface Identifiable {
  id: string | number;
}

function findById<T extends Identifiable>(
  items: T[],
  id: T['id']
): T | undefined {
  return items.find(item => item.id === id);
}
```

### Utility Types
```typescript
// Partial - all properties optional
type PartialUser = Partial<User>;

// Required - all properties required
type RequiredUser = Required<User>;

// Pick - select properties
type UserCredentials = Pick<User, 'email' | 'password'>;

// Omit - exclude properties
type PublicUser = Omit<User, 'password' | 'internalId'>;

// Record - object with specific keys
type UsersByRole = Record<UserRole, User[]>;

// Readonly - immutable
type ReadonlyUser = Readonly<User>;

// Custom utility types
type Nullable<T> = T | null;
type Optional<T> = T | undefined;
type AsyncReturnType<T extends (...args: any) => Promise<any>> = 
  T extends (...args: any) => Promise<infer R> ? R : never;
```

## Component Typing

### Function Components
```typescript
// Basic FC typing
interface ButtonProps {
  variant?: 'primary' | 'secondary' | 'ghost';
  size?: 'sm' | 'md' | 'lg';
  disabled?: boolean;
  onClick?: (event: React.MouseEvent<HTMLButtonElement>) => void;
  children: React.ReactNode;
}

export function Button({ 
  variant = 'primary',
  size = 'md',
  disabled = false,
  onClick,
  children 
}: ButtonProps) {
  return (
    <button
      className={cn(
        'button',
        `button-${variant}`,
        `button-${size}`
      )}
      disabled={disabled}
      onClick={onClick}
    >
      {children}
    </button>
  );
}
```

### Generic Components
```typescript
// Generic Select component
interface SelectOption<T> {
  value: T;
  label: string;
  disabled?: boolean;
}

interface SelectProps<T> {
  options: SelectOption<T>[];
  value: T;
  onChange: (value: T) => void;
  placeholder?: string;
}

export function Select<T extends string | number>({
  options,
  value,
  onChange,
  placeholder
}: SelectProps<T>) {
  return (
    <select 
      value={value} 
      onChange={(e) => onChange(e.target.value as T)}
    >
      {placeholder && <option value="">{placeholder}</option>}
      {options.map(option => (
        <option 
          key={option.value} 
          value={option.value}
          disabled={option.disabled}
        >
          {option.label}
        </option>
      ))}
    </select>
  );
}
```

### Component with ForwardRef
```typescript
// ForwardRef with proper typing
interface InputProps extends React.InputHTMLAttributes<HTMLInputElement> {
  label?: string;
  error?: string;
}

export const Input = React.forwardRef<HTMLInputElement, InputProps>(
  ({ label, error, className, ...props }, ref) => {
    return (
      <div className="input-wrapper">
        {label && <label>{label}</label>}
        <input
          ref={ref}
          className={cn('input', error && 'input-error', className)}
          {...props}
        />
        {error && <span className="error-message">{error}</span>}
      </div>
    );
  }
);

Input.displayName = 'Input';
```

### Polymorphic Components
```typescript
// Component that can render as different elements
type PolymorphicProps<E extends React.ElementType> = {
  as?: E;
  children?: React.ReactNode;
} & React.ComponentPropsWithoutRef<E>;

export function Box<E extends React.ElementType = 'div'>({
  as,
  children,
  ...props
}: PolymorphicProps<E>) {
  const Component = as || 'div';
  return <Component {...props}>{children}</Component>;
}

// Usage
<Box as="section" className="container">Content</Box>
<Box as="button" onClick={handleClick}>Click me</Box>
```

## Hooks Typing

### Custom Hooks
```typescript
// Basic custom hook
function useLocalStorage<T>(
  key: string,
  initialValue: T
): [T, (value: T | ((prev: T) => T)) => void] {
  const [storedValue, setStoredValue] = useState<T>(() => {
    try {
      const item = window.localStorage.getItem(key);
      return item ? JSON.parse(item) : initialValue;
    } catch (error) {
      console.error(error);
      return initialValue;
    }
  });

  const setValue = (value: T | ((prev: T) => T)) => {
    try {
      const valueToStore = value instanceof Function ? value(storedValue) : value;
      setStoredValue(valueToStore);
      window.localStorage.setItem(key, JSON.stringify(valueToStore));
    } catch (error) {
      console.error(error);
    }
  };

  return [storedValue, setValue];
}
```

### Hook with Complex Return Types
```typescript
// API hook with loading states
interface UseApiResult<T> {
  data: T | null;
  error: Error | null;
  loading: boolean;
  refetch: () => Promise<void>;
}

function useApi<T>(url: string): UseApiResult<T> {
  const [data, setData] = useState<T | null>(null);
  const [error, setError] = useState<Error | null>(null);
  const [loading, setLoading] = useState(true);

  const fetchData = useCallback(async () => {
    try {
      setLoading(true);
      setError(null);
      const response = await fetch(url);
      if (!response.ok) throw new Error(response.statusText);
      const json = await response.json();
      setData(json);
    } catch (err) {
      setError(err instanceof Error ? err : new Error('Unknown error'));
    } finally {
      setLoading(false);
    }
  }, [url]);

  useEffect(() => {
    fetchData();
  }, [fetchData]);

  return { data, error, loading, refetch: fetchData };
}
```

## Form and Event Typing

### Form Events
```typescript
// Form submission
interface LoginForm {
  email: string;
  password: string;
  remember: boolean;
}

function LoginForm() {
  const handleSubmit = (e: React.FormEvent<HTMLFormElement>) => {
    e.preventDefault();
    const formData = new FormData(e.currentTarget);
    const data: LoginForm = {
      email: formData.get('email') as string,
      password: formData.get('password') as string,
      remember: formData.get('remember') === 'on',
    };
    // Process form data
  };

  const handleInputChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const { name, value, type, checked } = e.target;
    const inputValue = type === 'checkbox' ? checked : value;
    // Handle change
  };

  return (
    <form onSubmit={handleSubmit}>
      <input name="email" type="email" onChange={handleInputChange} />
      <input name="password" type="password" onChange={handleInputChange} />
      <input name="remember" type="checkbox" onChange={handleInputChange} />
      <button type="submit">Login</button>
    </form>
  );
}
```

### Event Handlers
```typescript
// Mouse events
const handleClick = (e: React.MouseEvent<HTMLButtonElement>) => {
  e.preventDefault();
  console.log(e.currentTarget.dataset.id);
};

// Keyboard events
const handleKeyPress = (e: React.KeyboardEvent<HTMLInputElement>) => {
  if (e.key === 'Enter') {
    e.preventDefault();
    submitForm();
  }
};

// Touch events
const handleTouch = (e: React.TouchEvent<HTMLDivElement>) => {
  const touch = e.touches[0];
  console.log(touch.clientX, touch.clientY);
};

// Drag events
const handleDragStart = (e: React.DragEvent<HTMLDivElement>) => {
  e.dataTransfer.setData('text/plain', e.currentTarget.id);
};
```

## API and Async Typing

### API Response Types
```typescript
// Success/Error union types
type ApiResult<T> = 
  | { success: true; data: T }
  | { success: false; error: string };

async function fetchUser(id: string): Promise<ApiResult<User>> {
  try {
    const response = await fetch(`/api/users/${id}`);
    if (!response.ok) {
      return { success: false, error: response.statusText };
    }
    const data = await response.json();
    return { success: true, data };
  } catch (error) {
    return { 
      success: false, 
      error: error instanceof Error ? error.message : 'Unknown error' 
    };
  }
}

// Usage with type narrowing
const result = await fetchUser('123');
if (result.success) {
  console.log(result.data.name); // TypeScript knows data exists
} else {
  console.error(result.error); // TypeScript knows error exists
}
```

### Promise Types
```typescript
// Async function types
type AsyncFunction<T = void> = () => Promise<T>;
type AsyncFunctionWithArgs<TArgs, TReturn = void> = (args: TArgs) => Promise<TReturn>;

// Promise utilities
type PromiseType<T> = T extends Promise<infer U> ? U : never;

// Error handling
class ApiError extends Error {
  constructor(
    public statusCode: number,
    message: string,
    public code?: string
  ) {
    super(message);
    this.name = 'ApiError';
  }
}

async function apiCall<T>(fn: () => Promise<T>): Promise<T> {
  try {
    return await fn();
  } catch (error) {
    if (error instanceof ApiError) {
      // Handle API errors
      console.error(`API Error ${error.statusCode}: ${error.message}`);
    }
    throw error;
  }
}
```

## Database and Model Types

### Database Schema Types
```typescript
// Generated from database schema
interface Database {
  public: {
    Tables: {
      users: {
        Row: {
          id: string;
          email: string;
          name: string | null;
          created_at: string;
          updated_at: string;
        };
        Insert: {
          id?: string;
          email: string;
          name?: string | null;
          created_at?: string;
          updated_at?: string;
        };
        Update: {
          id?: string;
          email?: string;
          name?: string | null;
          updated_at?: string;
        };
      };
      posts: {
        Row: {
          id: string;
          user_id: string;
          title: string;
          content: string;
          published: boolean;
          created_at: string;
        };
        Insert: {
          id?: string;
          user_id: string;
          title: string;
          content: string;
          published?: boolean;
          created_at?: string;
        };
        Update: {
          title?: string;
          content?: string;
          published?: boolean;
        };
      };
    };
  };
}

// Type helpers
type Tables<T extends keyof Database['public']['Tables']> = 
  Database['public']['Tables'][T]['Row'];

type InsertTables<T extends keyof Database['public']['Tables']> = 
  Database['public']['Tables'][T]['Insert'];

type UpdateTables<T extends keyof Database['public']['Tables']> = 
  Database['public']['Tables'][T]['Update'];

// Usage
type User = Tables<'users'>;
type NewUser = InsertTables<'users'>;
type UserUpdate = UpdateTables<'users'>;
```

### Query Builder Types
```typescript
// Type-safe query builder
interface QueryBuilder<T> {
  select<K extends keyof T>(columns: K[]): QueryBuilder<Pick<T, K>>;
  where<K extends keyof T>(column: K, value: T[K]): QueryBuilder<T>;
  orderBy<K extends keyof T>(column: K, direction?: 'asc' | 'desc'): QueryBuilder<T>;
  limit(count: number): QueryBuilder<T>;
  execute(): Promise<T[]>;
}

// Usage with type inference
const users = await db
  .from<User>('users')
  .select(['id', 'name', 'email'])
  .where('email', 'user@example.com')
  .orderBy('created_at', 'desc')
  .limit(10)
  .execute();
// Type of users is Pick<User, 'id' | 'name' | 'email'>[]
```

## Advanced Patterns

### Discriminated Unions
```typescript
// Action types with discriminated unions
type Action =
  | { type: 'SET_USER'; payload: User }
  | { type: 'LOGOUT' }
  | { type: 'SET_LOADING'; payload: boolean }
  | { type: 'SET_ERROR'; payload: string };

function reducer(state: State, action: Action): State {
  switch (action.type) {
    case 'SET_USER':
      return { ...state, user: action.payload }; // payload is User
    case 'LOGOUT':
      return { ...state, user: null };
    case 'SET_LOADING':
      return { ...state, loading: action.payload }; // payload is boolean
    case 'SET_ERROR':
      return { ...state, error: action.payload }; // payload is string
    default:
      return state;
  }
}
```

### Template Literal Types
```typescript
// CSS unit types
type CSSUnit = 'px' | 'em' | 'rem' | '%' | 'vh' | 'vw';
type CSSValue = `${number}${CSSUnit}`;

interface StyleProps {
  width?: CSSValue;
  height?: CSSValue;
  margin?: CSSValue;
  padding?: CSSValue;
}

// Event handler types
type EventName = 'click' | 'focus' | 'blur' | 'change';
type EventHandlerName = `on${Capitalize<EventName>}`;
type EventHandlers = {
  [K in EventHandlerName]?: (event: Event) => void;
};
```

### Type Guards
```typescript
// Type predicate functions
function isUser(value: unknown): value is User {
  return (
    typeof value === 'object' &&
    value !== null &&
    'id' in value &&
    'email' in value &&
    typeof (value as any).email === 'string'
  );
}

// Using type guards
function processUserData(data: unknown) {
  if (isUser(data)) {
    // TypeScript knows data is User here
    console.log(data.email);
  } else {
    throw new Error('Invalid user data');
  }
}

// Array type guards
function isStringArray(value: unknown): value is string[] {
  return Array.isArray(value) && value.every(item => typeof item === 'string');
}
```

### Mapped Types
```typescript
// Make all properties nullable
type Nullable<T> = {
  [P in keyof T]: T[P] | null;
};

// Make specific properties required
type RequireFields<T, K extends keyof T> = T & Required<Pick<T, K>>;

// Deep partial
type DeepPartial<T> = T extends object ? {
  [P in keyof T]?: DeepPartial<T[P]>;
} : T;

// Getters type
type Getters<T> = {
  [K in keyof T as `get${Capitalize<string & K>}`]: () => T[K];
};

// Usage
type UserGetters = Getters<User>;
// Result: { getId: () => string; getName: () => string; ... }
```

## Environment and Config Types

### Environment Variables
```typescript
// env.d.ts
declare global {
  namespace NodeJS {
    interface ProcessEnv {
      NODE_ENV: 'development' | 'production' | 'test';
      NEXT_PUBLIC_API_URL: string;
      DATABASE_URL: string;
      JWT_SECRET: string;
      STRIPE_SECRET_KEY: string;
      STRIPE_WEBHOOK_SECRET: string;
    }
  }
}

export {};

// Type-safe env helper
function getEnvVar(key: keyof NodeJS.ProcessEnv): string {
  const value = process.env[key];
  if (!value) {
    throw new Error(`Missing environment variable: ${key}`);
  }
  return value;
}
```

### Configuration Types
```typescript
// config.ts
interface AppConfig {
  api: {
    baseUrl: string;
    timeout: number;
    retries: number;
  };
  auth: {
    sessionDuration: number;
    providers: ('github' | 'google' | 'email')[];
  };
  features: {
    enableBeta: boolean;
    maintenanceMode: boolean;
  };
}

const config: AppConfig = {
  api: {
    baseUrl: process.env.NEXT_PUBLIC_API_URL,
    timeout: 30000,
    retries: 3,
  },
  auth: {
    sessionDuration: 7 * 24 * 60 * 60 * 1000, // 7 days
    providers: ['github', 'google', 'email'],
  },
  features: {
    enableBeta: process.env.NODE_ENV === 'development',
    maintenanceMode: false,
  },
};

// Type-safe config access
export function getConfig<K extends keyof AppConfig>(key: K): AppConfig[K] {
  return config[key];
}
```

## Best Practices

### Do's
- Use strict TypeScript configuration
- Prefer interfaces for object shapes
- Use const assertions for literal types
- Leverage type inference when possible
- Create reusable generic types
- Use discriminated unions for state
- Implement proper error types
- Type external library usage

### Don'ts
- Don't use `any` unless absolutely necessary
- Don't use `@ts-ignore` without documentation
- Don't create overly complex types
- Don't duplicate type definitions
- Don't use `Function` type (too broad)
- Don't forget to handle null/undefined
- Don't cast without type guards
- Don't ignore TypeScript errors

### Type Safety Checklist
- [ ] Enable strict mode in tsconfig.json
- [ ] No implicit any
- [ ] All functions have return types
- [ ] All event handlers are properly typed
- [ ] API responses have types
- [ ] Form data is validated with types
- [ ] Environment variables are typed
- [ ] External libraries have type definitions