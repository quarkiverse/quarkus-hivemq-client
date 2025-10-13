# System Workflows & Control Center

## 🎯 Purpose & Scope

This document serves as the **comprehensive master guide** for the ClaudeFast AI development framework, providing:
- **System Architecture Overview**: Complete sub-agent system and coordination patterns
- **Workflow Control**: Multi-agent coordination sequences and execution patterns
- **Documentation Management**: Living documentation maintenance and reference matrix
- **Session Control**: Task management, progress tracking, and continuity systems

## 🏗️ System Architecture

### Directory Structure
```
.claude/
├── agents/                                     # Sub-agent configurations (11 specialists)
│   ├── README.md                               # Agent architecture and usage
│   ├── master-orchestrator.md                  # Strategic coordination, task management, session orchestration
│   ├── frontend-specialist.md                  # React, UI, components
│   ├── backend-engineer.md                     # Server actions, APIs
│   ├── supabase-specialist.md                  # Database, RLS, real-time
│   ├── quality-engineer.md                     # Testing, QA automation
│   ├── performance-optimizer.md                # Core Web Vitals, optimization
│   ├── security-auditor.md                     # Security reviews, compliance
│   ├── debugger-detective.md                   # Bug investigation, root cause
│   ├── deep-researcher.md                      # Research, evidence-based decisions
│   └── content-copywriter.md                   # All content creation: website copy, blogs, email, SEO
├── context/                                    # Development patterns and control
│   ├── control/                                # System control and coordination
│   │   └── system-workflows.md                 # This master guide with TodoWrite integration
│   ├── rules/                                  # Development patterns (15 files)
│   │   ├── api-auth-patterns.md                # API routes, authentication, security
│   │   ├── supabase-database-patterns.md       # Database, RLS, migrations, queries
│   │   ├── nextjs-react-patterns.md            # React and Next.js patterns
│   │   ├── forms-state-patterns.md             # Forms, validation, state management
│   │   ├── ui-styling-patterns.md              # UI styling and component patterns
│   │   ├── typescript-patterns.md              # TypeScript advanced patterns, types
│   │   ├── performance-testing-patterns.md     # Testing, E2E, performance optimization
│   │   ├── email-content-patterns.md           # Email development, content workflows
│   │   ├── tanstack-table-patterns.md          # Advanced data tables, filtering
│   │   ├── nextbase-reference.md               # Starter kit reference patterns
│   │   ├── project-organization-patterns.md    # Project structure patterns
│   │   ├── git-workflow-patterns.md            # Git protocols and workflows
│   │   ├── playwright-mcp-patterns.md          # Playwright testing workflows
│   │   └── shadcn-mcp-patterns.md              # Shadcn UI component patterns
│   └── examples/                               # Practical code examples
└── tasks/                                      # Session-based task management
    ├── session-001.md                          # Completed sessions
    ├── session-002.md                          # Historical development
    └── session-XXX.md                          # Active session tracking
```

### Root Configuration
```
/CLAUDE.md                   # Auto-loaded base configuration
```

## 🤖 Sub-Agent System

### **Core Development Agents**

#### **Frontend Specialist**
- **Purpose**: React components, UI implementation, responsive design
- **Best For**: Components, forms, Shadcn UI, state management
- **Usage**: `Task tool → frontend-specialist`
- **References**: 
  - `nextjs-react-patterns.md`, `ui-styling-patterns.md`, `forms-state-patterns.md`
  - `shadcn-mcp-patterns.md` (MANDATORY MCP workflow)
  - `typescript-component-examples.md`

#### **Backend Engineer**
- **Purpose**: Server actions, API development, database operations
- **Best For**: Server Actions, API routes, database functions, authentication
- **Usage**: `Task tool → backend-engineer`
- **References**: 
  - `api-auth-patterns.md`, `supabase-database-patterns.md`
  - `email-content-patterns.md`
  - `typescript-server-action-examples.md`

#### **Supabase Specialist**
- **Purpose**: Database design, RLS policies, Edge Functions, real-time
- **Best For**: Database schemas, security policies, migrations, real-time features
- **Usage**: `Task tool → supabase-specialist`
- **References**: 
  - `supabase-database-patterns.md` (PRIMARY)
  - `api-auth-patterns.md`
  - `typescript-database-examples.md`

### **Quality & Performance Agents**

#### **Quality Engineer**
- **Purpose**: Testing strategies, test automation, quality assurance
- **Best For**: Unit tests, E2E testing, coverage analysis, TDD
- **Usage**: `Task tool → quality-engineer`
- **References**: 
  - `performance-testing-patterns.md`
  - `playwright-mcp-patterns.md` (MANDATORY MCP workflow)

#### **Performance Optimizer**
- **Purpose**: Core Web Vitals, bundle optimization, performance analysis
- **Best For**: Performance monitoring, optimization strategies, scaling
- **Usage**: `Task tool → performance-optimizer`
- **References**: 
  - `performance-testing-patterns.md`

#### **Security Auditor**
- **Purpose**: Security reviews, vulnerability assessment, RLS validation
- **Best For**: Authentication security, RLS policies, compliance, vulnerability scanning
- **Usage**: `Task tool → security-auditor`
- **References**: 
  - `api-auth-patterns.md`
  - `supabase-database-patterns.md`

### **Specialized Domain Agents**

#### **Debugger Detective**
- **Purpose**: Systematic debugging, root cause analysis, bug investigation
- **Best For**: Complex bugs, production issues, performance debugging
- **Usage**: `Task tool → debugger-detective`
- **References**: 
  - ALL pattern files (for context-specific debugging)
  - `system-workflows.md`

#### **Deep Researcher**
- **Purpose**: Technical research, evidence-based decision making
- **Best For**: Technology evaluation, best practices research, documentation analysis
- **Usage**: `Task tool → deep-researcher`
- **References**: 
  - `context7-mcp-patterns.md` (MANDATORY Context7-first research)
  - ALL pattern files (for project constraints)

#### **Content Copywriter**
- **Purpose**: All content creation including website copy, landing pages, blog posts, email marketing, and SEO-optimized content
- **Best For**: Marketing copy, blog content, email campaigns, landing pages, sales pages, brand voice, SEO optimization
- **Usage**: `Task tool → content-copywriter`
- **References**: 
  - `email-content-patterns.md`
  - `project-organization-patterns.md`
  - `nextjs-react-patterns.md`
  - `typescript-patterns.md`

### **Orchestration Agent**

#### **Master Orchestrator**
- **Purpose**: Comprehensive codebase management, strategic planning, task breakdown, session orchestration
- **Best For**: ALL technical planning, task coordination, session management, architectural oversight
- **Usage**: `Task tool → master-orchestrator`
- **References**: 
  - Session files in `.claude/tasks/`
  - `system-workflows.md` (Complete workflow patterns)
  - ALL pattern files for architectural context
- **Special Role**: SOLE responsibility for ALL documentation updates and session orchestration

## 📋 Workflow Control System

### **Parallel vs Sequential Agent Execution**

#### **Parallel Execution Guidelines**
Maximize efficiency by running independent agents concurrently:
- **Independent Domains**: Execute UI, API, and database work simultaneously when no dependencies exist
- **Multi-Aspect Analysis**: Run security, performance, and quality checks in parallel
- **Research Topics**: Investigate multiple technologies or solutions concurrently
- **Optimization**: Always prefer parallel execution when tasks are truly independent

**Implementation**: Use multiple Task tool invocations in a single response to launch agents in parallel. Always include "think hard" instruction for optimal agent performance.

**🚨 MANDATORY PARALLELIZATION ENFORCEMENT 🚨**

Forced parallel patterns include debug requests (debugger-detective + deep-researcher), validation requests (security-auditor + performance-optimizer + quality-engineer). Conditional parallel for feature development when no dependencies exist. Single agent only for simple tasks like content updates or minor styling. Central AI must delegate all technical work, maximize parallelization, use Debug+Research pattern for debugging, and include "think hard" in all delegations.

#### **Sequential Execution Requirements**
Maintain sequential flow when dependencies exist:
- **Data Dependencies**: Schema must exist before API implementation
- **API Dependencies**: Endpoints must exist before frontend integration
- **Progressive Building**: Core features before enhancements
- **Validation Chain**: Implementation before testing, testing before security review

### **Workflow Mode Selection**

#### **Development Mode (Default)**
**Use For**: Regular feature development (Sessions 1-4), quick iterations, time-sensitive implementations

**Sequence**: User Request → Strategic analysis → Task breakdown → Domain specialists (parallel/sequential) → Progress tracking → Integration review

#### **Research Mode**
**Use For**: Technology evaluation, architecture research, when user requests "research", "evaluate", "compare"

**Sequence**: User Request → Research scope → Task coordination → Research execution (parallel/sequential) → Research tracking → Decision integration

#### **Debug Mode (Enhanced with Mandatory Parallel Research)**
**Use For**: Complex bugs, production issues, when user requests "debug", "investigate", "find root cause"

**NEW MANDATORY PATTERN: Debug+Research Parallel Execution**

**Enhanced Sequence**: User Request → Parallel Investigation (debugger-detective + deep-researcher) → Knowledge synthesis → Specialist implementation → Validation

**Debug+Research Coordination Patterns**:
- Intermittent bugs: Evidence collection + race condition research
- Performance issues: Profiling + optimization strategy research
- Framework errors: Component investigation + framework debugging research
- Database problems: Query analysis + database optimization research
- Authentication bugs: Auth flow investigation + security practice research

**Enforcement**: ALL debugging requests MUST use Debug+Research parallel pattern unless explicitly single-domain

#### **Validation Mode**
**Use For**: Every 5th session, pre-deployment, when user requests "validate", "security", "performance"

**Sequence**: User Request → Validation scope → Task coordination → Validation specialists (parallel) → Issue resolution → Validation tracking → Consolidation

### **Agent Task Routing (Updated with Mandatory Patterns)**
For domain-specific work with enforced parallelization:
- **UI Changes**: `Task tool → frontend-specialist`
- **API Development**: `Task tool → backend-engineer`
- **Database Work**: `Task tool → supabase-specialist`
- **Testing**: `Task tool → quality-engineer`
- **Security Review**: `Task tool → security-auditor`
- **Performance**: `Task tool → performance-optimizer`
- **Debugging**: `Task tool → debugger-detective` + `Task tool → deep-researcher` (MANDATORY PARALLEL)
- **Research**: `Task tool → deep-researcher`
- **Content**: `Task tool → content-copywriter`
- **All Planning & Coordination**: `Task tool → master-orchestrator`

**🚨 CRITICAL NOTE**: Debugging is NO LONGER single-agent. All debugging MUST use Debug+Research parallel pattern.

## 🎯 Context-Aware Pattern Loading

### **Auto-Loading Triggers**
Pattern loading is triggered by context keywords: React components load frontend patterns, server actions load API patterns, database work loads Supabase patterns, authentication loads security patterns, forms load validation patterns, testing loads QA patterns. Debugging triggers mandatory parallel execution with debugger-detective and deep-researcher agents focusing on bug type, framework, and debugging techniques.

### **Enhanced Session-Based Task Management**
Session management follows initialization (auto-load configuration, check previous sessions, create current session, analyze request, load patterns, coordinate agents), execution (agents read/update session files, validate quality gates, sync TodoWrite), continuity (active coordination, real-time progress, context preservation), and completion (validate completion, archive session, extract pending work, update documentation).

## 🔄 Session Lifecycle Management

### **TodoWrite Integration Architecture**

#### **Bidirectional Synchronization System**
The system maintains seamless coordination between session-based development tracking and TodoWrite real-time task execution, ensuring complete visibility and control over development workflows.

The architecture connects Session Tasks through a Sync Engine to TodoWrite Items, coordinated by a State Manager with Progress Tracking providing real-time updates and session documentation.

#### **Task Complexity Mapping Strategy**
Task complexity determines TodoWrite item breakdown: Simple tasks (1-3 complexity) map to single TodoWrite items, moderate tasks (4-6 complexity) break into 2-3 logical phases, complex tasks (7-10 complexity) require 4-8 comprehensive items with validation steps.

#### **Synchronization Protocols**
Session-to-TodoWrite synchronization parses task definitions and creates TodoWrite items based on complexity. TodoWrite-to-Session monitoring tracks item status changes and updates session progress. Bidirectional reconciliation ensures consistency between tracking systems and resolves conflicts.

#### **Session File TodoWrite Integration**
**TodoWrite Integration Section**: Tasks created/completed, dependencies resolved, progress tracking, active items with ID linking and status synchronization

### **Complete Session Workflow**

#### **Phase 1: Session Initialization (Master-Orchestrator)**
- Context checking, session creation, strategic analysis, success criteria definition, workflow setup, TodoWrite integration
- Deliverables: Structured session file, architectural decisions, agent responsibilities, validation checkpoints, risk assessment

#### **Phase 2: Task Coordination (Task-Manager)**
- Task breakdown, dependency mapping, TodoWrite creation, handoff coordination, progress tracking setup
- Deliverables: Task breakdown with complexity, dependency mapping, TodoWrite integration, handoff protocols, tracking framework

#### **Phase 3: Specialist Execution (Domain Agents)**
- Context reading, specialized implementation, decision documentation, progress updates, handoff preparation, quality validation
- Deliverables: Domain implementation, work log with rationale, integration documentation, quality validation, handoff context
```

#### **Phase 4: Session Completion (Master-Orchestrator)**
```yaml
Responsibilities:
  - Validate all session work completed successfully
  - Verify all success criteria met and quality gates passed
  - Extract incomplete work and follow-up items
  - Archive session-current.md as numbered session file
  - Update living documentation based on session outcomes
  - Prepare context for future sessions
  - Execute automated git commit when all tasks complete

Deliverables:
  - Complete session validation and quality assurance
  - Archived session file with full development history
  - Updated living documentation reflecting session outcomes
  - Extracted follow-up items for future development
  - Context preparation for seamless future work
  - Git commit created with all completed work

Automated_Commit_Workflow:
  trigger: "All TodoWrite tasks marked complete"
  process:
    1. Validate all tasks completed successfully
    2. Run pre-commit checks (lint, typecheck if available)
    3. Stage all modified files from task execution
    4. Create commit with format: "feat: [summary] - Auto-commit after completing [N] tasks 🤖"
    5. Notify user of successful commit
    6. Continue with session archival
```

### **Session File Management**

#### **Session Creation Pattern**
```yaml
File_Creation:
  master_orchestrator:
    1. Check_Previous_Context: "Review archived sessions for relevant context"
    2. Load_Template: "Use session-template.md as base structure"
    3. Customize_Workflow: "Adapt agent workflow to specific request"
    4. Define_Success_Criteria: "Establish clear completion requirements"
    5. Initialize_Tracking: "Set up TodoWrite integration framework"

Quality_Assurance:
  - All agent sections include clear responsibilities
  - Success criteria are specific and measurable
  - Handoff requirements are explicitly defined
  - Quality gates are established at appropriate points
  - TodoWrite integration is properly configured
```

#### **Session Execution Pattern**
```yaml
Agent_Execution:
  pre_work:
    1. Read_Complete_Session: "Load full session-current.md context"
    2. Understand_Previous_Work: "Review all prior agent contributions"
    3. Validate_Dependencies: "Ensure prerequisites are met"
    4. Confirm_Responsibilities: "Verify understanding of assigned work"

  during_work:
    1. Document_Decisions: "Record all major decisions with rationale"
    2. Track_Progress: "Update TodoWrite and session metrics"
    3. Validate_Quality: "Perform domain-specific quality checks"
    4. Identify_Issues: "Document blockers and challenges"

  post_work:
    1. Update_Session_File: "Complete detailed work log"
    2. Provide_Next_Context: "Define requirements for next agent"
    3. Validate_Handoff: "Ensure next agent has sufficient context"
    4. Mark_Complete: "Update session metrics and completion status"
```

#### **Session Archival Pattern**

##### **Complete Archival Process**
```yaml
# Phase 1: Session Validation
Validation_Checklist:
  task_completion:
    - All session tasks marked complete
    - All agent responsibilities fulfilled
    - All TodoWrite tasks completed and synced
  
  quality_assurance:
    - All quality gates passed
    - All validation criteria met
    - All integration points validated
  
  documentation_completeness:
    - All agent work logs completed
    - All decisions documented with rationale
    - All handoff requirements satisfied
    - All issues resolved or documented for follow-up

# Phase 2: Content Extraction
Extract_Components:
  session_summary:
    - Primary accomplishments and outcomes
    - Key architectural decisions made
    - Critical challenges encountered and resolved
    - Quality metrics and validation results
  
  follow_up_items:
    - Incomplete tasks requiring future attention
    - Identified improvements and optimizations
    - Recommended next features or enhancements
    - Technical debt items requiring addressing
  
  context_preservation:
    - Architectural patterns established
    - Integration points and dependencies
    - Performance considerations and constraints
    - Security implementations and validations

# Phase 3: Archival Execution
Archive_Steps:
  1. Generate_Summary:
     - Create comprehensive session overview
     - Highlight key outcomes and decisions
     - Document lessons learned and best practices
  
  2. Extract_Follow_Up:
     - Identify incomplete work items
     - Document recommendations for future sessions
     - Extract reusable context and patterns
  
  3. File_Operations:
     - Determine next session number (auto-increment)
     - Rename session-current.md to session-[number].md
     - Move archived file to appropriate location
  
  4. Documentation_Updates:
     - Update architectural decisions based on session outcomes
     - Evolve development patterns based on implementation experience
     - Update cross-references and navigation structures
  
  5. Context_Preparation:
     - Prepare context snippets for future sessions
     - Update project knowledge base with session learnings
     - Maintain historical continuity for development patterns
```

##### **Context Continuity Management**
```yaml
Cross_Session_References:
  previous_session:
    - Reference relevant architectural decisions
    - Import unresolved items from previous work
    - Maintain dependency chains across sessions
  
  architectural_integration:
    - Update architectural-decisions.md with session outcomes
    - Evolve development patterns based on implementation experience
    - Maintain consistency with established architectural principles
  
  knowledge_preservation:
    - Capture reusable patterns and solutions
    - Document successful agent coordination strategies
    - Maintain institutional knowledge about project evolution

Historical_Context_Access:
  recent_sessions:
    - Last 3-5 sessions readily accessible for context
    - Key decisions and patterns easily referenced
    - Unresolved items tracked across session boundaries
  
  archived_sessions:
    - Moved to archive/ after extended period
    - Indexed for searchability and reference
    - Preserved for historical analysis and learning
  
  pattern_evolution:
    - Track how development patterns evolve over time
    - Document successful coordination strategies
    - Identify optimization opportunities based on historical data
```

##### **Archival Quality Control**
```yaml
Completion_Gates:
  technical_validation:
    - All code implementations completed and tested
    - All integration points validated and documented
    - All performance requirements met and verified
  
  documentation_validation:
    - All agent work logs complete with detailed rationale
    - All architectural decisions documented and integrated
    - All handoff requirements satisfied
  
  continuity_validation:
    - All follow-up items properly extracted and documented
    - All context preserved for future session continuity
    - All cross-references updated and validated

Archive_Quality:
  completeness:
    - Session file contains all required sections
    - All agent contributions properly documented
    - All decisions include rationale and context
  
  consistency:
    - Naming conventions followed correctly
    - Cross-references updated and validated
    - Integration with architectural documentation maintained
  
  accessibility:
    - Archived sessions easily discoverable
    - Context extraction readily available
    - Historical patterns trackable across sessions
```

##### **Error Handling and Recovery**
```yaml
Incomplete_Session_Recovery:
  session_interruption:
    - Save current state as session-current.md
    - Document point of interruption and context
    - Preserve work completed for recovery
  
  agent_handoff_failure:
    - Identify point of failure in agent sequence
    - Preserve completed work and context
    - Enable recovery from last successful handoff
  
  quality_gate_failure:
    - Document specific quality issues encountered
    - Preserve work state for remediation
    - Enable targeted fixes without full restart

Data_Loss_Prevention:
  continuous_updates:
    - Agent updates to session file create implicit backups
    - TodoWrite integration provides parallel tracking
    - Quality gates provide validation checkpoints
  
  recovery_procedures:
    - Restore from last successful agent handoff
    - Reconstruct work from TodoWrite tracking
    - Manual recovery with user validation if needed
```

## 📚 Documentation Management

### **Documentation Maintenance Model**

#### **Master Orchestrator** (ALL Documentation Updates)
**Sole Responsibility**: Creating, updating, and maintaining ALL documentation files
- Ensures consistency across all documentation
- Handles cross-references and integration updates
- Maintains living documentation standards
- Resolves documentation conflicts and overlaps

#### **Other Agents** (Documentation Reference)
**Responsibility**: REFERENCE specific documents for their workflows
- Must explicitly reference relevant documents in agent specifications
- Use documents as guidance for domain-specific work
- Report documentation issues to master-orchestrator
- Follow documented patterns and procedures

### **Agent Reference Matrix**

#### **Multi-Domain Patterns** (Referenced by Multiple Agents)
- **Project Organization**: Frontend + Backend + All Agents
- **TypeScript Patterns**: Frontend (primary) + Backend (shared types)
- **Performance Testing**: Quality Engineer (primary) + Performance Optimizer

#### **Domain-Specific Patterns** (Single Agent Reference)
- **Email Content**: Backend Engineer
- **TanStack Table**: Frontend Specialist
- **NextBase Reference**: Frontend Specialist

### **Document Lifecycle**
- **Creation**: Master Orchestrator creates new documents
- **Reference Assignment**: Master Orchestrator ensures appropriate agent references
- **Usage Validation**: Agents report usage patterns and effectiveness
- **Updates**: Master Orchestrator maintains all content based on agent feedback
- **Archival**: Master Orchestrator archives obsolete documents

## ⚡ Performance & Token Optimization

### **Token Economy**
```yaml
Base_Configuration: "~15K tokens (optimized from 47K)"
Pattern_Loading: "5-10K tokens per pattern (on-demand)"
Agent_Coordination: "Minimal overhead through Task tool"
Total_Optimization: "~70% reduction vs monolithic configuration"

Smart_Loading:
  Simple_Request: "15K tokens (base only)"
  Complex_Feature: "25-35K tokens (base + relevant patterns)"
  Full_Architecture: "45-60K tokens (comprehensive analysis)"
```

### **Development Session Workflow**
```yaml
Session_Management:
  1. Load base configuration (CLAUDE.md)
  2. Read navigation guide for system overview
  3. Analyze request to determine complexity and domain
  4. Load relevant patterns based on context triggers
  5. Coordinate appropriate specialists via Task tool
  6. Track progress in session files
  7. Update documentation as needed
```

## 🛠️ Setup & Usage

### **System Status**
- **Ready**: ClaudeFast is ready to use with native Claude Code sub-agents
- **No Installation Required**: Sub-agents work through built-in Task tool
- **Pattern Loading**: Automatic based on context detection
- **Session Tracking**: Managed through .claude/tasks/ directory

### **Best Practices**

#### **Evidence-Based Development**
- **Language**: Use measured, evidence-based language
- **Research**: Leverage deep-researcher for external documentation
- **Validation**: Use quality-engineer for comprehensive testing

#### **Security-First**
- **Authentication**: Always use security-auditor for auth work
- **Database**: RLS policies reviewed by security-auditor
- **Compliance**: Regular security audits for production systems

#### **Quality Standards**
- **Code Quality**: Use quality-engineer for testing strategies
- **Performance**: performance-optimizer for optimization work
- **Documentation**: master-orchestrator maintains living documentation

## 🎯 Validation Requirements

### **No Orphan Documents**
Every file must be explicitly referenced by at least one agent:
- **Pattern Files**: Must be referenced for specific use cases
- **Control Files**: Must be referenced for system understanding
- **Example Files**: Must be referenced for implementation guidance
- **Session Files**: Must be referenced for task coordination

### **Reference Verification**
Each agent specification must explicitly list:
- Which documents it references
- When/how it uses each document
- What specific guidance it follows from each document

### **Cross-Reference Integrity**
Master Orchestrator maintains:
- Document interdependencies
- Cross-reference accuracy
- Consistency across related patterns
- Navigation system alignment

## 📚 Key Files Reference

### **System Control**
- `/CLAUDE.md` - Auto-loaded base configuration
- @.claude/context/control/system-workflows.md - This comprehensive master guide including TodoWrite integration

### **Agent Specifications**
- @.claude/agents/README.md - Sub-agent architecture overview
- @.claude/agents/*.md - Individual agent capabilities and references

### **Development Patterns**
- @.claude/context/rules/*.md - 15 comprehensive development patterns
- @.claude/context/examples/*.md - Practical code implementations

### **Session Management**
- @.claude/tasks/session-current.md - Active session coordination document
- @.claude/tasks/session-[number].md - Archived development sessions
- @.claude/tasks/session-template.md - Comprehensive session template

#### **Session Directory Structure**
```
.claude/tasks/
├── session-current.md          # Active session (if any)
├── session-001.md              # Archived: First development session
├── session-002.md              # Archived: Second development session
├── session-NNN.md              # Archived: Most recent completed session
├── session-template.md         # Template for new sessions
└── archive/                    # Optional: Long-term storage
    ├── session-001.md          # Moved after significant time
    └── session-NNN.md          # Historical sessions
```

#### **Session Naming Convention**
```yaml
Active_Session: "session-current.md"
Archived_Sessions: "session-001.md", "session-002.md", "session-NNN.md"
Auto_Increment: "Next number based on highest existing session number"
```

---

## 🔧 TodoWrite Integration Architecture

### System Architecture
```
┌─────────────────────────────────────────────────────────────┐
│                 Unified Task Management System              │
├─────────────────────────────────────────────────────────────┤
│                                                             │
│  ┌─────────────────┐    ┌─────────────────┐    ┌──────────┐ │
│  │ Session Files   │◄──►│ TodoWrite Sync  │◄──►│ Master   │ │
│  │ (.md)           │    │ (Real-time)     │    │ Orch.    │ │
│  └─────────────────┘    └─────────────────┘    └──────────┘ │
│           │                       │                    │    │
│           │              ┌─────────────────┐           │    │
│           └─────────────►│ Coordinator     │◄──────────┘    │
│                          │ Engine          │                │
│                          └─────────────────┘                │
│                                   │                         │
│  ┌────────────────────────────────┼───────────────────────┐ │
│  │            Archive System      │                       │ │
│  │  ┌─────────────────┐    ┌──────────────────┐           │ │
│  │  │ Completed       │    │ Historical       │           │ │
│  │  │ Sessions        │    │ Context          │           │ │
│  │  └─────────────────┘    └──────────────────┘           │ │
│  └────────────────────────────────────────────────────────┘ │
└─────────────────────────────────────────────────────────────┘
```

### Core Data Models
- **Session Task Structure**: id, title, complexity (1-10), specialist, status, dependencies, TodoWrite mapping, progress tracking
- **TodoWrite Sync**: 1:N mapping with auto-breakdown for complexity > 6, real-time bidirectional updates
- **Complexity Thresholds**: 1-3 (simple, single item), 4-6 (moderate, 2-3 items), 7-10 (complex, 4+ items)

### Error Handling & Recovery
```yaml
Sync_Failure_Scenarios:
  todowrite_unavailable:
    fallback: "session-only-mode"
    recovery: "restore-todowrite-on-reconnect"
    data_loss: "none"
  
  session_file_corrupted:
    fallback: "reconstruct-from-todowrite"
    recovery: "manual-validation-required"
    data_loss: "session-context-only"
  
  mapping_conflicts:
    fallback: "manual-resolution"
    recovery: "user-guided-reconciliation"
    data_loss: "conflicted-items-only"
```

### Configuration Settings
```json
{
  "todoWriteSync": {
    "enabled": true,
    "syncFrequency": "realtime",
    "batchSize": 10,
    "retryAttempts": 3,
    "timeoutMs": 5000
  },
  "taskBreakdown": {
    "complexityThreshold": 6,
    "maxTodoWriteItems": 10,
    "autoBreakdown": true
  },
  "sessionManagement": {
    "consolidatedPath": ".claude/tasks/",
    "archivePath": ".claude/tasks/archive/",
    "continuityValidation": true
  }
}
```

### Performance Optimization
- **Lazy Loading**: Load context only when needed
- **Incremental Sync**: Only sync changed TodoWrite items
- **Batch Operations**: Group multiple updates for efficiency
- **Cache Strategy**: Cache frequently accessed task context

### Success Metrics
- **Sync Success Rate**: >99% successful TodoWrite ↔ session updates
- **Data Consistency**: Zero data loss during sync operations
- **Recovery Time**: <30 seconds for sync failure recovery
- **Session Continuity**: >95% successful session continuation

---

**Maintained By**: Master Orchestrator (this file and ALL documentation)  
**Last Updated**: 2025-08-04 (Enhanced with TodoWrite integration)  
**Reference Validation**: Required for all agent specifications