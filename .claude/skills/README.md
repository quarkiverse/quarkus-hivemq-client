# Claude Skills Directory

## Overview

This directory contains specialized skills that extend Claude's capabilities for the Quarkus HiveMQ Client extension project. Skills provide domain-specific knowledge, workflows, and tool integrations for common development tasks.

## Available Skills

### 1. quarkus-native-test

**Purpose**: Execute Quarkus native image tests using GraalVM/Mandrel builder

**When to Use**:
- Running native tests
- Validating GraalVM compatibility
- Testing native compilation
- Debugging native-specific issues
- Pre-deployment validation for native deployments

**Key Features**:
- Automated native test execution with progress monitoring
- Comprehensive error handling for common native compilation issues
- Pre-execution validation (Docker/Podman, resources)
- Performance metrics and validation
- Project-specific context for HiveMQ client

**Command**:
```bash
./mvnw -V -B -am clean verify -Dnative -Dquarkus.native.builder-image=quay.io/quarkus/ubi9-quarkus-mandrel-builder-image:jdk-21
```

**Invocation**: User says "run native tests" or "validate native compilation"

### 2. skill-creator

**Purpose**: Guide for creating effective skills

**When to Use**:
- Creating new skills
- Updating existing skills
- Learning skill creation best practices

**Key Features**:
- Step-by-step skill creation process
- Bundled resources guidance (scripts, references, assets)
- Progressive disclosure design principles
- Validation and packaging instructions

**Invocation**: User says "create a new skill" or "help me build a skill"

### 3. template-skill

**Purpose**: Template for creating new skills

**When to Use**:
- Starting point for new skill development
- Reference for skill structure and format

**Key Features**:
- Proper YAML frontmatter format
- Standard directory structure
- Placeholder sections for customization

## Skill Structure

Each skill follows this standard structure:

```
skill-name/
├── SKILL.md          # Main skill implementation (required)
│   ├── YAML frontmatter (name, description)
│   └── Markdown instructions
├── README.md         # Skill documentation (optional)
└── Bundled Resources (optional)
    ├── scripts/      # Executable code
    ├── references/   # Documentation and reference material
    └── assets/       # Files used in output
```

## How Skills Work

Skills use a three-level loading system for efficient context management:

1. **Metadata** (name + description): Always in context (~100 words)
2. **SKILL.md body**: Loaded when skill triggers (<5k words)
3. **Bundled resources**: Loaded as needed by Claude (unlimited)

## Creating New Skills

To create a new skill:

1. Use the `skill-creator` skill for guidance
2. Create a new directory in `.claude/skills/`
3. Add `SKILL.md` with proper YAML frontmatter
4. Include clear description of when to use the skill
5. Write imperative/infinitive form instructions
6. Add bundled resources as needed (scripts, references, assets)
7. Document the skill in this README

## Skill Development Best Practices

- **Clear Triggers**: Define specific user requests that invoke the skill
- **Comprehensive Guidance**: Include error handling, validation, and troubleshooting
- **Project Context**: Tailor skills to project-specific requirements
- **Progressive Disclosure**: Keep SKILL.md lean, use bundled resources for details
- **Objective Language**: Use imperative form, avoid second person
- **Validation**: Test skills with real user scenarios before deployment

## Integration with Development Workflow

Skills integrate seamlessly with the Quarkus HiveMQ Client development workflow:

- **Planning**: master-orchestrator uses skills for strategic planning
- **Implementation**: backend-engineer leverages skills for specialized tasks
- **Validation**: quality-engineer uses skills for testing strategies
- **Debugging**: debugger-detective applies skills for troubleshooting

## Skill Maintenance

**Maintained By**: Master Orchestrator and Backend Engineer
**Last Updated**: 2025-10-21
**Project**: Quarkus HiveMQ Client Extension

## Additional Resources

- [Quarkus Documentation](https://quarkus.io/guides/)
- [GraalVM Native Image](https://www.graalvm.org/latest/reference-manual/native-image/)
- [Claude Code Documentation](https://docs.anthropic.com/)