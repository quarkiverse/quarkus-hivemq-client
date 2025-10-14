# ClaudeFast Sub-agents Suite

## Overview

This directory contains a comprehensive suite of specialized sub-agents designed to enhance Claude Code development workflows. These agents replace and extend the previous SuperClaude framework with native Claude Code sub-agent support.

**📝 Code Examples Migration**: All implementation examples and code snippets have been moved from agent files to:
- **Implementation patterns**: @.claude/context/rules/
- **Code examples**: @.claude/context/examples/
- **System workflows**: @.claude/context/control/

Agent files now focus on WHAT each agent does (roles, responsibilities, coordination patterns) rather than HOW (specific implementation details).

## Agent Architecture

### Tier 1: Master Orchestration Agent
- **master-orchestrator** - Unified planning and coordination intelligence combining strategic architecture with tactical execution. Handles ALL technical planning, task breakdown, TodoWrite integration, and session orchestration

### Tier 2: Core Development Agents
- **frontend-specialist** - React components, UI implementation, forms, responsive design  
- **backend-engineer** - Server actions, database operations, API development
- **security-auditor** - Security reviews, RLS policies, OWASP compliance
- **quality-engineer** - Testing strategies, test automation, quality assurance

### Tier 3: Specialized Domain Agents
- **performance-optimizer** - Core Web Vitals, bundle optimization, database tuning
- **supabase-specialist** - Database design, RLS policies, Edge Functions, real-time features
- **debugger-detective** - Systematic debugging, root cause analysis, forensic investigation
- **deep-researcher** - Technical research, evidence gathering, Context7 integration
- **content-copywriter** - All content creation: website copy, landing pages, blog posts, email marketing, SEO-optimized content

## Usage Patterns

### Direct Agent Invocation
Use the Task tool to invoke specific agents for specialized tasks:
- @master-orchestrator: analyze codebase and coordinate feature implementation
- @frontend-specialist: create responsive dashboard components
- @security-auditor: review authentication systems

### Auto-selection by Claude
Claude automatically selects appropriate agents based on context:
- "Build a workspace analytics feature with real-time updates" → master-orchestrator coordinates supabase-specialist, frontend-specialist
- "Our app is loading slowly, help optimize performance" → master-orchestrator analyzes and coordinates performance-optimizer

### Multi-agent Coordination
Complex tasks requiring multiple specialists:
- "Implement a complete user invitation system with security and real-time notifications" → master-orchestrator analyzes, plans, and coordinates backend-engineer, security-auditor, supabase-specialist, quality-engineer

## Integration with Project Patterns

### Context Loading Strategy
Each agent is designed to reference relevant patterns from `.claude/context/rules/`:

**Agent Pattern References:**
- master-orchestrator → ALL patterns (comprehensive codebase oversight and living documentation)
- frontend-specialist → nextjs-react-patterns.md, ui-styling-patterns.md, forms-state-patterns.md
- backend-engineer → api-auth-patterns.md, supabase-database-patterns.md
- security-auditor → api-auth-patterns.md, supabase-database-patterns.md
- quality-engineer → performance-testing-patterns.md, playwright-mcp-patterns.md
- master-orchestrator → @.claude/context/control/system-workflows.md, session templates
- performance-optimizer → performance-testing-patterns.md, nextjs-react-patterns.md
- supabase-specialist → supabase-database-patterns.md, api-auth-patterns.md
- debugger-detective → ALL patterns (context-dependent)
- deep-researcher → context7-mcp-patterns.md
- content-copywriter → email-content-patterns.md, project-organization-patterns.md, nextjs-react-patterns.md, typescript-patterns.md

### SuperClaude Migration Map

| SuperClaude Component | Sub-agent Equivalent | Enhancement |
|----------------------|---------------------|-------------|
| `--persona-architect` + `/analyze --code` + Command orchestration | `master-orchestrator` | Combined codebase analysis, architecture, feature coordination + living documentation |
| `--persona-frontend` | `frontend-specialist` | React 19 + Next.js 15 focus |
| `--persona-backend` | `backend-engineer` | Server Actions + Supabase integration |
| `--persona-security` | `security-auditor` | OWASP + RLS policy expertise |
| `--persona-qa` | `quality-engineer` | Modern testing stack (Vitest + Playwright) |
| `--persona-performance` | `performance-optimizer` | Core Web Vitals optimization |
| Context7 MCP | Native web search + specialists | Built-in research capabilities |
| Magic MCP | `frontend-specialist` | Component generation patterns |

## Agent Tool Permissions

### Master Orchestration Agent
- **master-orchestrator**: `read`, `edit`, `multi-edit`, `write`, `grep`, `glob`, `ls`, `web_search`, `task` (full access for comprehensive oversight and living documentation)

### Read-Only Agents (Analysis & Strategy)
- **security-auditor**: `read`, `grep`, `glob`, `web_search`

### Implementation Agents (Full Development)
- **frontend-specialist**: `read`, `edit`, `multi-edit`, `write`, `bash`
- **backend-engineer**: `read`, `edit`, `multi-edit`, `write`, `bash`
- **supabase-specialist**: `read`, `edit`, `multi-edit`, `write`, `bash`
- **performance-optimizer**: `read`, `edit`, `bash`, `web_search`

### Testing & Validation Agents
- **quality-engineer**: `read`, `edit`, `write`, `bash`


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
- **Security Reviews**: Always include `security-auditor` for auth/data access changes
- **Database Work**: Prefer `supabase-specialist` for Supabase-specific implementations

### Agent Chaining Patterns
**Master Orchestration**: master-orchestrator analyzes, plans, and coordinates, then delegates to specialists, followed by quality-engineer validation.

**Security-First Development**: master-orchestrator coordinates overall flow, security-auditor reviews requirements, backend-engineer implements secure APIs, frontend-specialist builds secure UI, quality-engineer validates security testing.

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
- **design-system-curator**: Design system maintenance and evolution
- **api-integration-specialist**: Third-party integrations and webhooks
- **mobile-specialist**: React Native or mobile-specific implementations

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

This sub-agent suite represents a evolution from generic AI assistance to specialized, domain-expert AI collaboration that maintains context awareness while providing focused expertise for complex development workflows.