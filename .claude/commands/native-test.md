# Quarkus Native Test Execution

Execute Quarkus native tests with comprehensive monitoring and validation.

## Pre-Execution Validation

First, verify the native build environment is ready:

```bash
# Check for Docker or Podman
if command -v docker &> /dev/null; then
    echo "✓ Docker detected"
    docker --version
    docker info | grep -i "Server Version" || echo "⚠ Docker daemon may not be running"
elif command -v podman &> /dev/null; then
    echo "✓ Podman detected"
    podman --version
    podman info | grep -i "version" || echo "⚠ Podman may not be properly configured"
else
    echo "✗ ERROR: Neither Docker nor Podman found. Native builds require a container runtime."
    echo "Please install Docker or Podman before running native tests."
    exit 1
fi

# Check available disk space (native builds need ~2GB)
available_space=$(df -BG /home/pagonzal/Documents/workspace/quarkus-hivemq-client | tail -1 | awk '{print $4}' | sed 's/G//')
if [ "$available_space" -lt 3 ]; then
    echo "⚠ WARNING: Low disk space (${available_space}GB available). Native builds require ~2GB."
    echo "Consider freeing up disk space before proceeding."
fi

# Check if mvnw is executable
if [ ! -x "/home/pagonzal/Documents/workspace/quarkus-hivemq-client/mvnw" ]; then
    echo "Making mvnw executable..."
    chmod +x /home/pagonzal/Documents/workspace/quarkus-hivemq-client/mvnw
fi

echo ""
echo "======================================"
echo "Starting Quarkus Native Test Build"
echo "======================================"
echo "⏱  Estimated time: 5-30 minutes"
echo "📦 Builder image: quay.io/quarkus/ubi9-quarkus-mandrel-builder-image:jdk-21"
echo "🎯 Profile: native"
echo "======================================"
echo ""
```

## Execute Native Build and Tests

Run the native verification with proper timeout and monitoring:

```bash
# Set working directory
cd /home/pagonzal/Documents/workspace/quarkus-hivemq-client

# Start timestamp
start_time=$(date +%s)
echo "⏰ Build started at: $(date '+%Y-%m-%d %H:%M:%S')"
echo ""

# Execute native build with verify goal
# Using timeout of 40 minutes (2400 seconds) to allow for slower systems
timeout 2400 ./mvnw -V -B -am clean verify \
    -Dnative \
    -Dquarkus.native.builder-image=quay.io/quarkus/ubi9-quarkus-mandrel-builder-image:jdk-21 \
    2>&1 | tee /tmp/native-test-output.log

# Capture exit code
exit_code=${PIPESTATUS[0]}

# End timestamp
end_time=$(date +%s)
duration=$((end_time - start_time))
minutes=$((duration / 60))
seconds=$((duration % 60))

echo ""
echo "======================================"
echo "Native Test Build Completed"
echo "======================================"
echo "⏱  Duration: ${minutes}m ${seconds}s"
echo "⏰ Completed at: $(date '+%Y-%m-%d %H:%M:%S')"

# Handle different exit codes
if [ $exit_code -eq 124 ]; then
    echo "❌ Build TIMEOUT after 40 minutes"
    echo ""
    echo "Possible causes:"
    echo "  - Insufficient system resources (CPU/RAM)"
    echo "  - Network issues downloading dependencies"
    echo "  - Container runtime performance issues"
    echo ""
    echo "Recommendations:"
    echo "  - Close other applications to free resources"
    echo "  - Check container runtime logs: docker logs or podman logs"
    echo "  - Verify network connectivity"
    exit 124
elif [ $exit_code -eq 0 ]; then
    echo "✅ Build SUCCESSFUL"
    echo ""
    echo "Native executable created successfully!"
    echo "All native tests passed."
    echo ""

    # Find and display native executable locations
    echo "Native Executables:"
    find /home/pagonzal/Documents/workspace/quarkus-hivemq-client -name "*-runner" -type f 2>/dev/null | while read -r runner; do
        size=$(du -h "$runner" | cut -f1)
        echo "  📦 $runner ($size)"
    done

    # Show test summary
    echo ""
    echo "Test Summary:"
    grep -E "Tests run:|BUILD SUCCESS" /tmp/native-test-output.log | tail -5

    exit 0
else
    echo "❌ Build FAILED (exit code: $exit_code)"
    echo ""
    echo "Error Analysis:"
    echo "======================================"

    # Extract and display relevant error information
    if grep -q "OutOfMemoryError" /tmp/native-test-output.log; then
        echo "⚠ Out of Memory Error detected"
        echo "  - Increase Docker/Podman memory limit"
        echo "  - Close other applications"
        echo "  - Try: export MAVEN_OPTS='-Xmx4g'"
    fi

    if grep -q "Cannot connect to the Docker daemon" /tmp/native-test-output.log; then
        echo "⚠ Docker daemon connection error"
        echo "  - Ensure Docker is running: systemctl start docker"
        echo "  - Check user permissions: groups | grep docker"
    fi

    if grep -q "Error pulling image" /tmp/native-test-output.log; then
        echo "⚠ Container image pull error"
        echo "  - Check network connectivity"
        echo "  - Verify proxy settings if behind firewall"
    fi

    # Show last 20 error lines
    echo ""
    echo "Last error messages:"
    echo "======================================"
    grep -E "ERROR|FAILURE|Failed" /tmp/native-test-output.log | tail -20

    echo ""
    echo "Full log available at: /tmp/native-test-output.log"

    exit $exit_code
fi
```

## Post-Execution Summary

After completion, the command provides:

- **Success**: Native executable locations, test summary, build duration
- **Failure**: Error analysis, troubleshooting recommendations, log file location
- **Timeout**: Clear timeout message with resource optimization suggestions

## Usage

Simply run:
```
/native-test
```

## Notes

- **Duration**: Native builds typically take 5-30 minutes depending on system resources
- **Requirements**: Docker or Podman must be installed and running
- **Disk Space**: Requires approximately 2GB free space
- **Memory**: Recommended 8GB+ RAM available
- **Timeout**: Set to 40 minutes (configurable in script)
- **Logs**: Full build output saved to `/tmp/native-test-output.log`

## Troubleshooting

If the build fails:

1. Check the error analysis output for specific recommendations
2. Review the full log: `less /tmp/native-test-output.log`
3. Verify Docker/Podman is running: `docker info` or `podman info`
4. Ensure sufficient resources: at least 8GB RAM and 3GB free disk space
5. Try increasing Maven memory: `export MAVEN_OPTS='-Xmx4g'`

## Environment Variables

The command respects standard Maven environment variables:
- `MAVEN_OPTS`: JVM options for Maven (e.g., `-Xmx4g`)
- `DOCKER_HOST`: Custom Docker daemon location
- `TESTCONTAINERS_*`: Testcontainers configuration if used