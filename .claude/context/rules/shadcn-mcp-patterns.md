# Shadcn UI MCP Integration Patterns - Strict Workflow

## CRITICAL: MCP-First Development Protocol

### Absolute Rule 1: NO UI WORK WITHOUT MCP DISCOVERY
**Trigger**: ANY request involving UI components or design
**Mandatory Actions** (frontend-specialist CANNOT proceed without these):
1. `list_components()` - ALWAYS call first to see available components
2. `list_blocks()` - ALWAYS call to identify pre-built solutions
3. Analyze results and determine if blocks can solve the requirement
4. NEVER begin implementation without completing discovery phase

**Violation Consequence**: Implementation will contain errors that MCP demo patterns prevent

### Absolute Rule 2: Demo-First Implementation Protocol
**Trigger**: When any component needs to be implemented
**Mandatory Sequence** (NO EXCEPTIONS):
1. Call `get_component_demo(componentName)` for EVERY component
2. Study the EXACT implementation pattern from demo
3. Implement using the demo pattern as the foundation
4. Only customize AFTER the base pattern is correctly implemented

**Critical Understanding**: The transcript emphasizes "virtually no chance of making errors because you have the exact usage patterns right there"

### Absolute Rule 3: Block Prioritization System
**Trigger**: When building complex UI sections
**Priority Order** (STRICTLY enforced):
1. **First**: Check `list_blocks()` for existing complete solutions
2. **Second**: Use `get_block(blockName)` to get complete implementation
3. **Third**: Customize block for specific needs
4. **Last Resort**: Build from individual components using demo patterns

**Example Block Categories** (from codebase analysis):
- Calendar interfaces → Use calendar blocks
- Dashboard layouts → Use dashboard-01, dashboard-02, etc.
- Authentication → Use login blocks
- Navigation → Use sidebar blocks

## Precise MCP Tool Specifications

### Tool 1: `list_components` 
**Codebase Location**: Queries `apps/v4/registry/new-york-v4/ui/` directory
**Returns**: Array of 46+ component names (accordion, alert, button, calendar, etc.)
**Usage**: MANDATORY first step in every UI task
**Implementation**: Always filter .tsx files and return component names only

### Tool 2: `list_blocks`
**Codebase Location**: Queries `apps/v4/registry/new-york-v4/blocks/` directory  
**Returns**: Categorized blocks (calendar, dashboard, login, sidebar, products, authentication, charts, mail, music)
**Categories**: Each contains multiple pre-built implementations
**Usage**: MANDATORY second step to identify complete solutions

### Tool 3: `get_component_demo`
**Codebase Location**: Retrieves from `apps/v4/registry/new-york-v4/examples/`
**File Pattern**: `{componentName}-demo.tsx`
**Returns**: EXACT usage patterns showing proper implementation
**Critical Rule**: NEVER implement without calling this first

### Tool 4: `get_block`
**Codebase Location**: Retrieves from `apps/v4/registry/new-york-v4/blocks/`
**Two Types**:
- **Simple Blocks**: Single `.tsx` file with complete implementation
- **Complex Blocks**: Directory with main file + `components/` subfolder
**Returns**: Complete source code + dependencies + usage instructions

### Tool 5: `get_component`
**Codebase Location**: Direct access to `apps/v4/registry/new-york-v4/ui/{componentName}.tsx`
**Returns**: Complete TypeScript component source with props and variants
**Usage**: When customization beyond demo patterns is required

### Tool 6: `get_component_metadata`
**Codebase Location**: Parses `registry-ui.ts` file for component metadata
**Returns**: dependencies, registryDependencies, component type
**Usage**: For understanding component requirements and dependencies

## Complete Development Workflow Integration

### Phase 1: MCP-Driven Context Engineering
**Based on Transcript Quote**: "we're returning to the concept of context engineering, but this time it's specifically optimized for front-end development"

**Context Building Process**:
1. **MCP Discovery**: Call `list_components` and `list_blocks` to understand available building blocks
2. **Pattern Retrieval**: Use `get_component_demo` for each needed component to get exact patterns
3. **Architecture Planning**: Use `get_block` for complex sections to get complete implementations
4. **Context Compilation**: Compile all retrieved patterns into implementation plan
5. **Implementation Readiness**: Only proceed when complete context is assembled

### Phase 2: Implementation Following MCP Patterns
**Transcript Principle**: "it never strays from the process. It consistently fetches the context first"

**Implementation Rules**:
1. Follow demo patterns exactly - no deviations
2. Use retrieved block structure as foundation
3. Apply customizations only after core structure is proven working
4. Return to MCP tools if implementation differs from patterns

### Phase 3: Build → Test → Screenshot → Iterate Workflow
**Purpose**: Visual validation and iterative improvement using Playwright MCP

**Complete Workflow**:
1. **Build**: Implement using Shadcn MCP exact patterns
2. **Deploy**: Start development server with implemented components  
3. **Screenshot**: Use Playwright MCP to capture current implementation
4. **Analysis**: Compare screenshots with expected design requirements
5. **Iterate**: If mismatch found, return to Shadcn MCP for pattern adjustment
6. **Validate**: Repeat until visual implementation matches requirements

### Phase 4: TweakCN Theme Integration
**Based on Transcript**: "you use tweak CN to make everything look great"

**Theme Application Workflow**:
1. **Structure Complete**: Ensure MCP patterns are correctly implemented first
2. **Theme Selection**: Guide user to TweakCN (https://tweakcn.com) for visual customization
3. **User Instruction**: "Please visit TweakCN, customize your preferred theme, and copy the configuration code"
4. **Theme Application**: Apply user-selected theme to MCP-built structure
5. **Final Validation**: Use Playwright MCP for final screenshot validation

## Error Prevention Strategies

### Common Implementation Issues Avoided
- Incorrect component prop usage
- Missing required dependencies
- Improper styling integration
- Responsive design inconsistencies
- Accessibility compliance gaps

### MCP-Driven Solutions
- Official demo patterns prevent prop errors
- Complete block implementations show proper integration
- Metadata provides dependency information
- Examples include responsive and accessibility patterns

## Customization and Theming Integration

### Theme Application Workflow
1. Build structure using MCP-provided patterns
2. Apply custom themes using tools like TweakCN
3. Maintain component functionality while customizing appearance
4. Ensure consistency across application

### Design System Integration
- Use MCP patterns as foundation
- Apply project-specific theming
- Maintain shadcn/ui component integrity
- Document custom pattern variations

## Performance Optimization

### MCP-Enhanced Development Speed
- Single-try implementations using correct patterns
- Reduced debugging cycles
- Faster component integration
- Consistent code quality

### Long-term Maintenance Benefits
- Official pattern compliance ensures future compatibility
- Reduced technical debt from proper implementations
- Easier component updates and modifications
- Consistent codebase structure

## Integration with Existing Patterns

### Coordination with Other Rules
- Follows existing TypeScript patterns in codebase
- Integrates with Next.js and React patterns
- Maintains performance optimization requirements
- Supports existing testing and validation approaches

### Project-Specific Adaptations
- Adapt MCP patterns to project requirements
- Maintain consistency with established conventions
- Document any pattern modifications
- Ensure team alignment on MCP usage protocols