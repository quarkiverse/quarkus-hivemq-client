# System Workflows & Control Center

## 🎯 Purpose & Scope

This document serves as the **comprehensive master guide** for Java backend development with the Quarkus HiveMQ Client extension, providing:
- **System Architecture Overview**: Complete sub-agent system and coordination patterns for Java backend
- **Workflow Control**: Multi-agent coordination sequences and execution patterns
- **Documentation Management**: Living documentation maintenance and reference matrix
- **Session Control**: Task management, progress tracking, and continuity systems

## 🏗️ System Architecture

### Directory Structure
```
.claude/
├── agents/                                     # Sub-agent configurations (7 specialists)
│   ├── README.md                               # Agent architecture and usage
│   ├── master-orchestrator.md                  # Strategic coordination, task management, session orchestration
│   ├── backend-engineer.md                     # Java, Quarkus, REST APIs, HiveMQ integration
│   ├── quality-engineer.md                     # JUnit testing, integration tests, QA automation
│   ├── performance-optimizer.md                # JVM optimization, message throughput, performance analysis
│   ├── security-auditor.md                     # Security reviews, vulnerability scanning, compliance
│   ├── debugger-detective.md                   # Bug investigation, root cause analysis
│   ├── deep-researcher.md                      # Research, evidence-based decisions, documentation
│   └── content-copywriter.md                   # Technical documentation, API docs, README files
├── context/                                    # Development patterns and control
│   ├── control/                                # System control and coordination
│   │   └── system-workflows.md                 # This master guide with TodoWrite integration
│   ├── rules/                                  # Development patterns
│   │   ├── api-auth-patterns.md                # REST API design, authentication, security
│   │   ├── best-java-patterns.md               # Java best practices and patterns
│   │   ├── java-patterns.md                    # Advanced Java patterns, functional programming
│   │   ├── quarkus.md                          # Quarkus framework patterns and extensions
│   │   ├── performance-testing-patterns.md     # JUnit, integration tests, performance testing
│   │   ├── project-organization-patterns.md    # Maven project structure
│   │   ├── git-workflow-patterns.md            # Git protocols and workflows
│   │   └── context7-mcp-patterns.md            # Research and documentation workflows
│   └── examples/                               # Practical code examples (Java)
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

#### **Backend Engineer**
- **Purpose**: Java development, Quarkus framework, REST APIs, HiveMQ integration
- **Best For**: Java code, REST endpoints, message broker integration, Maven builds
- **Usage**: `Task tool → backend-engineer`
- **References**:
  - `best-java-patterns.md`, `java-patterns.md`, `quarkus.md`
  - `api-auth-patterns.md`
  - `performance-testing-patterns.md`

### **Quality & Performance Agents**

#### **Quality Engineer**
- **Purpose**: Testing strategies, JUnit tests, integration tests, quality assurance
- **Best For**: Unit tests, integration testing, test coverage analysis, TDD
- **Usage**: `Task tool → quality-engineer`
- **References**:
  - `performance-testing-patterns.md`
  - `best-java-patterns.md`

#### **Performance Optimizer**
- **Purpose**: JVM optimization, message throughput, performance analysis
- **Best For**: Performance monitoring, JVM tuning, message broker optimization
- **Usage**: `Task tool → performance-optimizer`
- **References**:
  - `performance-testing-patterns.md`
  - `quarkus.md`

#### **Security Auditor**
- **Purpose**: Security reviews, vulnerability assessment, code security
- **Best For**: REST API security, authentication, compliance, vulnerability scanning
- **Usage**: `Task tool → security-auditor`
- **References**:
  - `api-auth-patterns.md`
  - `best-java-patterns.md`

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
- **Purpose**: Technical documentation, API documentation, README files, architectural docs
- **Best For**: API documentation, technical guides, README files, javadoc documentation
- **Usage**: `Task tool → content-copywriter`
- **References**:
  - `project-organization-patterns.md`
  - `best-java-patterns.md`
  - `java-patterns.md`
  - `quarkus.md`

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
- **Independent Domains**: Execute API, testing, and documentation work simultaneously when no dependencies exist
- **Multi-Aspect Analysis**: Run security, performance, and quality checks in parallel
- **Research Topics**: Investigate multiple technologies or solutions concurrently
- **Optimization**: Always prefer parallel execution when tasks are truly independent

**Implementation**: Use multiple Task tool invocations in a single response to launch agents in parallel. Always include "think hard" instruction for optimal agent performance.

**🚨 MANDATORY PARALLELIZATION ENFORCEMENT 🚨**

Forced parallel patterns include debug requests (debugger-detective + deep-researcher), validation requests (security-auditor + performance-optimizer + quality-engineer). Conditional parallel for feature development when no dependencies exist. Single agent only for simple tasks like documentation updates. Central AI must delegate all technical work, maximize parallelization, use Debug+Research pattern for debugging, and include "think hard" in all delegations.

#### **Sequential Execution Requirements**
Maintain sequential flow when dependencies exist:
- **API Dependencies**: Core functionality must exist before tests
- **Progressive Building**: Core features before enhancements
- **Validation Chain**: Implementation before testing, testing before security review

### **Workflow Mode Selection**

#### **Development Mode (Default)**
**Use For**: Regular feature development, quick iterations, time-sensitive implementations

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
- Framework errors: Component investigation + Quarkus debugging research
- Message broker problems: HiveMQ analysis + message broker optimization research
- Connection bugs: Connection investigation + pooling best practices research

**Enforcement**: ALL debugging requests MUST use Debug+Research parallel pattern unless explicitly single-domain

#### **Validation Mode**
**Use For**: Pre-deployment, when user requests "validate", "security", "performance"

**Sequence**: User Request → Validation scope → Task coordination → Validation specialists (parallel) → Issue resolution → Validation tracking → Consolidation

### **Agent Task Routing (Updated with Mandatory Patterns)**
For domain-specific work with enforced parallelization:
- **Java Development**: `Task tool → backend-engineer`
- **Testing**: `Task tool → quality-engineer`
- **Security Review**: `Task tool → security-auditor`
- **Performance**: `Task tool → performance-optimizer`
- **Debugging**: `Task tool → debugger-detective` + `Task tool → deep-researcher` (MANDATORY PARALLEL)
- **Research**: `Task tool → deep-researcher`
- **Documentation**: `Task tool → content-copywriter`
- **All Planning & Coordination**: `Task tool → master-orchestrator`

**🚨 CRITICAL NOTE**: Debugging is NO LONGER single-agent. All debugging MUST use Debug+Research parallel pattern.

## 🎯 Context-Aware Pattern Loading

### **Auto-Loading Triggers**
Pattern loading is triggered by context keywords: Java code loads Java patterns, REST APIs load API patterns, Quarkus work loads framework patterns, testing loads QA patterns. Debugging triggers mandatory parallel execution with debugger-detective and deep-researcher agents focusing on bug type, framework, and debugging techniques.

### **Enhanced Session-Based Task Management**
Session management follows initialization (auto-load configuration, check previous sessions, create current session, analyze request, load patterns, coordinate agents), execution (agents read/update session files, validate quality gates, sync TodoWrite), continuity (active coordination, real-time progress, context preservation), and completion (validate completion, archive session, extract pending work, update documentation).

## 🔄 Session Lifecycle Management

### **TodoWrite Integration Architecture**

#### **Bidirectional Synchronization System**
The system maintains seamless coordination between session-based development tracking and TodoWrite real-time task execution, ensuring complete visibility and control over development workflows.

#### **Task Complexity Mapping Strategy**
Task complexity determines TodoWrite item breakdown: Simple tasks (1-3 complexity) map to single TodoWrite items, moderate tasks (4-6 complexity) break into 2-3 logical phases, complex tasks (7-10 complexity) require 4-8 comprehensive items with validation steps.

#### **Synchronization Protocols**
Session-to-TodoWrite synchronization parses task definitions and creates TodoWrite items based on complexity. TodoWrite-to-Session monitoring tracks item status changes and updates session progress. Bidirectional reconciliation ensures consistency between tracking systems and resolves conflicts.

### **Complete Session Workflow**

#### **Phase 1: Session Initialization (Master-Orchestrator)**
- Context checking, session creation, strategic analysis, success criteria definition, workflow setup, TodoWrite integration
- Deliverables: Structured session file, architectural decisions, agent responsibilities, validation checkpoints, risk assessment

#### **Phase 2: Task Coordination**
- Task breakdown, dependency mapping, TodoWrite creation, handoff coordination, progress tracking setup
- Deliverables: Task breakdown with complexity, dependency mapping, TodoWrite integration, handoff protocols, tracking framework

#### **Phase 3: Specialist Execution (Domain Agents)**
- Context reading, specialized implementation, decision documentation, progress updates, handoff preparation, quality validation
- Deliverables: Domain implementation, work log with rationale, integration documentation, quality validation, handoff context

#### **Phase 4: Session Completion (Master-Orchestrator)**
- Validate all session work completed successfully
- Verify all success criteria met and quality gates passed
- Extract incomplete work and follow-up items
- Archive session-current.md as numbered session file
- Update living documentation based on session outcomes
- Prepare context for future sessions
- Execute automated git commit when all tasks complete

**Automated Commit Workflow**:
- Trigger: All TodoWrite tasks marked complete
- Process: Validate tasks → Run pre-commit checks (Maven verify if available) → Stage files → Create commit → Notify user

### **Session File Management**

#### **Session Creation Pattern**
Master orchestrator checks previous context, loads template, customizes workflow, defines success criteria, and initializes tracking with TodoWrite integration.

#### **Session Execution Pattern**
Agents read complete session, understand previous work, validate dependencies, document decisions, track progress, update session file, provide next context, and validate handoff.

#### **Session Archival Pattern**
Validate task completion and quality assurance, extract session summary and follow-up items, generate summary, determine next session number, rename and move files, update documentation, and prepare context for future sessions.

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
- **Project Organization**: Backend Engineer + All Agents
- **Java Patterns**: Backend Engineer (primary) + Quality Engineer (testing)
- **Performance Testing**: Quality Engineer (primary) + Performance Optimizer

#### **Domain-Specific Patterns** (Single Agent Reference)
- **Quarkus**: Backend Engineer
- **API Auth**: Backend Engineer + Security Auditor

### **Document Lifecycle**
- **Creation**: Master Orchestrator creates new documents
- **Reference Assignment**: Master Orchestrator ensures appropriate agent references
- **Usage Validation**: Agents report usage patterns and effectiveness
- **Updates**: Master Orchestrator maintains all content based on agent feedback
- **Archival**: Master Orchestrator archives obsolete documents

## ⚡ Performance & Token Optimization

### **Token Economy**
- Base Configuration: ~15K tokens (optimized)
- Pattern Loading: 5-10K tokens per pattern (on-demand)
- Agent Coordination: Minimal overhead through Task tool
- Total Optimization: ~70% reduction vs monolithic configuration

### **Development Session Workflow**
1. Load base configuration (CLAUDE.md)
2. Read navigation guide for system overview
3. Analyze request to determine complexity and domain
4. Load relevant patterns based on context triggers
5. Coordinate appropriate specialists via Task tool
6. Track progress in session files
7. Update documentation as needed

## 🛠️ Setup & Usage

### **System Status**
- **Ready**: Sub-agent system ready for Java backend development
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
- **API Security**: Security reviews for all REST endpoints
- **Compliance**: Regular security audits for production systems

#### **Quality Standards**
- **Code Quality**: Use quality-engineer for testing strategies
- **Performance**: performance-optimizer for JVM and message broker optimization
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
- @.claude/context/control/system-workflows.md - This comprehensive master guide

### **Agent Specifications**
- @.claude/agents/README.md - Sub-agent architecture overview
- @.claude/agents/*.md - Individual agent capabilities and references

### **Development Patterns**
- @.claude/context/rules/*.md - Java backend development patterns

### **Session Management**
- @.claude/tasks/session-current.md - Active session coordination document
- @.claude/tasks/session-[number].md - Archived development sessions
- @.claude/tasks/session-template.md - Comprehensive session template

## 🔧 TodoWrite Integration Architecture

### System Architecture
Unified Task Management System connecting Session Files through TodoWrite Sync to Master Orchestrator, coordinated by a central engine with Archive System for completed sessions and historical context.

### Core Data Models
- **Session Task Structure**: id, title, complexity (1-10), specialist, status, dependencies, TodoWrite mapping, progress tracking
- **TodoWrite Sync**: 1:N mapping with auto-breakdown for complexity > 6, real-time bidirectional updates
- **Complexity Thresholds**: 1-3 (simple, single item), 4-6 (moderate, 2-3 items), 7-10 (complex, 4+ items)

### Error Handling & Recovery
- todowrite_unavailable: session-only-mode with restore on reconnect
- session_file_corrupted: reconstruct from TodoWrite
- mapping_conflicts: manual resolution with user guidance

### Configuration Settings
TodoWrite sync enabled with realtime frequency, task breakdown with complexity threshold 6, session management with consolidated and archive paths.

### Performance Optimization
- Lazy Loading: Load context only when needed
- Incremental Sync: Only sync changed TodoWrite items
- Batch Operations: Group multiple updates for efficiency
- Cache Strategy: Cache frequently accessed task context

### Success Metrics
- Sync Success Rate: >99% successful TodoWrite ↔ session updates
- Data Consistency: Zero data loss during sync operations
- Recovery Time: <30 seconds for sync failure recovery
- Session Continuity: >95% successful session continuation

---

**Maintained By**: Master Orchestrator (this file and ALL documentation)
**Last Updated**: 2025-10-16 (Java backend focused with TodoWrite integration)
**Reference Validation**: Required for all agent specifications
**Project Context**: Quarkus HiveMQ Client Extension - Pure Java Backend Development
