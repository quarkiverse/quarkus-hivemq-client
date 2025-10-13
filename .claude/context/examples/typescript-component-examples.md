# NextBase Component Examples

## Basic Server Component
```typescript
// ✅ app/dashboard/page.tsx
import { getLoggedInUser } from '@/utils/server/serverGetLoggedInUser';
import { getUserWorkspaces } from '@/data/user/workspaces';
import { WorkspaceList } from '@/components/workspaces/WorkspaceList';

export default async function DashboardPage() {
  const user = await getLoggedInUser();
  const { data: workspaces, error } = await getUserWorkspaces(user.id);
  
  if (error) {
    return <div>Error loading workspaces: {error.message}</div>;
  }
  
  return (
    <div className="container mx-auto py-6">
      <h1 className="text-3xl font-bold mb-6">Your Workspaces</h1>
      <WorkspaceList workspaces={workspaces || []} />
    </div>
  );
}
```

## Interactive Client Component
```typescript
// ✅ components/workspaces/WorkspaceList.tsx
'use client';
import { useState } from 'react';
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { Button } from '@/components/ui/button';
import { Badge } from '@/components/ui/badge';
import { Workspace } from '@/lib/database.types';

interface WorkspaceListProps {
  workspaces: Workspace[];
}

export function WorkspaceList({ workspaces }: WorkspaceListProps) {
  const [selectedId, setSelectedId] = useState<string>();
  
  const handleWorkspaceClick = (workspaceId: string) => {
    setSelectedId(workspaceId);
    // Navigate to workspace
    window.location.href = `/workspace/${workspaceId}`;
  };
  
  return (
    <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
      {workspaces.map((workspace) => (
        <Card 
          key={workspace.id}
          className={`cursor-pointer transition-all hover:shadow-lg ${
            selectedId === workspace.id ? 'ring-2 ring-primary' : ''
          }`}
          onClick={() => handleWorkspaceClick(workspace.id)}
        >
          <CardHeader>
            <CardTitle className="flex items-center justify-between">
              {workspace.name}
              <Badge variant="secondary">{workspace.member_count} members</Badge>
            </CardTitle>
          </CardHeader>
          <CardContent>
            <p className="text-muted-foreground mb-4">
              {workspace.description || 'No description'}
            </p>
            <div className="flex justify-between items-center">
              <span className="text-sm text-muted-foreground">
                Created {new Date(workspace.created_at).toLocaleDateString()}
              </span>
              <Button variant="outline" size="sm">
                View Details
              </Button>
            </div>
          </CardContent>
        </Card>
      ))}
    </div>
  );
}
```

## Form Component with Server Action
```typescript
// ✅ components/workspaces/CreateWorkspaceForm.tsx
'use client';
import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { z } from 'zod';
import { Button } from '@/components/ui/button';
import { Form, FormControl, FormField, FormItem, FormLabel, FormMessage } from '@/components/ui/form';
import { Input } from '@/components/ui/input';
import { Textarea } from '@/components/ui/textarea';
import { createWorkspaceAction } from '@/actions/workspace-actions';
import { useRouter } from 'next/navigation';
import { toast } from 'sonner';

const workspaceSchema = z.object({
  name: z.string().min(1, 'Name is required').max(50, 'Name too long'),
  slug: z.string()
    .min(3, 'Slug must be at least 3 characters')
    .regex(/^[a-z0-9-]+$/, 'Only lowercase letters, numbers, and hyphens'),
  description: z.string().optional()
});

type WorkspaceFormData = z.infer<typeof workspaceSchema>;

export function CreateWorkspaceForm() {
  const router = useRouter();
  const form = useForm<WorkspaceFormData>({
    resolver: zodResolver(workspaceSchema),
    defaultValues: {
      name: '',
      slug: '',
      description: ''
    }
  });
  
  const onSubmit = async (data: WorkspaceFormData) => {
    try {
      const result = await createWorkspaceAction(data);
      
      if (result?.serverError) {
        toast.error(result.serverError);
        return;
      }
      
      if (result?.validationErrors) {
        // Handle validation errors
        Object.entries(result.validationErrors).forEach(([field, errors]) => {
          form.setError(field as keyof WorkspaceFormData, {
            message: errors?.[0]
          });
        });
        return;
      }
      
      toast.success('Workspace created successfully!');
      router.push(`/workspace/${data.slug}`);
      
    } catch (error) {
      toast.error('Failed to create workspace');
      console.error('Workspace creation error:', error);
    }
  };
  
  return (
    <Form {...form}>
      <form onSubmit={form.handleSubmit(onSubmit)} className="space-y-6">
        <FormField
          control={form.control}
          name="name"
          render={({ field }) => (
            <FormItem>
              <FormLabel>Workspace Name</FormLabel>
              <FormControl>
                <Input placeholder="Enter workspace name" {...field} />
              </FormControl>
              <FormMessage />
            </FormItem>
          )}
        />
        
        <FormField
          control={form.control}
          name="slug"
          render={({ field }) => (
            <FormItem>
              <FormLabel>Workspace Slug</FormLabel>
              <FormControl>
                <Input 
                  placeholder="workspace-slug" 
                  {...field}
                  onChange={(e) => {
                    const value = e.target.value.toLowerCase().replace(/[^a-z0-9-]/g, '');
                    field.onChange(value);
                  }}
                />
              </FormControl>
              <FormMessage />
            </FormItem>
          )}
        />
        
        <FormField
          control={form.control}
          name="description"
          render={({ field }) => (
            <FormItem>
              <FormLabel>Description (Optional)</FormLabel>
              <FormControl>
                <Textarea 
                  placeholder="Describe your workspace"
                  className="min-h-[100px]"
                  {...field} 
                />
              </FormControl>
              <FormMessage />
            </FormItem>
          )}
        />
        
        <Button 
          type="submit" 
          className="w-full"
          disabled={form.formState.isSubmitting}
        >
          {form.formState.isSubmitting ? 'Creating...' : 'Create Workspace'}
        </Button>
      </form>
    </Form>
  );
}
```

## Modal Dialog Component
```typescript
// ✅ components/ui/CreateWorkspaceDialog.tsx
'use client';
import { useState } from 'react';
import { Dialog, DialogContent, DialogHeader, DialogTitle, DialogTrigger } from '@/components/ui/dialog';
import { Button } from '@/components/ui/button';
import { Plus } from 'lucide-react';
import { CreateWorkspaceForm } from '@/components/workspaces/CreateWorkspaceForm';

export function CreateWorkspaceDialog() {
  const [open, setOpen] = useState(false);
  
  const handleSuccess = () => {
    setOpen(false);
  };
  
  return (
    <Dialog open={open} onOpenChange={setOpen}>
      <DialogTrigger asChild>
        <Button data-testid="create-workspace-button">
          <Plus className="mr-2 h-4 w-4" />
          Create Workspace
        </Button>
      </DialogTrigger>
      <DialogContent className="sm:max-w-[425px]">
        <DialogHeader>
          <DialogTitle>Create New Workspace</DialogTitle>
        </DialogHeader>
        <CreateWorkspaceForm onSuccess={handleSuccess} />
      </DialogContent>
    </Dialog>
  );
}
```

## Data Table Component
```typescript
// ✅ components/workspaces/WorkspaceTable.tsx
'use client';
import {
  ColumnDef,
  flexRender,
  getCoreRowModel,
  getPaginationRowModel,
  useReactTable,
} from '@tanstack/react-table';
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from '@/components/ui/table';
import { Button } from '@/components/ui/button';
import { Badge } from '@/components/ui/badge';
import { MoreHorizontal, Edit, Trash2 } from 'lucide-react';
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuTrigger,
} from '@/components/ui/dropdown-menu';

interface Workspace {
  id: string;
  name: string;
  slug: string;
  member_count: number;
  created_at: string;
  status: 'active' | 'suspended';
}

const columns: ColumnDef<Workspace>[] = [
  {
    accessorKey: 'name',
    header: 'Name',
    cell: ({ row }) => (
      <div>
        <div className="font-medium">{row.getValue('name')}</div>
        <div className="text-sm text-muted-foreground">/{row.original.slug}</div>
      </div>
    ),
  },
  {
    accessorKey: 'member_count',
    header: 'Members',
    cell: ({ row }) => (
      <Badge variant="secondary">
        {row.getValue('member_count')} members
      </Badge>
    ),
  },
  {
    accessorKey: 'status',
    header: 'Status',
    cell: ({ row }) => (
      <Badge variant={row.getValue('status') === 'active' ? 'default' : 'destructive'}>
        {row.getValue('status')}
      </Badge>
    ),
  },
  {
    accessorKey: 'created_at',
    header: 'Created',
    cell: ({ row }) => new Date(row.getValue('created_at')).toLocaleDateString(),
  },
  {
    id: 'actions',
    cell: ({ row }) => (
      <DropdownMenu>
        <DropdownMenuTrigger asChild>
          <Button variant="ghost" className="h-8 w-8 p-0">
            <MoreHorizontal className="h-4 w-4" />
          </Button>
        </DropdownMenuTrigger>
        <DropdownMenuContent align="end">
          <DropdownMenuItem>
            <Edit className="mr-2 h-4 w-4" />
            Edit
          </DropdownMenuItem>
          <DropdownMenuItem className="text-destructive">
            <Trash2 className="mr-2 h-4 w-4" />
            Delete
          </DropdownMenuItem>
        </DropdownMenuContent>
      </DropdownMenu>
    ),
  },
];

interface WorkspaceTableProps {
  data: Workspace[];
}

export function WorkspaceTable({ data }: WorkspaceTableProps) {
  const table = useReactTable({
    data,
    columns,
    getCoreRowModel: getCoreRowModel(),
    getPaginationRowModel: getPaginationRowModel(),
  });
  
  return (
    <div className="space-y-4">
      <div className="rounded-md border">
        <Table>
          <TableHeader>
            {table.getHeaderGroups().map((headerGroup) => (
              <TableRow key={headerGroup.id}>
                {headerGroup.headers.map((header) => (
                  <TableHead key={header.id}>
                    {header.isPlaceholder
                      ? null
                      : flexRender(header.column.columnDef.header, header.getContext())}
                  </TableHead>
                ))}
              </TableRow>
            ))}
          </TableHeader>
          <TableBody>
            {table.getRowModel().rows?.length ? (
              table.getRowModel().rows.map((row) => (
                <TableRow key={row.id}>
                  {row.getVisibleCells().map((cell) => (
                    <TableCell key={cell.id}>
                      {flexRender(cell.column.columnDef.cell, cell.getContext())}
                    </TableCell>
                  ))}
                </TableRow>
              ))
            ) : (
              <TableRow>
                <TableCell colSpan={columns.length} className="h-24 text-center">
                  No workspaces found.
                </TableCell>
              </TableRow>
            )}
          </TableBody>
        </Table>
      </div>
      
      <div className="flex items-center justify-end space-x-2">
        <Button
          variant="outline"
          size="sm"
          onClick={() => table.previousPage()}
          disabled={!table.getCanPreviousPage()}
        >
          Previous
        </Button>
        <Button
          variant="outline"
          size="sm"
          onClick={() => table.nextPage()}
          disabled={!table.getCanNextPage()}
        >
          Next
        </Button>
      </div>
    </div>
  );
}
```

## Loading States Component
```typescript
// ✅ components/ui/LoadingSpinner.tsx
import { Loader2 } from 'lucide-react';
import { cn } from '@/lib/utils';

interface LoadingSpinnerProps {
  size?: 'sm' | 'md' | 'lg';
  className?: string;
}

export function LoadingSpinner({ size = 'md', className }: LoadingSpinnerProps) {
  const sizeClasses = {
    sm: 'h-4 w-4',
    md: 'h-6 w-6',
    lg: 'h-8 w-8'
  };
  
  return (
    <Loader2 className={cn('animate-spin', sizeClasses[size], className)} />
  );
}

// ✅ Loading page component
export default function Loading() {
  return (
    <div className="flex items-center justify-center min-h-screen">
      <div className="text-center">
        <LoadingSpinner size="lg" />
        <p className="mt-4 text-muted-foreground">Loading...</p>
      </div>
    </div>
  );
}
```

## Error Boundary Component
```typescript
// ✅ components/ui/ErrorBoundary.tsx
'use client';
import { Component, ReactNode } from 'react';
import { Button } from '@/components/ui/button';
import { AlertTriangle } from 'lucide-react';

interface Props {
  children: ReactNode;
  fallback?: ReactNode;
}

interface State {
  hasError: boolean;
  error?: Error;
}

export class ErrorBoundary extends Component<Props, State> {
  constructor(props: Props) {
    super(props);
    this.state = { hasError: false };
  }
  
  static getDerivedStateFromError(error: Error): State {
    return { hasError: true, error };
  }
  
  componentDidCatch(error: Error, errorInfo: any) {
    console.error('ErrorBoundary caught an error:', error, errorInfo);
  }
  
  render() {
    if (this.state.hasError) {
      if (this.props.fallback) {
        return this.props.fallback;
      }
      
      return (
        <div className="flex flex-col items-center justify-center min-h-[400px] p-8">
          <AlertTriangle className="h-12 w-12 text-destructive mb-4" />
          <h2 className="text-xl font-semibold mb-2">Something went wrong</h2>
          <p className="text-muted-foreground text-center mb-4">
            {this.state.error?.message || 'An unexpected error occurred'}
          </p>
          <Button 
            onClick={() => this.setState({ hasError: false, error: undefined })}
            variant="outline"
          >
            Try Again
          </Button>
        </div>
      );
    }
    
    return this.props.children;
  }
}
```