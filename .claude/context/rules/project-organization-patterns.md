---
description: Project organization and structure guidelines for Quarkus extensions
globs: **/*.{java}
alwaysApply: false
---

You are an expert Java Quarkus architect with deep knowledge of project organization, file structure, and code organization in modern Maven-based Quarkus extensions.

# Project Organization Guidelines

## Overview
This document outlines best practices for organizing a Quarkus extension project using Maven and Java 17+. It covers directory structure, file naming, code organization, and architecture for consistency and maintainability.

## 1. Directory Structure

### Root-Level Organization
```
/
├── deployment/           # Build-time processors and deployment logic
├── docs/                 # Documentation and Antora files
├── integration-tests/    # Integration and end-to-end tests
├── runtime/              # Runtime classes and configurations
├── pom.xml               # Root Maven POM
├── README.md             # Project overview
└── LICENSE               # License file
```
### Deployment Directory

```
deployment/
├── src/
│   ├── main/
│   │   └── java/
│   │       └── io/quarkiverse/hivemqclient/deployment/
│   │           ├── HiveMQClientProcessor.java
│   │           ├── MqttBuildTimeConfig.java
│   │           └── MqttDevServicesProcessor.java
│   └── test/
│       └── java/
│           └── io/quarkiverse/hivemqclient/
│               ├── HiveMQClientDevModeTest.java
│               └── HiveMQClientTest.java
└── pom.xml               # Module POM
```

### Docs Directory

```
docs/
├── antora.yml            # Antora configuration
├── img/                  # Images
├── modules/
│   └── ROOT/
│       └── pages/
│           ├── config-reference.adoc
│           ├── includes/
│           │   ├── attributes.adoc
│           │   └── config.adoc
│           └── index.adoc
└── pom.xml               # Module POM
```

### Integration-tests Directory

```
integration-tests/
├── hivemq-client-smallrye/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/
│   │   │   │   └── io/quarkiverse/hivemqclient/test/smallrye/
│   │   │   │       ├── PriceConverter.java
│   │   │   │       ├── PriceGenerator.java
│   │   │   │       └── PriceResource.java
│   │   │   └── resources/
│   │   │       ├── application.properties
│   │   │       └── META-INF/
│   │   └── test/
│   │       ├── java/
│   │       │   └── io/quarkiverse/hivemqclient/test/smallrye/
│   │       │       ├── CommonScenarios.java
│   │       │       └── resources/
│   │       │           └── CommonResources.java
│   │       └── resources/
│   │           ├── certs/
│   │           └── hivemq-enterprise-security-extension/
│   └── pom.xml
├── kitchensink/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/
│   │   │   │   └── io/quarkiverse/hivemqclient/
│   │   │   │       └── GreetingResource.java
│   │   │   └── resources/
│   │   │       ├── application.properties
│   │   │       └── META-INF/resources/
│   │   │           └── index.html
│   │   └── test/
│   │       └── java/
│   │           └── io/quarkiverse/hivemqclient/
│   │               ├── GreetingResourceTest.java
│   │               └── NativeGreetingResourceIT.java
│   └── pom.xml
└── pom.xml               
```

### Runtime Directory

```
runtime/
├── src/
│   ├── main/
│   │   ├── codestarts/
│   │   │   └── quarkus/prices-codestart/
│   │   │       ├── base/src/main/
│   │   │       │   ├── java/io/codestart/hivemq/
│   │   │       │   │   ├── PriceConverter.java
│   │   │       │   │   ├── PriceGenerator.java
│   │   │       │   │   └── PriceResource.java
│   │   │       │   └── resources/
│   │   │       │       ├── application.yaml
│   │   │       │       └── META-INF/resources/
│   │   │       │           ├── index.html
│   │   │       │           └── prices.html
│   │   │       └── codestart.yml
│   │   ├── java/
│   │   │   └── io/quarkiverse/hivemqclient/
│   │   │       ├── exceptions/
│   │   │       │   └── SSLConfigException.java
│   │   │       ├── smallrye/reactive/
│   │   │       │   ├── HiveMQClients.java
│   │   │       │   ├── HiveMQMqttConnector.java
│   │   │       │   └── HiveMQMqttSink.java
│   │   │       └── ssl/
│   │   │           ├── IgnoreHostnameVerifier.java
│   │   │           └── KeyStoreUtil.java
│   │   └── resources/
│   │       └── META-INF/
│   │           └── quarkus-extension.yaml
│   └── test/
│       └── java/
│           └── io/quarkiverse/hivemqclient/
│               ├── smallrye/reactive/
│               │   ├── HiveMQMqttConnectorTest.java
│               │   └── HiveMQPingTest.java
│               ├── ssl/
│               │   └── IgnoreHostnameVerifierTest.java
│               └── test/
│                   ├── MqttTestBase.java
│                   └── MqttTestFixtures.java
└── pom.xml               
```

## 2. File Naming Conventions

### General Rules
- Use lowercase with hyphens for directory names: smallrye-reactive
- Use PascalCase for class files: HiveMQClientProcessor.java
- Use camelCase for method names: getDashboardData()
- Append Test or IT for test classes: GreetingResourceTest.java
- Use .properties or .yaml for config files

### Class Files

- Use PascalCase: PriceConverter.java, HiveMQClients.java
- Group related classes in packages: io.quarkiverse.hivemqclient.smallrye.reactive
- Use subpackages for organization: deployment, runtime, test

```java
// Example class structure
// runtime/src/main/java/io/quarkiverse/hivemqclient/smallrye/reactive/HiveMQClients.java
package io.quarkiverse.hivemqclient.smallrye.reactive;

public class HiveMQClients {
    // Implementation
}
```

### Utility Files
- Use descriptive names: KeyStoreUtil.java, JwtUtils.java
- Group in packages with exports via imports

```java
// Example utility
// runtime/src/main/java/io/quarkiverse/hivemqclient/ssl/KeyStoreUtil.java
package io.quarkiverse.hivemqclient.ssl;

public class KeyStoreUtil {
    public static void loadKeyStore() {
        // Implementation
    }
}
```

## 3. Code Organization

### Class Organization
- Split large classes into smaller ones
- Keep classes under 300 lines
- Co-locate related classes in packages
- Use dependency injection

```java
// Example
// integration-tests/hivemq-client-smallrye/src/main/java/io/quarkiverse/hivemqclient/test/smallrye/PriceResource.java
package io.quarkiverse.hivemqclient.test.smallrye;

import jakarta.inject.Inject;

public class PriceResource {
    @Inject
    PriceConverter converter;

    // Methods
}
```

### Resource Organization
- Keep REST resources simple
- Handle data in services
- Use annotations for injection

```java
// Example
// kitchensink/src/main/java/io/quarkiverse/hivemqclient/GreetingResource.java
package io.quarkiverse.hivemqclient;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;

@Path("/greeting")
public class GreetingResource {
    @GET
    public String hello() {
        return "Hello";
    }
}
```

## 4. Package Imports

### Import Order
- Java standard imports first
- Third-party imports next
- Internal imports last
- Separate groups with blank lines

```java
// Example
import java.util.List;

import io.smallrye.mutiny.Uni;

import io.quarkiverse.hivemqclient.smallrye.reactive.HiveMQClients;
```

## 5. Data Handling Organization

### Build-Time Processors
- Keep in `deployment` module
- Organize by feature
- Use BuildItems

```java
// Example
// deployment/src/main/java/io/quarkiverse/hivemqclient/deployment/HiveMQClientProcessor.java
package io.quarkiverse.hivemqclient.deployment;

import io.quarkus.arc.deployment.AdditionalBeanBuildItem;

public class HiveMQClientProcessor {
    // Produce build items
}
```

### Runtime Connectors
- Keep in `runtime` module
- Use Quarkus configurations

```java
// Example
// runtime/src/main/java/io/quarkiverse/hivemqclient/smallrye/reactive/HiveMQMqttConnector.java
package io.quarkiverse.hivemqclient.smallrye.reactive;

import io.smallrye.reactive.messaging.annotations.Connector;

@Connector
public class HiveMQMqttConnector {
    // Implementation
}
```

## 6. Configuration Management

### Application Config
- Use `application.properties` or `application.yaml`
- Document in docs
- Use Quarkus config annotations

```properties
# Example application.properties
quarkus.hivemq-client.host=localhost
quarkus.hivemq-client.port=1883
```

### Extension Config
- Define in runtime/src/main/resources/META-INF/quarkus-extension.yaml

## 7. Testing Organization

### Unit Tests
- Co-locate with code
- Use Test suffix
- Use JUnit5

```java
// Example
// runtime/src/test/java/io/quarkiverse/hivemqclient/smallrye/reactive/HiveMQMqttConnectorTest.java
package io.quarkiverse.hivemqclient.smallrye.reactive;

import org.junit.jupiter.api.Test;

class HiveMQMqttConnectorTest {
    @Test
    void testConnection() {
        // Assertions
    }
}
```

### Integration Tests
- In `integration-tests` module
- Use `@QuarkusTest`

```java
// Example
// integration-tests/hivemq-client-smallrye/src/test/java/io/quarkiverse/hivemqclient/test/smallrye/NoAuthPriceTest.java
package io.quarkiverse.hivemqclient.test.smallrye;

import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
class NoAuthPriceTest {
    // Tests
}
```

## 8. Documentation

### Code Comments
- Use Javadoc for classes, methods
- Comment complex logic

```java
/**
 * Utility for key stores.
 */
public class KeyStoreUtil {
    /**
     * Loads a key store.
     */
    public static void loadKeyStore() {
        // Implementation
    }
}
```

### README Files
- Root README.md with setup
- Module READMEs if needed

## 9. Do's and Don'ts

### Do ✅
- Follow Maven module structure
- Use consistent package naming
- Co-locate tests
- Document configs
- Split large classes

### Don't ❌
- Nest packages deeply
- Mix concerns in classes
- Duplicate code
- Use ambiguous names
- Break Quarkus patterns

---

Follow these guidelines for a maintainable Quarkus extension.