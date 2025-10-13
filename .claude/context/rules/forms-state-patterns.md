---
description: Form handling, state management, and data fetching patterns
alwaysApply: true
---

# Forms & State Management Patterns

Consolidated from: form-handling-patterns.mdc, state-management-patterns.mdc, data-fetching-guidelines.mdc

## Form Handling with React Hook Form

### Basic Form Setup
```tsx
// Basic form with validation
import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import * as z from "zod";

const formSchema = z.object({
  email: z.string().email("Invalid email address"),
  password: z.string().min(8, "Password must be at least 8 characters"),
  rememberMe: z.boolean().default(false),
});

type FormData = z.infer<typeof formSchema>;

export function LoginForm() {
  const form = useForm<FormData>({
    resolver: zodResolver(formSchema),
    defaultValues: {
      email: "",
      password: "",
      rememberMe: false,
    },
  });

  const onSubmit = async (data: FormData) => {
    try {
      // Handle form submission
      await loginUser(data);
    } catch (error) {
      form.setError("root", {
        message: "Invalid credentials",
      });
    }
  };

  return (
    <Form {...form}>
      <form onSubmit={form.handleSubmit(onSubmit)} className="space-y-4">
        <FormField
          control={form.control}
          name="email"
          render={({ field }) => (
            <FormItem>
              <FormLabel>Email</FormLabel>
              <FormControl>
                <Input type="email" placeholder="john@example.com" {...field} />
              </FormControl>
              <FormMessage />
            </FormItem>
          )}
        />

        <FormField
          control={form.control}
          name="password"
          render={({ field }) => (
            <FormItem>
              <FormLabel>Password</FormLabel>
              <FormControl>
                <Input type="password" {...field} />
              </FormControl>
              <FormMessage />
            </FormItem>
          )}
        />

        <FormField
          control={form.control}
          name="rememberMe"
          render={({ field }) => (
            <FormItem className="flex items-center space-x-2">
              <FormControl>
                <Checkbox
                  checked={field.value}
                  onCheckedChange={field.onChange}
                />
              </FormControl>
              <FormLabel className="text-sm font-normal">
                Remember me
              </FormLabel>
            </FormItem>
          )}
        />

        {form.formState.errors.root && (
          <Alert variant="destructive">
            <AlertDescription>
              {form.formState.errors.root.message}
            </AlertDescription>
          </Alert>
        )}

        <Button
          type="submit"
          className="w-full"
          disabled={form.formState.isSubmitting}
        >
          {form.formState.isSubmitting ? (
            <>
              <Loader2 className="mr-2 h-4 w-4 animate-spin" />
              Signing in...
            </>
          ) : (
            "Sign in"
          )}
        </Button>
      </form>
    </Form>
  );
}
```

### Dynamic Form Arrays
```tsx
// Form with dynamic fields
const teamSchema = z.object({
  name: z.string().min(1, "Team name is required"),
  members: z.array(
    z.object({
      email: z.string().email("Invalid email"),
      role: z.enum(["admin", "member", "viewer"]),
    })
  ).min(1, "At least one member is required"),
});

export function TeamForm() {
  const form = useForm<z.infer<typeof teamSchema>>({
    resolver: zodResolver(teamSchema),
    defaultValues: {
      name: "",
      members: [{ email: "", role: "member" }],
    },
  });

  const { fields, append, remove } = useFieldArray({
    control: form.control,
    name: "members",
  });

  return (
    <Form {...form}>
      <form onSubmit={form.handleSubmit(onSubmit)} className="space-y-6">
        <FormField
          control={form.control}
          name="name"
          render={({ field }) => (
            <FormItem>
              <FormLabel>Team Name</FormLabel>
              <FormControl>
                <Input {...field} />
              </FormControl>
              <FormMessage />
            </FormItem>
          )}
        />

        <div className="space-y-4">
          <div className="flex items-center justify-between">
            <Label>Team Members</Label>
            <Button
              type="button"
              variant="outline"
              size="sm"
              onClick={() => append({ email: "", role: "member" })}
            >
              Add Member
            </Button>
          </div>

          {fields.map((field, index) => (
            <div key={field.id} className="flex gap-2">
              <FormField
                control={form.control}
                name={`members.${index}.email`}
                render={({ field }) => (
                  <FormItem className="flex-1">
                    <FormControl>
                      <Input
                        type="email"
                        placeholder="member@example.com"
                        {...field}
                      />
                    </FormControl>
                    <FormMessage />
                  </FormItem>
                )}
              />

              <FormField
                control={form.control}
                name={`members.${index}.role`}
                render={({ field }) => (
                  <FormItem className="w-32">
                    <Select
                      onValueChange={field.onChange}
                      defaultValue={field.value}
                    >
                      <FormControl>
                        <SelectTrigger>
                          <SelectValue />
                        </SelectTrigger>
                      </FormControl>
                      <SelectContent>
                        <SelectItem value="admin">Admin</SelectItem>
                        <SelectItem value="member">Member</SelectItem>
                        <SelectItem value="viewer">Viewer</SelectItem>
                      </SelectContent>
                    </Select>
                    <FormMessage />
                  </FormItem>
                )}
              />

              <Button
                type="button"
                variant="ghost"
                size="icon"
                onClick={() => remove(index)}
                disabled={fields.length === 1}
              >
                <X className="h-4 w-4" />
              </Button>
            </div>
          ))}
        </div>

        <Button type="submit">Create Team</Button>
      </form>
    </Form>
  );
}
```

### File Upload Forms
```tsx
// Form with file upload
const uploadSchema = z.object({
  title: z.string().min(1, "Title is required"),
  description: z.string().optional(),
  file: z
    .custom<File>()
    .refine((file) => file?.size <= 5 * 1024 * 1024, "Max file size is 5MB")
    .refine(
      (file) => ["image/jpeg", "image/png", "image/webp"].includes(file?.type),
      "Only .jpg, .png, and .webp files are accepted"
    ),
});

export function UploadForm() {
  const [preview, setPreview] = useState<string | null>(null);
  const form = useForm<z.infer<typeof uploadSchema>>({
    resolver: zodResolver(uploadSchema),
  });

  const handleFileChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const file = e.target.files?.[0];
    if (file) {
      form.setValue("file", file);
      const reader = new FileReader();
      reader.onloadend = () => {
        setPreview(reader.result as string);
      };
      reader.readAsDataURL(file);
    }
  };

  return (
    <Form {...form}>
      <form onSubmit={form.handleSubmit(onSubmit)} className="space-y-4">
        <FormField
          control={form.control}
          name="title"
          render={({ field }) => (
            <FormItem>
              <FormLabel>Title</FormLabel>
              <FormControl>
                <Input {...field} />
              </FormControl>
              <FormMessage />
            </FormItem>
          )}
        />

        <FormField
          control={form.control}
          name="file"
          render={({ field: { onChange, ...field } }) => (
            <FormItem>
              <FormLabel>Image</FormLabel>
              <FormControl>
                <div className="space-y-4">
                  <Input
                    type="file"
                    accept="image/*"
                    onChange={handleFileChange}
                    {...field}
                  />
                  {preview && (
                    <div className="relative h-40 w-40">
                      <img
                        src={preview}
                        alt="Preview"
                        className="h-full w-full object-cover rounded"
                      />
                      <Button
                        type="button"
                        variant="destructive"
                        size="icon"
                        className="absolute -top-2 -right-2 h-6 w-6"
                        onClick={() => {
                          form.setValue("file", undefined);
                          setPreview(null);
                        }}
                      >
                        <X className="h-3 w-3" />
                      </Button>
                    </div>
                  )}
                </div>
              </FormControl>
              <FormMessage />
            </FormItem>
          )}
        />

        <Button type="submit">Upload</Button>
      </form>
    </Form>
  );
}
```

### Multi-Step Forms
```tsx
// Multi-step form with progress
const steps = [
  { id: "account", name: "Account", fields: ["email", "password"] },
  { id: "profile", name: "Profile", fields: ["firstName", "lastName", "bio"] },
  { id: "preferences", name: "Preferences", fields: ["newsletter", "notifications"] },
];

const multiStepSchema = z.object({
  // Step 1
  email: z.string().email(),
  password: z.string().min(8),
  // Step 2
  firstName: z.string().min(1),
  lastName: z.string().min(1),
  bio: z.string().optional(),
  // Step 3
  newsletter: z.boolean(),
  notifications: z.boolean(),
});

export function MultiStepForm() {
  const [currentStep, setCurrentStep] = useState(0);
  const form = useForm<z.infer<typeof multiStepSchema>>({
    resolver: zodResolver(multiStepSchema),
    defaultValues: {
      newsletter: true,
      notifications: true,
    },
  });

  const next = async () => {
    const fields = steps[currentStep].fields;
    const output = await form.trigger(fields as any);

    if (!output) return;

    if (currentStep < steps.length - 1) {
      setCurrentStep(currentStep + 1);
    }
  };

  const previous = () => {
    if (currentStep > 0) {
      setCurrentStep(currentStep - 1);
    }
  };

  const onSubmit = async (data: z.infer<typeof multiStepSchema>) => {
    // Handle complete form submission
    console.log(data);
  };

  return (
    <div className="space-y-6">
      {/* Progress indicator */}
      <div className="space-y-2">
        <div className="flex justify-between text-sm">
          <span>Step {currentStep + 1} of {steps.length}</span>
          <span>{steps[currentStep].name}</span>
        </div>
        <Progress value={(currentStep + 1) / steps.length * 100} />
      </div>

      <Form {...form}>
        <form onSubmit={form.handleSubmit(onSubmit)} className="space-y-6">
          {/* Step 1: Account */}
          {currentStep === 0 && (
            <div className="space-y-4">
              <FormField
                control={form.control}
                name="email"
                render={({ field }) => (
                  <FormItem>
                    <FormLabel>Email</FormLabel>
                    <FormControl>
                      <Input type="email" {...field} />
                    </FormControl>
                    <FormMessage />
                  </FormItem>
                )}
              />
              <FormField
                control={form.control}
                name="password"
                render={({ field }) => (
                  <FormItem>
                    <FormLabel>Password</FormLabel>
                    <FormControl>
                      <Input type="password" {...field} />
                    </FormControl>
                    <FormMessage />
                  </FormItem>
                )}
              />
            </div>
          )}

          {/* Step 2: Profile */}
          {currentStep === 1 && (
            <div className="space-y-4">
              <div className="grid grid-cols-2 gap-4">
                <FormField
                  control={form.control}
                  name="firstName"
                  render={({ field }) => (
                    <FormItem>
                      <FormLabel>First Name</FormLabel>
                      <FormControl>
                        <Input {...field} />
                      </FormControl>
                      <FormMessage />
                    </FormItem>
                  )}
                />
                <FormField
                  control={form.control}
                  name="lastName"
                  render={({ field }) => (
                    <FormItem>
                      <FormLabel>Last Name</FormLabel>
                      <FormControl>
                        <Input {...field} />
                      </FormControl>
                      <FormMessage />
                    </FormItem>
                  )}
                />
              </div>
              <FormField
                control={form.control}
                name="bio"
                render={({ field }) => (
                  <FormItem>
                    <FormLabel>Bio</FormLabel>
                    <FormControl>
                      <Textarea {...field} />
                    </FormControl>
                    <FormMessage />
                  </FormItem>
                )}
              />
            </div>
          )}

          {/* Step 3: Preferences */}
          {currentStep === 2 && (
            <div className="space-y-4">
              <FormField
                control={form.control}
                name="newsletter"
                render={({ field }) => (
                  <FormItem className="flex items-center justify-between rounded-lg border p-4">
                    <div>
                      <FormLabel>Newsletter</FormLabel>
                      <FormDescription>
                        Receive our weekly newsletter
                      </FormDescription>
                    </div>
                    <FormControl>
                      <Switch
                        checked={field.value}
                        onCheckedChange={field.onChange}
                      />
                    </FormControl>
                  </FormItem>
                )}
              />
              <FormField
                control={form.control}
                name="notifications"
                render={({ field }) => (
                  <FormItem className="flex items-center justify-between rounded-lg border p-4">
                    <div>
                      <FormLabel>Notifications</FormLabel>
                      <FormDescription>
                        Receive notifications about your account
                      </FormDescription>
                    </div>
                    <FormControl>
                      <Switch
                        checked={field.value}
                        onCheckedChange={field.onChange}
                      />
                    </FormControl>
                  </FormItem>
                )}
              />
            </div>
          )}

          {/* Navigation */}
          <div className="flex justify-between">
            <Button
              type="button"
              variant="outline"
              onClick={previous}
              disabled={currentStep === 0}
            >
              Previous
            </Button>
            {currentStep === steps.length - 1 ? (
              <Button type="submit">Submit</Button>
            ) : (
              <Button type="button" onClick={next}>
                Next
              </Button>
            )}
          </div>
        </form>
      </Form>
    </div>
  );
}
```

## State Management Patterns

### Context for Global State
```tsx
// Global state with Context
interface AppState {
  user: User | null;
  theme: "light" | "dark" | "system";
  sidebarOpen: boolean;
}

interface AppContextValue extends AppState {
  setUser: (user: User | null) => void;
  setTheme: (theme: AppState["theme"]) => void;
  toggleSidebar: () => void;
}

const AppContext = createContext<AppContextValue | undefined>(undefined);

export function AppProvider({ children }: { children: React.ReactNode }) {
  const [state, setState] = useState<AppState>({
    user: null,
    theme: "system",
    sidebarOpen: true,
  });

  const value: AppContextValue = {
    ...state,
    setUser: (user) => setState((prev) => ({ ...prev, user })),
    setTheme: (theme) => setState((prev) => ({ ...prev, theme })),
    toggleSidebar: () =>
      setState((prev) => ({ ...prev, sidebarOpen: !prev.sidebarOpen })),
  };

  return <AppContext.Provider value={value}>{children}</AppContext.Provider>;
}

export function useApp() {
  const context = useContext(AppContext);
  if (!context) {
    throw new Error("useApp must be used within AppProvider");
  }
  return context;
}
```

### Zustand for Complex State
```tsx
// Zustand store for complex state
import { create } from 'zustand';
import { devtools, persist } from 'zustand/middleware';

interface CartItem {
  id: string;
  name: string;
  price: number;
  quantity: number;
}

interface CartStore {
  items: CartItem[];
  addItem: (item: Omit<CartItem, 'quantity'>) => void;
  removeItem: (id: string) => void;
  updateQuantity: (id: string, quantity: number) => void;
  clearCart: () => void;
  getTotalPrice: () => number;
  getTotalItems: () => number;
}

export const useCartStore = create<CartStore>()(
  devtools(
    persist(
      (set, get) => ({
        items: [],
        
        addItem: (item) =>
          set((state) => {
            const existingItem = state.items.find((i) => i.id === item.id);
            if (existingItem) {
              return {
                items: state.items.map((i) =>
                  i.id === item.id
                    ? { ...i, quantity: i.quantity + 1 }
                    : i
                ),
              };
            }
            return {
              items: [...state.items, { ...item, quantity: 1 }],
            };
          }),
        
        removeItem: (id) =>
          set((state) => ({
            items: state.items.filter((item) => item.id !== id),
          })),
        
        updateQuantity: (id, quantity) =>
          set((state) => ({
            items: state.items.map((item) =>
              item.id === id ? { ...item, quantity } : item
            ),
          })),
        
        clearCart: () => set({ items: [] }),
        
        getTotalPrice: () => {
          const { items } = get();
          return items.reduce(
            (total, item) => total + item.price * item.quantity,
            0
          );
        },
        
        getTotalItems: () => {
          const { items } = get();
          return items.reduce((total, item) => total + item.quantity, 0);
        },
      }),
      {
        name: 'cart-storage',
      }
    )
  )
);
```

### URL State Management
```tsx
// URL state with nuqs
import { parseAsString, parseAsInteger, useQueryStates } from 'nuqs';

export function ProductList() {
  const [filters, setFilters] = useQueryStates({
    search: parseAsString.withDefault(''),
    category: parseAsString,
    minPrice: parseAsInteger,
    maxPrice: parseAsInteger,
    sort: parseAsString.withDefault('name'),
    page: parseAsInteger.withDefault(1),
  });

  // Update URL when filters change
  const handleSearchChange = (search: string) => {
    setFilters({ search, page: 1 }); // Reset to page 1 on search
  };

  const handleCategoryChange = (category: string | null) => {
    setFilters({ category, page: 1 });
  };

  // Fetch products based on URL state
  const { data, isLoading } = useQuery({
    queryKey: ['products', filters],
    queryFn: () => fetchProducts(filters),
  });

  return (
    <div className="space-y-4">
      <div className="flex gap-4">
        <Input
          placeholder="Search products..."
          value={filters.search}
          onChange={(e) => handleSearchChange(e.target.value)}
        />
        
        <Select
          value={filters.category || 'all'}
          onValueChange={(value) =>
            handleCategoryChange(value === 'all' ? null : value)
          }
        >
          <SelectTrigger>
            <SelectValue />
          </SelectTrigger>
          <SelectContent>
            <SelectItem value="all">All Categories</SelectItem>
            <SelectItem value="electronics">Electronics</SelectItem>
            <SelectItem value="clothing">Clothing</SelectItem>
            <SelectItem value="books">Books</SelectItem>
          </SelectContent>
        </Select>
      </div>

      {/* Product grid */}
      <ProductGrid products={data?.products} loading={isLoading} />

      {/* Pagination */}
      <Pagination
        currentPage={filters.page}
        totalPages={data?.totalPages || 1}
        onPageChange={(page) => setFilters({ page })}
      />
    </div>
  );
}
```

## Data Fetching Patterns

### Server Components Data Fetching
```tsx
// Parallel data fetching in server components
async function getUser(id: string) {
  const res = await fetch(`${API_URL}/users/${id}`, {
    next: { revalidate: 60 }, // Cache for 60 seconds
  });
  if (!res.ok) throw new Error('Failed to fetch user');
  return res.json();
}

async function getUserPosts(userId: string) {
  const res = await fetch(`${API_URL}/users/${userId}/posts`, {
    next: { revalidate: 60 },
  });
  if (!res.ok) throw new Error('Failed to fetch posts');
  return res.json();
}

export default async function UserProfile({ params }: { params: { id: string } }) {
  // Fetch in parallel
  const [user, posts] = await Promise.all([
    getUser(params.id),
    getUserPosts(params.id),
  ]);

  return (
    <div>
      <UserInfo user={user} />
      <PostList posts={posts} />
    </div>
  );
}
```

### TanStack Query Patterns
```tsx
// Query with optimistic updates
export function useUpdatePost() {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: updatePost,
    onMutate: async (newPost) => {
      // Cancel outgoing refetches
      await queryClient.cancelQueries({ queryKey: ['posts', newPost.id] });

      // Snapshot previous value
      const previousPost = queryClient.getQueryData(['posts', newPost.id]);

      // Optimistically update
      queryClient.setQueryData(['posts', newPost.id], newPost);

      // Return context with snapshot
      return { previousPost };
    },
    onError: (err, newPost, context) => {
      // Rollback on error
      if (context?.previousPost) {
        queryClient.setQueryData(
          ['posts', newPost.id],
          context.previousPost
        );
      }
    },
    onSettled: (data, error, variables) => {
      // Always refetch after error or success
      queryClient.invalidateQueries({ queryKey: ['posts', variables.id] });
    },
  });
}

// Infinite query for pagination
export function useInfinitePosts() {
  return useInfiniteQuery({
    queryKey: ['posts'],
    queryFn: ({ pageParam = 1 }) => fetchPosts({ page: pageParam }),
    getNextPageParam: (lastPage, pages) => {
      return lastPage.hasMore ? pages.length + 1 : undefined;
    },
    initialPageParam: 1,
  });
}

// Usage
export function InfinitePostList() {
  const {
    data,
    fetchNextPage,
    hasNextPage,
    isFetchingNextPage,
    status,
  } = useInfinitePosts();

  return (
    <div>
      {status === 'loading' ? (
        <Spinner />
      ) : status === 'error' ? (
        <Error />
      ) : (
        <>
          {data.pages.map((page, i) => (
            <div key={i}>
              {page.posts.map((post) => (
                <PostCard key={post.id} post={post} />
              ))}
            </div>
          ))}
          
          <Button
            onClick={() => fetchNextPage()}
            disabled={!hasNextPage || isFetchingNextPage}
          >
            {isFetchingNextPage
              ? 'Loading more...'
              : hasNextPage
              ? 'Load More'
              : 'Nothing more to load'}
          </Button>
        </>
      )}
    </div>
  );
}
```

### Real-time Subscriptions
```tsx
// Real-time data with React Query
export function useRealtimeMessages(channelId: string) {
  const queryClient = useQueryClient();

  // Initial data fetch
  const { data: messages } = useQuery({
    queryKey: ['messages', channelId],
    queryFn: () => fetchMessages(channelId),
  });

  // Set up real-time subscription
  useEffect(() => {
    const channel = supabase
      .channel(`messages:${channelId}`)
      .on(
        'postgres_changes',
        {
          event: '*',
          schema: 'public',
          table: 'messages',
          filter: `channel_id=eq.${channelId}`,
        },
        (payload) => {
          // Update cache based on event
          queryClient.setQueryData(
            ['messages', channelId],
            (old: Message[] = []) => {
              switch (payload.eventType) {
                case 'INSERT':
                  return [...old, payload.new as Message];
                case 'UPDATE':
                  return old.map((msg) =>
                    msg.id === payload.new.id ? payload.new as Message : msg
                  );
                case 'DELETE':
                  return old.filter((msg) => msg.id !== payload.old.id);
                default:
                  return old;
              }
            }
          );
        }
      )
      .subscribe();

    return () => {
      supabase.removeChannel(channel);
    };
  }, [channelId, queryClient]);

  return messages;
}
```

## Optimistic Updates

### Form with Optimistic UI
```tsx
// Optimistic form submission
export function CommentForm({ postId }: { postId: string }) {
  const [comment, setComment] = useState('');
  const queryClient = useQueryClient();
  const { user } = useUser();

  const mutation = useMutation({
    mutationFn: (text: string) => createComment({ postId, text }),
    onMutate: async (text) => {
      // Create optimistic comment
      const optimisticComment = {
        id: `temp-${Date.now()}`,
        text,
        postId,
        userId: user.id,
        createdAt: new Date().toISOString(),
        user: {
          name: user.name,
          avatar: user.avatar,
        },
      };

      // Add to cache
      queryClient.setQueryData(
        ['comments', postId],
        (old: Comment[] = []) => [...old, optimisticComment]
      );

      setComment(''); // Clear input immediately
      return { optimisticComment };
    },
    onError: (error, variables, context) => {
      // Remove optimistic comment on error
      if (context?.optimisticComment) {
        queryClient.setQueryData(
          ['comments', postId],
          (old: Comment[] = []) =>
            old.filter((c) => c.id !== context.optimisticComment.id)
        );
      }
      setComment(variables); // Restore input
      toast.error('Failed to post comment');
    },
    onSuccess: (data, variables, context) => {
      // Replace optimistic comment with real one
      queryClient.setQueryData(
        ['comments', postId],
        (old: Comment[] = []) =>
          old.map((c) =>
            c.id === context?.optimisticComment.id ? data : c
          )
      );
    },
  });

  return (
    <form
      onSubmit={(e) => {
        e.preventDefault();
        if (comment.trim()) {
          mutation.mutate(comment);
        }
      }}
    >
      <Textarea
        value={comment}
        onChange={(e) => setComment(e.target.value)}
        placeholder="Add a comment..."
        disabled={mutation.isPending}
      />
      <Button type="submit" disabled={!comment.trim() || mutation.isPending}>
        Post
      </Button>
    </form>
  );
}
```

## Best Practices

### Do's
- Use controlled components for forms
- Implement proper validation with Zod
- Show loading states during submission
- Handle errors gracefully
- Use optimistic updates for better UX
- Cache data appropriately
- Implement proper error boundaries
- Use URL state for shareable filters

### Don'ts
- Don't store sensitive data in URL state
- Don't mutate state directly
- Don't forget cleanup in useEffect
- Don't over-fetch data
- Don't ignore loading states
- Don't use uncontrolled forms for complex cases
- Don't forget form accessibility
- Don't store derived state

### Performance Tips
- Debounce search inputs
- Use React Query for server state
- Implement virtual scrolling for long lists
- Memoize expensive computations
- Use suspense for better loading UX
- Prefetch data on hover/focus
- Cancel outdated requests
- Batch state updates when possible