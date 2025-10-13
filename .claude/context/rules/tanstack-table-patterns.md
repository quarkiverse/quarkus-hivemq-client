---
description: Advanced Data Table Filter component with TanStack Table integration
alwaysApply: true
---

# Advanced TanStack Table Filter Patterns

## Overview

The Data Table Filter is a powerful add-on for your existing data table, providing key building blocks for an advanced filtering experience. It is library-agnostic and supports both client-side and server-side filtering strategies.

- **Key Components**:
    - A React hook, `useDataTableFilters()`, which exposes your data table filters state.
    - A `<DataTableFilter />` component, built with shadcn/ui and inspired by Linear's design.
- **Strategies**: Supports `client` (local browser filtering) and `server` (server applies filters) strategies.
- **Column Types Supported**: `text`, `number`, `date`, `option`, and `multiOption`.
- **Integrations**: TanStack Table, nuqs (for URL state), and others.

## Prerequisites

- A working `<DataTable />` component using TanStack Table and shadcn/ui
- Client-side filtering enabled
- TypeScript and React 18+
- (Optional) nuqs and zod for URL filter state

## Installation

From the command line, install the component and its dependencies into your project:

```sh
pnpm dlx shadcn@latest add https://ui.bazza.dev/r/filters
```

For internationalization support (optional):
```sh
pnpm dlx shadcn@latest add https://ui.bazza.dev/r/filters/i18n
```
When prompted (y/N), explicitly overwrite the `lib/i18n.ts` file if you are installing i18n.

For TanStack Table integration (if using client-side filtering with TanStack Table):
```sh
pnpm dlx shadcn@latest add https://ui.bazza.dev/r/filters/tst
```

## Core Concepts

### Strategy
The `FilterStrategy` decides where filtering happens:
- **`client`**: The client receives all table data and filters it locally.
- **`server`**: The client sends filter requests to the server. The server applies filters and sends back only filtered data.

### Column Data Types
Defines the type of data for each filterable column:
```typescript
export type ColumnDataType =
  | 'text'         // Text data
  | 'number'       // Numerical data
  | 'date'         // Dates
  | 'option'       // Single-valued option (e.g. status)
  | 'multiOption'  // Multi-valued option (e.g. labels)
```

### Filters State (`FiltersState`)
The state of applied filters is represented as `FilterModel[]`:
```typescript
export type FilterModel<TType extends ColumnDataType = any> = {
  columnId: string;
  type: TType; // e.g., 'option', 'text'
  operator: FilterOperators[TType]; // e.g., 'is', 'contains'
  values: FilterValues<TType>; // The actual filter values
};

export type FiltersState = FilterModel[];
```
Each `FilterModel` represents a single filter for a specific column.

### Column Options
For `option` and `multiOption` columns, options are defined as:
```typescript
export interface ColumnOption {
  label: string; // Display name
  value: string; // Unique internal value
  icon?: React.ReactElement | React.ElementType; // Optional icon
}
```

### Column Configuration (`ColumnConfig`)
Each filterable column needs a configuration, created using a type-safe builder:
```typescript
import { createColumnConfigHelper } from '@/components/data-table-filter/core/filters';
import type { Issue } from './your-data-types'; // Your data model type

const columnHelper = createColumnConfigHelper<Issue>();

// Create column configuration
const columnConfig = {
  status: columnHelper.optionColumn({
    label: 'Status',
    options: [
      { label: 'Todo', value: 'todo', icon: Circle },
      { label: 'In Progress', value: 'in-progress', icon: Timer },
      { label: 'Done', value: 'done', icon: CheckCircle },
      { label: 'Canceled', value: 'canceled', icon: XCircle }
    ]
  }),
  priority: columnHelper.optionColumn({
    label: 'Priority',
    options: priorityOptions
  }),
  title: columnHelper.textColumn({
    label: 'Title'
  }),
  createdAt: columnHelper.dateColumn({
    label: 'Created At'
  }),
  labels: columnHelper.multiOptionColumn({
    label: 'Labels',
    options: labelOptions
  })
};
```

## Basic Implementation

### 1. Define Your Data Model
```typescript
// types/issue.ts
export interface Issue {
  id: string;
  title: string;
  status: 'todo' | 'in-progress' | 'done' | 'canceled';
  priority: 'low' | 'medium' | 'high';
  labels: string[];
  createdAt: Date;
}
```

### 2. Create Column Configuration
```typescript
// components/issues-table/column-config.ts
import { createColumnConfigHelper } from '@/components/data-table-filter/core/filters';
import type { Issue } from '@/types/issue';

const columnHelper = createColumnConfigHelper<Issue>();

export const columnConfig = {
  status: columnHelper.optionColumn({
    label: 'Status',
    options: [
      { label: 'Todo', value: 'todo', icon: Circle },
      { label: 'In Progress', value: 'in-progress', icon: Timer },
      { label: 'Done', value: 'done', icon: CheckCircle },
      { label: 'Canceled', value: 'canceled', icon: XCircle }
    ]
  }),
  priority: columnHelper.optionColumn({
    label: 'Priority',
    options: [
      { label: 'Low', value: 'low', icon: ArrowDown },
      { label: 'Medium', value: 'medium', icon: ArrowRight },
      { label: 'High', value: 'high', icon: ArrowUp }
    ]
  }),
  title: columnHelper.textColumn({
    label: 'Title'
  }),
  createdAt: columnHelper.dateColumn({
    label: 'Created At'
  }),
  labels: columnHelper.multiOptionColumn({
    label: 'Labels',
    options: [
      { label: 'Bug', value: 'bug', icon: Bug },
      { label: 'Feature', value: 'feature', icon: Sparkles },
      { label: 'Documentation', value: 'documentation', icon: FileText }
    ]
  })
};
```

### 3. Use the Filter Hook
```typescript
// components/issues-table/issues-table.tsx
import { useDataTableFilters } from '@/components/data-table-filter/core/filters';
import { DataTableFilter } from '@/components/data-table-filter/data-table-filter';
import { columnConfig } from './column-config';

export function IssuesTable({ data }: { data: Issue[] }) {
  const { filters, addFilter, updateFilter, removeFilter, resetFilters } = 
    useDataTableFilters({
      strategy: 'client',
      columnConfig,
      onFilterChange: (filters) => {
        console.log('Filters changed:', filters);
      }
    });

  return (
    <div className="space-y-4">
      <DataTableFilter 
        filters={filters}
        onAddFilter={addFilter}
        onUpdateFilter={updateFilter}
        onRemoveFilter={removeFilter}
        onResetFilters={resetFilters}
        columnConfig={columnConfig}
      />
      
      <DataTable 
        columns={columns} 
        data={data}
        // Pass filters to your table
      />
    </div>
  );
}
```

## Advanced Usage

### Server-Side Filtering with URL State
```typescript
import { useDataTableFilters, getColumnConfig } from '@/components/data-table-filter/core/filters';
import { parseAsJson } from 'nuqs';
import { useQueryStates } from 'nuqs';

export function ServerFilteredTable() {
  const [urlFilters, setUrlFilters] = useQueryStates({
    filters: parseAsJson<FiltersState>().withDefault([])
  });

  const { filters, addFilter, updateFilter, removeFilter, resetFilters } = 
    useDataTableFilters({
      strategy: 'server',
      columnConfig,
      initialState: urlFilters.filters,
      onFilterChange: (newFilters) => {
        setUrlFilters({ filters: newFilters });
      }
    });

  // Use filters in your query
  const { data, isLoading } = useQuery({
    queryKey: ['issues', filters],
    queryFn: () => fetchFilteredIssues(filters)
  });

  return (
    <div className="space-y-4">
      <DataTableFilter 
        filters={filters}
        onAddFilter={addFilter}
        onUpdateFilter={updateFilter}
        onRemoveFilter={removeFilter}
        onResetFilters={resetFilters}
        columnConfig={columnConfig}
      />
      
      {isLoading ? (
        <TableSkeleton />
      ) : (
        <DataTable columns={columns} data={data} />
      )}
    </div>
  );
}
```

### TanStack Table Integration
```typescript
import { useDataTableFilters } from '@/components/data-table-filter/core/filters';
import { applyFilters } from '@/components/data-table-filter/integrations/tanstack-table';
import { 
  useReactTable,
  getCoreRowModel,
  getFilteredRowModel,
  ColumnDef
} from '@tanstack/react-table';

export function IntegratedDataTable({ data }: { data: Issue[] }) {
  const { filters, ...filterHandlers } = useDataTableFilters({
    strategy: 'client',
    columnConfig
  });

  const table = useReactTable({
    data,
    columns,
    state: {
      globalFilter: filters // Pass filters as globalFilter
    },
    onGlobalFilterChange: () => {}, // Handled by useDataTableFilters
    globalFilterFn: (row, columnId, filterValue) => {
      // Apply the filters using the helper
      return applyFilters(row, filterValue as FiltersState, columnConfig);
    },
    getCoreRowModel: getCoreRowModel(),
    getFilteredRowModel: getFilteredRowModel()
  });

  return (
    <>
      <DataTableFilter 
        filters={filters}
        {...filterHandlers}
        columnConfig={columnConfig}
      />
      
      <Table>
        {/* Render table using TanStack Table instance */}
      </Table>
    </>
  );
}
```

### Custom Filter Operators
```typescript
// Define custom operators for specific column types
const customColumnConfig = {
  amount: columnHelper.numberColumn({
    label: 'Amount',
    operators: [
      { label: 'Equals', value: '=' },
      { label: 'Greater than', value: '>' },
      { label: 'Less than', value: '<' },
      { label: 'Between', value: 'between' }
    ]
  }),
  tags: columnHelper.multiOptionColumn({
    label: 'Tags',
    options: tagOptions,
    operators: [
      { label: 'Has all of', value: 'hasAllOf' },
      { label: 'Has any of', value: 'hasAnyOf' },
      { label: 'Has none of', value: 'hasNoneOf' }
    ]
  })
};
```

### Internationalization (i18n)
```typescript
// lib/i18n.ts
export const filterI18n = {
  filters: {
    title: 'Filters',
    addFilter: 'Add filter',
    reset: 'Reset',
    // ... other translations
  },
  operators: {
    is: 'is',
    isNot: 'is not',
    contains: 'contains',
    doesNotContain: 'does not contain',
    // ... other operators
  }
};

// Use in component
import { filterI18n } from '@/lib/i18n';

<DataTableFilter 
  filters={filters}
  {...filterHandlers}
  columnConfig={columnConfig}
  i18n={filterI18n}
/>
```

## Filter Operators Reference

### Text Operators
- `contains` - Text contains value
- `doesNotContain` - Text does not contain value
- `is` - Text exactly matches
- `isNot` - Text does not match
- `startsWith` - Text starts with value
- `endsWith` - Text ends with value
- `isEmpty` - Text is empty
- `isNotEmpty` - Text is not empty

### Number Operators
- `=` - Equals
- `!=` - Not equals
- `>` - Greater than
- `<` - Less than
- `>=` - Greater than or equal
- `<=` - Less than or equal

### Date Operators
- `is` - Date equals
- `isNot` - Date not equals
- `isBefore` - Date is before
- `isAfter` - Date is after
- `isOnOrBefore` - Date is on or before
- `isOnOrAfter` - Date is on or after

### Option Operators
- `is` - Option equals
- `isNot` - Option not equals
- `isEmpty` - No option selected
- `isNotEmpty` - Has option selected

### Multi-Option Operators
- `hasAnyOf` - Has any of the selected options
- `hasAllOf` - Has all of the selected options
- `hasNoneOf` - Has none of the selected options
- `isEmpty` - No options selected
- `isNotEmpty` - Has at least one option

## Best Practices

### Do's
- Define column configs with proper TypeScript types
- Use consistent option values across your app
- Implement loading states for server-side filtering
- Persist filter state in URL for shareable links
- Use proper icons for visual clarity
- Test filter combinations thoroughly
- Provide clear operator labels

### Don'ts
- Don't mix client and server strategies
- Don't forget to handle empty states
- Don't use complex objects as option values
- Don't ignore performance with large datasets
- Don't forget accessibility (keyboard navigation)

### Performance Tips
- For large datasets (>5000 rows), use server-side filtering
- Debounce text input filters
- Use virtual scrolling with filtered results
- Memoize filter calculations
- Consider pagination with filters

## Resources
- [Official Documentation](https://ui.bazza.dev/docs/data-table-filter)
- [GitHub Repository](https://github.com/bazza/ui)
- [Live Demo](https://ui.bazza.dev/docs/data-table-filter#demo)