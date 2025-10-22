# Quarkus JVM Test Execution

Execute standard Quarkus JVM tests with comprehensive monitoring and validation.

## Pre-Execution Validation

First, verify the build environment is ready:

```bash
# Check if mvnw is executable
if [ ! -x "/home/pagonzal/Documents/workspace/quarkus-hivemq-client/mvnw" ]; then
    echo "Making mvnw executable..."
    chmod +x /home/pagonzal/Documents/workspace/quarkus-hivemq-client/mvnw
fi

# Check available disk space (JVM tests need ~500MB)
available_space=$(df -BM /home/pagonzal/Documents/workspace/quarkus-hivemq-client | tail -1 | awk '{print $4}' | sed 's/M//')
if [ "$available_space" -lt 500 ]; then
    echo "⚠ WARNING: Low disk space (${available_space}MB available). Build may fail."
    echo "Consider freeing up disk space before proceeding."
fi

# Check Java version
if command -v java &> /dev/null; then
    java_version=$(java -version 2>&1 | head -1)
    echo "✓ Java detected: $java_version"
else
    echo "✗ ERROR: Java not found. JVM tests require Java to be installed."
    exit 1
fi

echo ""
echo "======================================"
echo "Starting Quarkus JVM Test Build"
echo "======================================"
echo "⏱  Estimated time: 2-10 minutes"
echo "☕ Mode: JVM (standard tests)"
echo "🎯 Goal: clean verify"
echo "======================================"
echo ""
```

## Execute JVM Build and Tests

Run the JVM verification with proper timeout and monitoring:

```bash
# Set working directory
cd /home/pagonzal/Documents/workspace/quarkus-hivemq-client

# Start timestamp
start_time=$(date +%s)
echo "⏰ Build started at: $(date '+%Y-%m-%d %H:%M:%S')"
echo ""

# Execute JVM build with verify goal
# Using timeout of 15 minutes (900 seconds) - sufficient for JVM tests
timeout 900 ./mvnw -V -B -am clean verify 2>&1 | tee /tmp/jvm-test-output.log

# Capture exit code
exit_code=${PIPESTATUS[0]}

# End timestamp
end_time=$(date +%s)
duration=$((end_time - start_time))
minutes=$((duration / 60))
seconds=$((duration % 60))

echo ""
echo "======================================"
echo "JVM Test Build Completed"
echo "======================================"
echo "⏱  Duration: ${minutes}m ${seconds}s"
echo "⏰ Completed at: $(date '+%Y-%m-%d %H:%M:%S')"

# Handle different exit codes
if [ $exit_code -eq 124 ]; then
    echo "❌ Build TIMEOUT after 15 minutes"
    echo ""
    echo "Possible causes:"
    echo "  - Hanging tests or infinite loops"
    echo "  - Network issues downloading dependencies"
    echo "  - Insufficient system resources"
    echo ""
    echo "Recommendations:"
    echo "  - Review test logs for hanging tests"
    echo "  - Check network connectivity to Maven Central"
    echo "  - Run with -X flag for debug output: ./mvnw -X clean verify"
    exit 124
elif [ $exit_code -eq 0 ]; then
    echo "✅ Build SUCCESSFUL"
    echo ""
    echo "All JVM tests passed successfully!"
    echo ""

    # Find and display build artifacts
    echo "Build Artifacts:"
    find /home/pagonzal/Documents/workspace/quarkus-hivemq-client -name "*.jar" -path "*/target/*" -type f 2>/dev/null | while read -r jar; do
        size=$(du -h "$jar" | cut -f1)
        echo "  📦 $jar ($size)"
    done

    # Show test summary
    echo ""
    echo "Test Summary:"
    echo "======================================"
    grep -E "Tests run:" /tmp/jvm-test-output.log | tail -10 | while read -r line; do
        if echo "$line" | grep -q "Failures: 0, Errors: 0"; then
            echo "  ✅ $line"
        else
            echo "  $line"
        fi
    done

    # Show overall build success
    if grep -q "BUILD SUCCESS" /tmp/jvm-test-output.log; then
        build_time=$(grep "Total time:" /tmp/jvm-test-output.log | tail -1)
        echo ""
        echo "  🎉 $build_time"
    fi

    exit 0
else
    echo "❌ Build FAILED (exit code: $exit_code)"
    echo ""
    echo "Error Analysis:"
    echo "======================================"

    # Extract and display relevant error information
    if grep -q "OutOfMemoryError" /tmp/jvm-test-output.log; then
        echo "⚠ Out of Memory Error detected"
        echo "  - Try: export MAVEN_OPTS='-Xmx2g'"
        echo "  - Close other applications to free memory"
    fi

    if grep -q "Could not resolve dependencies" /tmp/jvm-test-output.log; then
        echo "⚠ Dependency resolution error"
        echo "  - Check network connectivity"
        echo "  - Verify Maven settings.xml configuration"
        echo "  - Try: ./mvnw dependency:purge-local-repository"
    fi

    if grep -q "Compilation failure" /tmp/jvm-test-output.log; then
        echo "⚠ Compilation error detected"
        echo "  - Review compiler errors below"
        echo "  - Check Java version compatibility"
    fi

    # Show failed tests summary
    if grep -q "Tests run:" /tmp/jvm-test-output.log; then
        echo ""
        echo "Failed Tests:"
        echo "======================================"
        grep -B2 -A5 "<<< FAILURE!" /tmp/jvm-test-output.log | head -30

        failed_count=$(grep -c "<<< FAILURE!" /tmp/jvm-test-output.log 2>/dev/null || echo "0")
        error_count=$(grep -c "<<< ERROR!" /tmp/jvm-test-output.log 2>/dev/null || echo "0")

        echo ""
        echo "Summary: $failed_count test failure(s), $error_count test error(s)"
    fi

    # Show last 20 error lines
    echo ""
    echo "Recent error messages:"
    echo "======================================"
    grep -E "ERROR|FAILURE|\[ERROR\]" /tmp/jvm-test-output.log | tail -20

    echo ""
    echo "Full log available at: /tmp/jvm-test-output.log"
    echo "To view: less /tmp/jvm-test-output.log"

    exit $exit_code
fi
```

## Post-Execution Summary

After completion, the command provides:

- **Success**: Build artifacts, test summary, build duration, success confirmation
- **Failure**: Error analysis, failed test details, troubleshooting recommendations, log file location
- **Timeout**: Clear timeout message with debugging suggestions

## Usage

Simply run:
```
/verify
```

Or use the alias:
```
/test
```

## Notes

- **Duration**: JVM tests typically take 2-10 minutes depending on system resources
- **Requirements**: Java JDK 21+ must be installed
- **Disk Space**: Requires approximately 500MB free space
- **Memory**: Recommended 2GB+ RAM available
- **Timeout**: Set to 15 minutes (configurable in script)
- **Logs**: Full build output saved to `/tmp/jvm-test-output.log`

## Comparison with Native Tests

| Aspect | JVM Tests (`/verify`) | Native Tests (`/native-test`) |
|--------|----------------------|-------------------------------|
| Duration | 2-10 minutes | 5-30 minutes |
| Requirements | Java JDK | Docker/Podman + Java |
| Memory | 2GB+ recommended | 8GB+ recommended |
| Disk Space | ~500MB | ~2GB |
| Use Case | Development, CI/CD | Production validation |

## Troubleshooting

If the build fails:

1. **Check the error analysis output** for specific recommendations
2. **Review the full log**: `less /tmp/jvm-test-output.log`
3. **Verify Java installation**: `java -version` (should be JDK 21+)
4. **Check Maven repository**: `ls -la ~/.m2/repository` (ensure dependencies can be downloaded)
5. **Increase Maven memory** if needed: `export MAVEN_OPTS='-Xmx2g'`
6. **Run with debug output**: `./mvnw -X clean verify` (verbose logging)

## Common Issues

### Test Failures
- Review failed test output in the error analysis section
- Run individual tests: `./mvnw test -Dtest=YourTestClass`
- Check test logs in `target/surefire-reports/`

### Compilation Errors
- Verify Java version: `java -version`
- Check for syntax errors in recent code changes
- Ensure all dependencies are available

### Dependency Issues
- Check internet connectivity
- Verify Maven Central is accessible
- Clear local repository: `./mvnw dependency:purge-local-repository`
- Check for proxy settings in `~/.m2/settings.xml`

### Performance Issues
- Close unnecessary applications
- Increase Maven heap: `export MAVEN_OPTS='-Xmx2g'`
- Run with fewer parallel threads: `./mvnw -T 1 clean verify`

## Environment Variables

The command respects standard Maven environment variables:
- `MAVEN_OPTS`: JVM options for Maven (e.g., `-Xmx2g`)
- `JAVA_HOME`: Java installation directory
- `M2_HOME`: Maven installation directory (if not using wrapper)

## Integration with Development Workflow

This command is ideal for:
- **Pre-commit validation**: Run before committing code changes
- **Local development**: Quick feedback during feature development
- **CI/CD pipelines**: Standard test execution in continuous integration
- **Code review**: Verify pull requests before merging
- **Regression testing**: Ensure no existing functionality is broken

## Related Commands

- `/native-test` - Run native Quarkus tests (slower, production-like)
- `./mvnw test` - Run tests only (skip clean and packaging)
- `./mvnw clean package` - Build without running tests (faster)
- `./mvnw clean install` - Install to local Maven repository