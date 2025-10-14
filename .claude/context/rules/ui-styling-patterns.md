---
description: Tailwind CSS, shadcn/ui, component styling, and UI patterns
alwaysApply: true
---

# UI & Styling Patterns

Consolidated from: tailwind-v4.mdc, tailwind-v4-ext.mdc, shadcn-tanstack-table-filter.mdc

## Tailwind CSS v4 Patterns

### CSS Variables & Theming
```css
/* app/globals.css */
@import "tailwindcss";

@theme {
  /* Color System */
  --color-background: oklch(100% 0 0);
  --color-foreground: oklch(0% 0 0);
  --color-primary: oklch(50% 0.2 250);
  --color-primary-foreground: oklch(100% 0 0);
  --color-secondary: oklch(90% 0.05 250);
  --color-secondary-foreground: oklch(10% 0 0);
  --color-muted: oklch(95% 0.02 250);
  --color-muted-foreground: oklch(40% 0.02 250);
  --color-accent: oklch(94% 0.05 250);
  --color-accent-foreground: oklch(10% 0 0);
  --color-destructive: oklch(60% 0.2 30);
  --color-destructive-foreground: oklch(100% 0 0);
  --color-border: oklch(90% 0.02 250);
  --color-input: oklch(90% 0.02 250);
  --color-ring: var(--color-primary);

  /* Dark Mode */
  @media (prefers-color-scheme: dark) {
    --color-background: oklch(10% 0 0);
    --color-foreground: oklch(95% 0 0);
    --color-primary: oklch(60% 0.2 250);
    --color-secondary: oklch(20% 0.05 250);
    --color-muted: oklch(20% 0.02 250);
    --color-accent: oklch(25% 0.05 250);
    --color-border: oklch(25% 0.02 250);
  }

  /* Spacing Scale */
  --spacing-px: 1px;
  --spacing-0: 0;
  --spacing-0.5: 0.125rem;
  --spacing-1: 0.25rem;
  --spacing-2: 0.5rem;
  --spacing-3: 0.75rem;
  --spacing-4: 1rem;
  --spacing-5: 1.25rem;
  --spacing-6: 1.5rem;
  --spacing-8: 2rem;
  --spacing-10: 2.5rem;
  --spacing-12: 3rem;
  --spacing-16: 4rem;
  --spacing-20: 5rem;
  --spacing-24: 6rem;

  /* Font System */
  --font-family-sans: system-ui, -apple-system, sans-serif;
  --font-family-mono: 'SF Mono', Monaco, monospace;
  
  --font-size-xs: 0.75rem;
  --font-size-sm: 0.875rem;
  --font-size-base: 1rem;
  --font-size-lg: 1.125rem;
  --font-size-xl: 1.25rem;
  --font-size-2xl: 1.5rem;
  --font-size-3xl: 1.875rem;
  --font-size-4xl: 2.25rem;

  /* Border Radius */
  --radius-none: 0;
  --radius-sm: 0.125rem;
  --radius-base: 0.25rem;
  --radius-md: 0.375rem;
  --radius-lg: 0.5rem;
  --radius-xl: 0.75rem;
  --radius-2xl: 1rem;
  --radius-full: 9999px;

  /* Shadows */
  --shadow-sm: 0 1px 2px 0 rgb(0 0 0 / 0.05);
  --shadow-base: 0 1px 3px 0 rgb(0 0 0 / 0.1), 0 1px 2px -1px rgb(0 0 0 / 0.1);
  --shadow-md: 0 4px 6px -1px rgb(0 0 0 / 0.1), 0 2px 4px -2px rgb(0 0 0 / 0.1);
  --shadow-lg: 0 10px 15px -3px rgb(0 0 0 / 0.1), 0 4px 6px -4px rgb(0 0 0 / 0.1);
  --shadow-xl: 0 20px 25px -5px rgb(0 0 0 / 0.1), 0 8px 10px -6px rgb(0 0 0 / 0.1);
}
```

### Component Class Patterns
```tsx
// Consistent component styling
export function Card({ className, ...props }: CardProps) {
  return (
    <div
      className={cn(
        // Base styles
        "rounded-lg border bg-card text-card-foreground shadow-sm",
        // Responsive
        "p-4 sm:p-6",
        // Dark mode handled by CSS variables
        className
      )}
      {...props}
    />
  );
}

// Button variants using Tailwind v4
const buttonVariants = {
  base: "inline-flex items-center justify-center whitespace-nowrap rounded-md text-sm font-medium ring-offset-background transition-colors focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-ring focus-visible:ring-offset-2 disabled:pointer-events-none disabled:opacity-50",
  variants: {
    variant: {
      default: "bg-primary text-primary-foreground hover:bg-primary/90",
      destructive: "bg-destructive text-destructive-foreground hover:bg-destructive/90",
      outline: "border border-input bg-background hover:bg-accent hover:text-accent-foreground",
      secondary: "bg-secondary text-secondary-foreground hover:bg-secondary/80",
      ghost: "hover:bg-accent hover:text-accent-foreground",
      link: "text-primary underline-offset-4 hover:underline",
    },
    size: {
      default: "h-10 px-4 py-2",
      sm: "h-9 rounded-md px-3",
      lg: "h-11 rounded-md px-8",
      icon: "h-10 w-10",
    },
  },
};
```

### Responsive Design Patterns
```tsx
// Mobile-first responsive design
export function Dashboard() {
  return (
    <div className="container mx-auto px-4 py-8">
      {/* Stack on mobile, grid on larger screens */}
      <div className="grid gap-6 md:grid-cols-2 lg:grid-cols-3">
        <StatsCard title="Total Users" value="1,234" />
        <StatsCard title="Revenue" value="$12,345" />
        <StatsCard title="Growth" value="+12.5%" />
      </div>

      {/* Responsive text sizing */}
      <h1 className="text-2xl font-bold md:text-3xl lg:text-4xl">
        Dashboard
      </h1>

      {/* Responsive spacing */}
      <div className="mt-4 space-y-4 md:mt-6 md:space-y-6 lg:mt-8">
        {/* Content */}
      </div>

      {/* Hide/show based on breakpoint */}
      <div className="hidden md:block">
        <Sidebar />
      </div>
      <div className="md:hidden">
        <MobileMenu />
      </div>
    </div>
  );
}
```

### Animation Patterns
```css
/* Custom animations in Tailwind v4 */
@theme {
  --animate-fade-in: fade-in 0.5s ease-in-out;
  --animate-slide-up: slide-up 0.3s ease-out;
  --animate-pulse-subtle: pulse-subtle 2s infinite;
}

@keyframes fade-in {
  from { opacity: 0; }
  to { opacity: 1; }
}

@keyframes slide-up {
  from { 
    opacity: 0;
    transform: translateY(10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

@keyframes pulse-subtle {
  0%, 100% { opacity: 1; }
  50% { opacity: 0.8; }
}
```

## shadcn/ui Integration

### Component Composition
```tsx
// Composing shadcn/ui components
import {
  Card,
  CardContent,
  CardDescription,
  CardFooter,
  CardHeader,
  CardTitle,
} from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { Badge } from "@/components/ui/badge";

export function FeatureCard({ feature }: { feature: Feature }) {
  return (
    <Card className="h-full">
      <CardHeader>
        <div className="flex items-center justify-between">
          <CardTitle>{feature.title}</CardTitle>
          {feature.isPro && <Badge variant="secondary">Pro</Badge>}
        </div>
        <CardDescription>{feature.description}</CardDescription>
      </CardHeader>
      <CardContent>
        <div className="space-y-2">
          {feature.items.map((item) => (
            <div key={item} className="flex items-center gap-2">
              <CheckIcon className="h-4 w-4 text-primary" />
              <span className="text-sm">{item}</span>
            </div>
          ))}
        </div>
      </CardContent>
      <CardFooter>
        <Button className="w-full" variant={feature.isPro ? "default" : "outline"}>
          {feature.isPro ? "Upgrade to Pro" : "Learn More"}
        </Button>
      </CardFooter>
    </Card>
  );
}
```

### Form Styling
```tsx
// Form with shadcn/ui components
import { zodResolver } from "@hookform/resolvers/zod";
import { useForm } from "react-hook-form";
import * as z from "zod";
import {
  Form,
  FormControl,
  FormDescription,
  FormField,
  FormItem,
  FormLabel,
  FormMessage,
} from "@/components/ui/form";
import { Input } from "@/components/ui/input";
import { Textarea } from "@/components/ui/textarea";
import { Switch } from "@/components/ui/switch";

const profileSchema = z.object({
  username: z.string().min(3).max(20),
  bio: z.string().max(160).optional(),
  publicProfile: z.boolean().default(true),
});

export function ProfileForm() {
  const form = useForm<z.infer<typeof profileSchema>>({
    resolver: zodResolver(profileSchema),
    defaultValues: {
      username: "",
      bio: "",
      publicProfile: true,
    },
  });

  return (
    <Form {...form}>
      <form onSubmit={form.handleSubmit(onSubmit)} className="space-y-6">
        <FormField
          control={form.control}
          name="username"
          render={({ field }) => (
            <FormItem>
              <FormLabel>Username</FormLabel>
              <FormControl>
                <Input placeholder="johndoe" {...field} />
              </FormControl>
              <FormDescription>
                This is your public display name.
              </FormDescription>
              <FormMessage />
            </FormItem>
          )}
        />

        <FormField
          control={form.control}
          name="bio"
          render={({ field }) => (
            <FormItem>
              <FormLabel>Bio</FormLabel>
              <FormControl>
                <Textarea
                  placeholder="Tell us about yourself"
                  className="resize-none"
                  {...field}
                />
              </FormControl>
              <FormDescription>
                Brief description for your profile. Max 160 characters.
              </FormDescription>
              <FormMessage />
            </FormItem>
          )}
        />

        <FormField
          control={form.control}
          name="publicProfile"
          render={({ field }) => (
            <FormItem className="flex flex-row items-center justify-between rounded-lg border p-4">
              <div className="space-y-0.5">
                <FormLabel className="text-base">Public Profile</FormLabel>
                <FormDescription>
                  Make your profile visible to everyone
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

        <Button type="submit">Save changes</Button>
      </form>
    </Form>
  );
}
```

### Dialog Patterns
```tsx
// Modal dialog with shadcn/ui
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogFooter,
  DialogHeader,
  DialogTitle,
  DialogTrigger,
} from "@/components/ui/dialog";

export function DeleteConfirmDialog({ onConfirm }: { onConfirm: () => void }) {
  const [open, setOpen] = useState(false);

  const handleConfirm = () => {
    onConfirm();
    setOpen(false);
  };

  return (
    <Dialog open={open} onOpenChange={setOpen}>
      <DialogTrigger asChild>
        <Button variant="destructive" size="sm">
          Delete
        </Button>
      </DialogTrigger>
      <DialogContent className="sm:max-w-[425px]">
        <DialogHeader>
          <DialogTitle>Are you sure?</DialogTitle>
          <DialogDescription>
            This action cannot be undone. This will permanently delete your
            account and remove your data from our servers.
          </DialogDescription>
        </DialogHeader>
        <DialogFooter className="gap-2 sm:gap-0">
          <Button variant="outline" onClick={() => setOpen(false)}>
            Cancel
          </Button>
          <Button variant="destructive" onClick={handleConfirm}>
            Delete Account
          </Button>
        </DialogFooter>
      </DialogContent>
    </Dialog>
  );
}
```

## Advanced Table Patterns

### TanStack Table with Filtering
```tsx
// Complex data table with filtering, sorting, and pagination
import {
  ColumnDef,
  ColumnFiltersState,
  SortingState,
  VisibilityState,
  flexRender,
  getCoreRowModel,
  getFilteredRowModel,
  getPaginationRowModel,
  getSortedRowModel,
  useReactTable,
} from "@tanstack/react-table";

interface DataTableProps<TData, TValue> {
  columns: ColumnDef<TData, TValue>[];
  data: TData[];
  searchKey?: string;
}

export function DataTable<TData, TValue>({
  columns,
  data,
  searchKey,
}: DataTableProps<TData, TValue>) {
  const [sorting, setSorting] = useState<SortingState>([]);
  const [columnFilters, setColumnFilters] = useState<ColumnFiltersState>([]);
  const [columnVisibility, setColumnVisibility] = useState<VisibilityState>({});
  const [rowSelection, setRowSelection] = useState({});
  const [globalFilter, setGlobalFilter] = useState("");

  const table = useReactTable({
    data,
    columns,
    onSortingChange: setSorting,
    onColumnFiltersChange: setColumnFilters,
    getCoreRowModel: getCoreRowModel(),
    getPaginationRowModel: getPaginationRowModel(),
    getSortedRowModel: getSortedRowModel(),
    getFilteredRowModel: getFilteredRowModel(),
    onColumnVisibilityChange: setColumnVisibility,
    onRowSelectionChange: setRowSelection,
    onGlobalFilterChange: setGlobalFilter,
    state: {
      sorting,
      columnFilters,
      columnVisibility,
      rowSelection,
      globalFilter,
    },
  });

  return (
    <div className="space-y-4">
      {/* Toolbar */}
      <div className="flex items-center justify-between">
        <div className="flex flex-1 items-center space-x-2">
          <Input
            placeholder={`Search ${searchKey || "table"}...`}
            value={globalFilter ?? ""}
            onChange={(event) => setGlobalFilter(event.target.value)}
            className="h-8 w-[150px] lg:w-[250px]"
          />
          {/* Faceted filters */}
          {table.getColumn("status") && (
            <DataTableFacetedFilter
              column={table.getColumn("status")}
              title="Status"
              options={statusOptions}
            />
          )}
          {table.getColumn("priority") && (
            <DataTableFacetedFilter
              column={table.getColumn("priority")}
              title="Priority"
              options={priorityOptions}
            />
          )}
        </div>
        <DataTableViewOptions table={table} />
      </div>

      {/* Table */}
      <div className="rounded-md border">
        <Table>
          <TableHeader>
            {table.getHeaderGroups().map((headerGroup) => (
              <TableRow key={headerGroup.id}>
                {headerGroup.headers.map((header) => (
                  <TableHead key={header.id}>
                    {header.isPlaceholder
                      ? null
                      : flexRender(
                          header.column.columnDef.header,
                          header.getContext()
                        )}
                  </TableHead>
                ))}
              </TableRow>
            ))}
          </TableHeader>
          <TableBody>
            {table.getRowModel().rows?.length ? (
              table.getRowModel().rows.map((row) => (
                <TableRow
                  key={row.id}
                  data-state={row.getIsSelected() && "selected"}
                >
                  {row.getVisibleCells().map((cell) => (
                    <TableCell key={cell.id}>
                      {flexRender(
                        cell.column.columnDef.cell,
                        cell.getContext()
                      )}
                    </TableCell>
                  ))}
                </TableRow>
              ))
            ) : (
              <TableRow>
                <TableCell
                  colSpan={columns.length}
                  className="h-24 text-center"
                >
                  No results.
                </TableCell>
              </TableRow>
            )}
          </TableBody>
        </Table>
      </div>

      {/* Pagination */}
      <DataTablePagination table={table} />
    </div>
  );
}
```

### Column Definitions
```tsx
// Type-safe column definitions
export const columns: ColumnDef<Task>[] = [
  {
    id: "select",
    header: ({ table }) => (
      <Checkbox
        checked={
          table.getIsAllPageRowsSelected() ||
          (table.getIsSomePageRowsSelected() && "indeterminate")
        }
        onCheckedChange={(value) => table.toggleAllPageRowsSelected(!!value)}
        aria-label="Select all"
      />
    ),
    cell: ({ row }) => (
      <Checkbox
        checked={row.getIsSelected()}
        onCheckedChange={(value) => row.toggleSelected(!!value)}
        aria-label="Select row"
      />
    ),
    enableSorting: false,
    enableHiding: false,
  },
  {
    accessorKey: "title",
    header: ({ column }) => (
      <DataTableColumnHeader column={column} title="Title" />
    ),
    cell: ({ row }) => {
      const title = row.getValue("title") as string;
      return (
        <div className="flex space-x-2">
          <Badge variant="outline">{row.original.label}</Badge>
          <span className="max-w-[500px] truncate font-medium">
            {title}
          </span>
        </div>
      );
    },
  },
  {
    accessorKey: "status",
    header: ({ column }) => (
      <DataTableColumnHeader column={column} title="Status" />
    ),
    cell: ({ row }) => {
      const status = row.getValue("status") as string;
      const statusConfig = statusOptions.find((s) => s.value === status);

      return (
        <div className="flex items-center">
          {statusConfig?.icon && (
            <statusConfig.icon className="mr-2 h-4 w-4 text-muted-foreground" />
          )}
          <span>{statusConfig?.label}</span>
        </div>
      );
    },
    filterFn: (row, id, value) => {
      return value.includes(row.getValue(id));
    },
  },
  {
    accessorKey: "priority",
    header: ({ column }) => (
      <DataTableColumnHeader column={column} title="Priority" />
    ),
    cell: ({ row }) => {
      const priority = row.getValue("priority") as string;
      const priorityConfig = priorityOptions.find((p) => p.value === priority);

      return (
        <div className="flex items-center">
          {priorityConfig?.icon && (
            <priorityConfig.icon className="mr-2 h-4 w-4 text-muted-foreground" />
          )}
          <span>{priorityConfig?.label}</span>
        </div>
      );
    },
    filterFn: (row, id, value) => {
      return value.includes(row.getValue(id));
    },
  },
  {
    id: "actions",
    cell: ({ row }) => <DataTableRowActions row={row} />,
  },
];
```

### Server-Side Table Filtering
```tsx
// Server-side filtering with URL state
import { parseAsInteger, parseAsString, useQueryStates } from 'nuqs';

export function ServerDataTable() {
  // URL state management
  const [filters, setFilters] = useQueryStates({
    page: parseAsInteger.withDefault(1),
    limit: parseAsInteger.withDefault(10),
    sort: parseAsString.withDefault('created_at'),
    order: parseAsString.withDefault('desc'),
    search: parseAsString,
    status: parseAsString,
    priority: parseAsString,
  });

  // Fetch data with filters
  const { data, isLoading } = useQuery({
    queryKey: ['tasks', filters],
    queryFn: () => fetchTasks(filters),
  });

  // Update URL when filter changes
  const handleFilterChange = (key: string, value: any) => {
    setFilters({ ...filters, [key]: value, page: 1 });
  };

  return (
    <div className="space-y-4">
      {/* Search */}
      <Input
        placeholder="Search tasks..."
        value={filters.search || ''}
        onChange={(e) => handleFilterChange('search', e.target.value)}
      />

      {/* Filters */}
      <div className="flex gap-2">
        <Select
          value={filters.status || 'all'}
          onValueChange={(value) => handleFilterChange('status', value)}
        >
          <SelectTrigger className="w-[180px]">
            <SelectValue placeholder="Select status" />
          </SelectTrigger>
          <SelectContent>
            <SelectItem value="all">All Statuses</SelectItem>
            <SelectItem value="todo">To Do</SelectItem>
            <SelectItem value="in-progress">In Progress</SelectItem>
            <SelectItem value="done">Done</SelectItem>
          </SelectContent>
        </Select>

        <Select
          value={filters.priority || 'all'}
          onValueChange={(value) => handleFilterChange('priority', value)}
        >
          <SelectTrigger className="w-[180px]">
            <SelectValue placeholder="Select priority" />
          </SelectTrigger>
          <SelectContent>
            <SelectItem value="all">All Priorities</SelectItem>
            <SelectItem value="low">Low</SelectItem>
            <SelectItem value="medium">Medium</SelectItem>
            <SelectItem value="high">High</SelectItem>
          </SelectContent>
        </Select>
      </div>

      {/* Table with loading state */}
      {isLoading ? (
        <TableSkeleton />
      ) : (
        <DataTable
          data={data?.tasks || []}
          columns={columns}
          pagination={{
            page: filters.page,
            pageSize: filters.limit,
            total: data?.total || 0,
            onPageChange: (page) => setFilters({ page }),
          }}
        />
      )}
    </div>
  );
}
```

## Loading & Skeleton States

### Skeleton Components
```tsx
// Reusable skeleton components
export function TableSkeleton({ rows = 5 }: { rows?: number }) {
  return (
    <div className="rounded-md border">
      <Table>
        <TableHeader>
          <TableRow>
            <TableHead className="w-[50px]">
              <Skeleton className="h-4 w-4" />
            </TableHead>
            <TableHead>
              <Skeleton className="h-4 w-[150px]" />
            </TableHead>
            <TableHead>
              <Skeleton className="h-4 w-[100px]" />
            </TableHead>
            <TableHead>
              <Skeleton className="h-4 w-[100px]" />
            </TableHead>
            <TableHead className="w-[50px]">
              <Skeleton className="h-4 w-4" />
            </TableHead>
          </TableRow>
        </TableHeader>
        <TableBody>
          {Array.from({ length: rows }).map((_, i) => (
            <TableRow key={i}>
              <TableCell>
                <Skeleton className="h-4 w-4" />
              </TableCell>
              <TableCell>
                <div className="space-y-2">
                  <Skeleton className="h-4 w-[250px]" />
                  <Skeleton className="h-3 w-[200px]" />
                </div>
              </TableCell>
              <TableCell>
                <Skeleton className="h-6 w-[80px] rounded-full" />
              </TableCell>
              <TableCell>
                <Skeleton className="h-6 w-[60px] rounded-full" />
              </TableCell>
              <TableCell>
                <Skeleton className="h-8 w-8 rounded" />
              </TableCell>
            </TableRow>
          ))}
        </TableBody>
      </Table>
    </div>
  );
}

// Card skeleton
export function CardSkeleton() {
  return (
    <Card>
      <CardHeader>
        <Skeleton className="h-6 w-[150px]" />
        <Skeleton className="h-4 w-[250px]" />
      </CardHeader>
      <CardContent>
        <div className="space-y-2">
          <Skeleton className="h-4 w-full" />
          <Skeleton className="h-4 w-[80%]" />
          <Skeleton className="h-4 w-[60%]" />
        </div>
      </CardContent>
      <CardFooter>
        <Skeleton className="h-10 w-full" />
      </CardFooter>
    </Card>
  );
}
```

## Dark Mode Implementation

### Theme Provider
```tsx
// providers/theme-provider.tsx
import { ThemeProvider as NextThemesProvider } from "next-themes";

export function ThemeProvider({
  children,
  ...props
}: React.ComponentProps<typeof NextThemesProvider>) {
  return (
    <NextThemesProvider
      attribute="class"
      defaultTheme="system"
      enableSystem
      disableTransitionOnChange
      {...props}
    >
      {children}
    </NextThemesProvider>
  );
}

// Theme toggle component
export function ThemeToggle() {
  const { setTheme, theme } = useTheme();

  return (
    <DropdownMenu>
      <DropdownMenuTrigger asChild>
        <Button variant="outline" size="icon">
          <Sun className="h-[1.2rem] w-[1.2rem] rotate-0 scale-100 transition-all dark:-rotate-90 dark:scale-0" />
          <Moon className="absolute h-[1.2rem] w-[1.2rem] rotate-90 scale-0 transition-all dark:rotate-0 dark:scale-100" />
          <span className="sr-only">Toggle theme</span>
        </Button>
      </DropdownMenuTrigger>
      <DropdownMenuContent align="end">
        <DropdownMenuItem onClick={() => setTheme("light")}>
          Light
        </DropdownMenuItem>
        <DropdownMenuItem onClick={() => setTheme("dark")}>
          Dark
        </DropdownMenuItem>
        <DropdownMenuItem onClick={() => setTheme("system")}>
          System
        </DropdownMenuItem>
      </DropdownMenuContent>
    </DropdownMenu>
  );
}
```

## Best Practices

### Do's
- Use CSS variables for theming
- Follow mobile-first responsive design
- Compose components from primitives
- Use semantic color names
- Implement proper loading states
- Ensure keyboard navigation works
- Test dark mode thoroughly
- Use consistent spacing scale

### Don'ts
- Don't use arbitrary values excessively
- Don't forget focus states
- Don't ignore accessibility
- Don't mix styling approaches
- Don't use !important unless necessary
- Don't forget hover/active states
- Don't hardcode colors
- Don't ignore RTL support

### Performance Tips
- Use CSS over JavaScript animations
- Minimize bundle size with component imports
- Lazy load heavy components
- Use skeleton screens for perceived performance
- Optimize images with next/image
- Use CSS containment for complex layouts
- Implement virtual scrolling for long lists
- Minimize re-renders with proper memoization