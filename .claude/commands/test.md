# Quarkus JVM Test Execution (Alias)

This is an alias for the `/verify` command.

Execute standard Quarkus JVM tests with comprehensive monitoring and validation.

## Usage

Simply run:
```
/test
```

This executes the same functionality as `/verify`.

## Full Documentation

For complete documentation, see: `/verify`

---

**Note**: This is a convenience alias. Both `/test` and `/verify` execute the same JVM test workflow: `./mvnw -V -B -am clean verify`

For the full command implementation and all features, please refer to `.claude/commands/verify.md` or run `/verify`.