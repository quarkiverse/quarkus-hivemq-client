# Java Backend Sub-Agents Suite

## Overview

This directory contains a comprehensive suite of specialized sub-agents designed for Java backend development with the Quarkus HiveMQ Client extension. These agents work through native Claude Code sub-agent support, providing focused expertise for backend engineering, testing, security, and documentation.

**Documentation Organization**: All implementation patterns and code examples are organized in:
- **Implementation patterns**: @.claude/context/rules/
- **System workflows**: @.claude/context/control/
- **Code examples**: @.claude/context/examples/

Agent files focus on WHAT each agent does (roles, responsibilities, coordination patterns) rather than HOW (specific implementation details).

## Agent Architecture

### Tier 1: Master Orchestration Agent
- **master-orchestrator** - Unified planning and coordination intelligence combining strategic architecture with tactical execution. Handles ALL technical planning, task breakdown, TodoWrite integration, and session orchestration for Java backend projects

### Tier 2: Core Development Agents
- **backend-engineer** - Java development, Quarkus framework, REST APIs, HiveMQ integration, Maven builds
- **security-auditor** - Security reviews, vulnerability scanning, REST API security, authentication
- **quality-engineer** - JUnit testing, integration tests, test automation, quality assurance

### Tier 3: Specialized Domain Agents
- **performance-optimizer** - JVM optimization, message throughput, performance monitoring, profiling
- **debugger-detective** - Systematic debugging, root cause analysis, forensic investigation
- **deep-researcher** - Technical research, evidence gathering, documentation analysis
- **content-copywriter** - Technical documentation, API docs, README files, javadoc generation

## Usage Patterns

### Direct Agent Invocation
Use the Task tool to invoke specific agents for specialized tasks:
- @master-orchestrator: analyze codebase and coordinate feature implementation
- @backend-engineer: implement HiveMQ message routing feature
- @security-auditor: review REST API security implementation

### Auto-selection by Claude
Claude automatically selects appropriate agents based on context:
- "Add message filtering to HiveMQ client" → master-orchestrator coordinates backend-engineer, quality-engineer
- "The connection pool is experiencing timeouts" → master-orchestrator analyzes and coordinates debugger-detective, deep-researcher, performance-optimizer

### Multi-agent Coordination
Complex tasks requiring multiple specialists:
- "Implement connection pooling with health monitoring and metrics" → master-orchestrator analyzes, plans, and coordinates backend-engineer, quality-engineer, performance-optimizer, security-auditor

## Integration with Project Patterns

### Context Loading Strategy
Each agent is designed to reference relevant patterns from `.claude/context/rules/`:

**Agent Pattern References:**
- master-orchestrator → ALL patterns (comprehensive codebase oversight and living documentation)
- backend-engineer → best-java-patterns.md, java-patterns.md, quarkus.md, api-auth-patterns.md
- security-auditor → api-auth-patterns.md, best-java-patterns.md
- quality-engineer → performance-testing-patterns.md, best-java-patterns.md
- performance-optimizer → performance-testing-patterns.md, quarkus.md, best-java-patterns.md
- debugger-detective → ALL patterns (context-dependent)
- deep-researcher → context7-mcp-patterns.md
- content-copywriter → project-organization-patterns.md, best-java-patterns.md, java-patterns.md, quarkus.md

## Agent Tool Permissions

### Master Orchestration Agent
- **master-orchestrator**: `read`, `edit`, `multi-edit`, `write`, `grep`, `glob`, `ls`, `web_search`, `task` (full access for comprehensive oversight and living documentation)

### Read-Only Agents (Analysis & Strategy)
- **security-auditor**: `read`, `grep`, `glob`, `web_search`

### Implementation Agents (Full Development)
- **backend-engineer**: `read`, `edit`, `multi-edit`, `write`, `bash`
- **performance-optimizer**: `read`, `edit`, `bash`, `web_search`

### Testing & Validation Agents
- **quality-engineer**: `read`, `edit`, `write`, `bash`

### Research & Documentation Agents
- **deep-researcher**: `read`, `web_search`, `grep`, `glob`
- **debugger-detective**: `read`, `grep`, `glob`, `bash`
- **content-copywriter**: `read`, `edit`, `write`

## Quality Standards

### Agent Development Principles
1. **Domain Expertise**: Each agent focuses on specific technical domains
2. **Context Integration**: All agents reference project patterns and examples
3. **Evidence-Based**: Recommendations based on established best practices
4. **Security-First**: Security considerations integrated into all workflows
5. **Performance-Aware**: Performance implications considered in all implementations

### Output Consistency
All agents follow structured output formats:
- **Analysis**: Current state assessment and recommendations
- **Implementation**: Complete code with explanations and integration guidance
- **Quality**: Testing, security, and performance considerations
- **Integration**: How the implementation fits with existing patterns

## Testing & Validation

### Agent Validation Checklist
- [ ] Agent description includes clear use cases and examples
- [ ] Expertise areas are well-defined and focused
- [ ] Integration with project patterns is documented
- [ ] Output format is structured and consistent
- [ ] Tool permissions match agent responsibilities
- [ ] Quality standards are maintained

### Usage Analytics
Track agent effectiveness:
- Most frequently invoked agents
- Success rate of agent recommendations
- Integration quality with project patterns
- User satisfaction with agent outputs

## Best Practices

### When to Use Specific Agents
- **Complex Architecture & Feature Development**: Start with `master-orchestrator` for comprehensive analysis, planning, and coordination
- **Codebase Analysis & Documentation Updates**: Use `master-orchestrator` for living documentation maintenance
- **Performance Issues**: Begin with `master-orchestrator` for analysis, then coordinate `performance-optimizer`
- **Security Reviews**: Always include `security-auditor` for API and authentication changes
- **Testing Strategy**: Coordinate with `quality-engineer` for comprehensive test coverage

### Agent Chaining Patterns
**Master Orchestration**: master-orchestrator analyzes, plans, and coordinates, then delegates to specialists, followed by quality-engineer validation.

**Security-First Development**: master-orchestrator coordinates overall flow, security-auditor reviews requirements, backend-engineer implements secure APIs, quality-engineer validates security testing.

**Performance Optimization**: master-orchestrator analyzes and coordinates optimization strategy, performance-optimizer implements improvements, specialists handle domain-specific optimizations, with documentation updates throughout.

## Evolution & Maintenance

### Agent Improvement Process
1. **Usage Monitoring**: Track which agents are most/least effective
2. **Pattern Updates**: Update agents when project patterns evolve
3. **Capability Enhancement**: Add new skills based on development needs
4. **Quality Refinement**: Improve output quality based on user feedback

### Future Agent Candidates
Based on project evolution:
- **devops-specialist**: Deployment, monitoring, infrastructure
- **api-integration-specialist**: Third-party integrations and webhooks
- **database-specialist**: Database optimization and migration strategies

## Success Metrics

### Developer Experience
- **Reduced Context Switching**: Specialists handle domain-specific tasks
- **Improved Code Quality**: Domain experts ensure best practices
- **Faster Development**: Focused expertise accelerates implementation
- **Better Architecture**: System-level thinking integrated from start

### Technical Quality
- **Security Compliance**: Security-first approach embedded in workflows
- **Performance Standards**: Performance considerations integrated throughout
- **Test Coverage**: QA expertise ensures comprehensive testing
- **Code Consistency**: Specialists ensure domain-specific best practices

This sub-agent suite represents an evolution from generic AI assistance to specialized, domain-expert AI collaboration that maintains context awareness while providing focused expertise for Java backend development workflows.
