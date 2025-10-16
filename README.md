# Quarkus - Hivemq Client
<!-- ALL-CONTRIBUTORS-BADGE:START - Do not remove or modify this section -->
[![Daily Build](https://github.com/quarkiverse/quarkus-hivemq-client/actions/workflows/daily.yaml/badge.svg)](https://github.com/quarkiverse/quarkus-hivemq-client/actions/workflows/daily.yaml) [![All Contributors](https://img.shields.io/badge/all_contributors-1-orange.svg?style=flat-square)](#contributors-)
<!-- ALL-CONTRIBUTORS-BADGE:END -->

## Introduction

This extension allow usage of the _HiveMQ MQTT Client_ inside a Quarkus App, in JVM and Native mode.

Added with the [SmallRye Reactive Messaging MQTT](https://smallrye.io/smallrye-reactive-messaging/4.3.0/mqtt/mqtt/) allows usage of a new connector type **smallrye-mqtt-hivemq** that will use _HiveMQ MQTT Client_ instead of Vertx MQTT client.

This adds some benefits to the original SmallRye MQTT:

* Battle tested MQTT Client outside of Vertx landscape.
* Management of external CA file for secure connections with self-signed certificates
* Backpressure support integrated with MQTT QOS.
* Automatic and configurable reconnect handling and message redelivery.
* Real Health Check against a configurable topic (defaults to the standard MQTT $SYS/broker/uptime) integrated in Quarkus HealthReport.
* Many others you can read in official documentation [here](https://hivemq.github.io/hivemq-mqtt-client/).

For more information about installation and configuration please read the documentation
[here](https://quarkiverse.github.io/quarkiverse-docs/quarkus-hivemq-client/dev/index.html).

## Development with Claude Code

This project is configured with **Claude Code**, an AI-powered development assistant optimized for Java backend development and maintenance tasks.

### Configuration Overview

The project includes a specialized `.claude/` directory structure with:

- **7 Backend-Focused Agents**: Master Orchestrator, Backend Engineer, Quality Engineer, Performance Optimizer, Security Auditor, Debugger Detective, Deep Researcher, and Content Copywriter
- **Java/Quarkus Patterns**: Best practices, API design, testing strategies, and performance optimization
- **Session-Based Workflow**: Coordinated task management with automatic tracking and documentation

### Quick Start for Maintenance Tasks

#### 1. Basic Workflow

All technical requests automatically trigger the **master-orchestrator** agent:

```bash
# Example maintenance request
"Fix the connection timeout issue in HiveMQ client"

# Claude Code automatically:
# 1. Spawns master-orchestrator to plan the task
# 2. Creates a session file in .claude/tasks/
# 3. Coordinates specialized agents (debugger-detective + deep-researcher)
# 4. Tracks progress with TodoWrite
# 5. Auto-commits when all tasks complete
```

#### 2. Agent System for Maintenance

**Backend Engineer** - Primary Java/Quarkus development, bug fixes, feature implementation
- Use for: Code changes, API modifications, Maven build updates
- Pattern files: `best-java-patterns.md`, `quarkus.md`, `api-auth-patterns.md`

**Quality Engineer** - Testing and quality assurance
- Use for: Unit tests, integration tests, test coverage
- Pattern files: `performance-testing-patterns.md`

**Debugger Detective + Deep Researcher** - Debugging (ALWAYS used together in parallel)
- Use for: Complex bugs, production issues, performance problems
- Automatically researches solutions while investigating

**Performance Optimizer** - JVM tuning and optimization
- Use for: Performance issues, memory leaks, message throughput optimization
- Focus: JVM profiling, HiveMQ client optimization

**Security Auditor** - Security reviews and vulnerability scanning
- Use for: Security audits, dependency updates, vulnerability fixes
- Focus: REST API security, authentication, compliance

#### 3. Common Maintenance Scenarios

**Dependency Updates:**
```
"Update HiveMQ client dependency to version X.Y.Z"
→ master-orchestrator plans update
→ backend-engineer updates pom.xml
→ quality-engineer runs tests
→ Auto-commit when complete
```

**Bug Fixes:**
```
"Fix connection pool exhaustion under load"
→ master-orchestrator creates session
→ debugger-detective + deep-researcher investigate (parallel)
→ backend-engineer implements fix
→ quality-engineer adds regression test
→ Auto-commit when complete
```

**Performance Optimization:**
```
"Optimize message throughput for high-volume scenarios"
→ master-orchestrator plans optimization
→ performance-optimizer analyzes bottlenecks
→ backend-engineer implements changes
→ quality-engineer validates performance
→ Auto-commit when complete
```

**Security Updates:**
```
"Run security audit and fix vulnerabilities"
→ master-orchestrator coordinates validation mode
→ security-auditor + quality-engineer + performance-optimizer (parallel)
→ backend-engineer fixes issues
→ Auto-commit when complete
```

#### 4. Validation Mode (Pre-Release)

For comprehensive quality checks before releases:

```
"Run validation" or "validate security and performance"
→ Triggers parallel execution of:
   - security-auditor (vulnerability scanning, API security)
   - performance-optimizer (JVM profiling, throughput analysis)
   - quality-engineer (test coverage, integration tests)
→ Generates comprehensive validation report
→ Creates remediation tasks for any issues
```

#### 5. Session Management

All work is tracked in session files (`.claude/tasks/`):

- **session-current.md** - Active development session
- **session-NNN.md** - Archived completed sessions
- Each session includes: tasks, decisions, progress, quality gates

#### 6. Auto-Commit on Completion

When all TodoWrite tasks are completed, Claude Code **automatically**:

1. Runs pre-commit checks (Maven verify if available)
2. Stages all modified files
3. Creates commit with format: `"feat: [summary] - Auto-commit after completing N tasks 🤖"`
4. Notifies completion

#### 7. Best Practices

**Think Hard Directive**: Always include "think hard" in requests for maximum analytical depth

**Parallel Execution**: Multiple independent tasks run simultaneously for faster completion

**Evidence-Based**: All decisions backed by research and validation

**Security-First**: All API changes reviewed by security-auditor

**Quality Gates**: Testing and validation required before completion

### Pattern Files Reference

The `.claude/context/rules/` directory contains specialized patterns:

- `best-java-patterns.md` - Java best practices and coding standards
- `java-patterns.md` - Advanced Java patterns and functional programming
- `quarkus.md` - Quarkus framework patterns and extensions
- `api-auth-patterns.md` - REST API design and authentication
- `performance-testing-patterns.md` - JUnit, integration tests, performance testing
- `project-organization-patterns.md` - Maven project structure
- `git-workflow-patterns.md` - Git protocols and workflows

### Example Maintenance Commands

```bash
# Dependency update
"Update Quarkus to version 3.8.0"

# Bug investigation
"Debug why MQTT reconnection fails after network timeout"

# Feature addition
"Add support for MQTT 5 shared subscriptions"

# Performance issue
"Investigate high memory usage during message processing"

# Security review
"Audit REST endpoints for security vulnerabilities"

# Documentation update
"Update README with new configuration options"

# Test coverage
"Add integration tests for connection pool manager"
```

### Session-Based Coordination

Claude Code uses a **session-based workflow** where:

1. **Master Orchestrator** creates a strategic plan
2. **Specialized Agents** execute tasks in parallel or sequentially
3. **TodoWrite** tracks real-time progress
4. **Session Files** maintain complete development history
5. **Auto-Commit** preserves all completed work

All maintenance tasks are documented, tracked, and validated through this coordinated system.

## Contributors ✨

Thanks goes to these wonderful people ([emoji key](https://allcontributors.org/docs/en/emoji-key)):

<!-- ALL-CONTRIBUTORS-LIST:START - Do not remove or modify this section -->
<!-- prettier-ignore-start -->
<!-- markdownlint-disable -->
<table>
  <tr>
    <td align="center"><a href="https://github.com/masini"><img src="https://avatars.githubusercontent.com/u/2060870?v=4?s=100" width="100px;" alt=""/><br /><sub><b>masini</b></sub></a><br /><a href="https://github.com/quarkiverse/quarkus-hivemq-client/commits?author=masini" title="Code">💻</a> <a href="#maintenance-masini" title="Maintenance">🚧</a></td>
  </tr>
  <tr>
    <td align="center"><a href="https://github.com/pjgg"><img src="https://avatars.githubusercontent.com/u/3541131?v=4" width="100px;" alt=""/><br /><sub><b>pjgg</b></sub></a><br /><a href="https://github.com/quarkiverse/quarkus-hivemq-client/commits?author=pjgg" title="Code">💻</a> <a href="#maintenance-pjgg" title="Maintenance">🚧</a></td>
  </tr>
</table>

<!-- markdownlint-restore -->
<!-- prettier-ignore-end -->

<!-- ALL-CONTRIBUTORS-LIST:END -->

This project follows the [all-contributors](https://github.com/all-contributors/all-contributors) specification. Contributions of any kind welcome!
