# Quarkus Native Test Skill

## Overview

This skill enables Claude to execute Quarkus native image tests using GraalVM/Mandrel native-image compilation. It provides comprehensive guidance for running native tests, handling errors, and validating native image compatibility for the Quarkus HiveMQ Client extension.

## Purpose

Native testing validates that Quarkus applications:
- Compile correctly to native executables using GraalVM/Mandrel
- Function properly in GraalVM's substrate VM environment
- Meet performance requirements (fast startup, low memory footprint)
- Handle reflection, serialization, and resource loading correctly in native mode

## When to Use

Invoke this skill when users request:
- Running native tests
- Validating GraalVM compatibility
- Testing native compilation
- Debugging native-specific issues
- Pre-deployment validation for native deployments

## Command Executed

```bash
./mvnw -V -B -am clean verify -Dnative -Dquarkus.native.builder-image=quay.io/quarkus/ubi9-quarkus-mandrel-builder-image:jdk-21
```

## Key Features

- **Pre-execution validation**: Checks Docker/Podman availability and system resources
- **Progress monitoring**: Tracks compilation phases and provides status updates
- **Comprehensive error handling**: Covers common native compilation issues
- **Performance metrics**: Reports startup time, memory usage, and build time
- **Project-specific context**: Optimized for Quarkus HiveMQ Client extension

## Prerequisites

- Docker or Podman installed and running
- Minimum 4GB RAM, 2 CPU cores (8GB RAM, 4 cores recommended)
- 2-5GB free disk space for build artifacts
- Internet connection for downloading builder image and dependencies

## Expected Duration

- Small projects: 2-5 minutes
- Medium projects: 5-15 minutes
- Large projects: 15-30+ minutes

## Common Issues Handled

1. Docker/Podman not available
2. Insufficient memory during compilation
3. Reflection configuration missing
4. Resource loading issues in native mode
5. Build timeouts
6. Test failures specific to native mode
7. Native image build failures

## Success Indicators

- `BUILD SUCCESS` message in output
- All tests passing (0 failures, 0 errors)
- Native executable created in `target/` directory
- Fast startup time (< 100ms for small apps)
- Lower memory footprint than JVM equivalent

## Skill Structure

```
quarkus-native-test/
├── SKILL.md        # Main skill implementation with detailed instructions
└── README.md       # This file - skill documentation and overview
```

## Usage Example

**User**: "Run native tests"

**Claude**:
1. Validates prerequisites (Docker/Podman, resources)
2. Displays execution notice with expected duration
3. Executes native test command
4. Monitors progress through compilation phases
5. Validates results and reports status
6. Provides next steps based on outcome

## Integration with Development Workflow

1. **Feature Development**: Develop with JVM testing
2. **Local Validation**: Run quick JVM tests first
3. **Pre-Commit Check**: Run native tests for critical changes
4. **CI Validation**: Automated native testing in CI/CD
5. **Deployment**: Final native test run before release

## Project Context

**Quarkus HiveMQ Client Extension**
- Technology: Java, Quarkus, HiveMQ MQTT client, Maven
- Native Requirements: GraalVM/Mandrel JDK 21 compatibility
- Critical Tests: MQTT connectivity, message handling, client lifecycle in native mode
- Considerations: HiveMQ reflection config, Netty native transport, SSL/TLS certificate loading

## Maintenance

**Maintained By**: Backend Engineer and Master Orchestrator
**Last Updated**: 2025-10-21
**Project**: Quarkus HiveMQ Client Extension

## Additional Resources

- [Quarkus Native Guide](https://quarkus.io/guides/building-native-image)
- [GraalVM Native Image](https://www.graalvm.org/latest/reference-manual/native-image/)
- [Mandrel Project](https://github.com/graalvm/mandrel)
- [Quarkus Testing Guide](https://quarkus.io/guides/getting-started-testing)