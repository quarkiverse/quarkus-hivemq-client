---
description: Java best practices and advanced patterns for Quarkus
alwaysApply: true
---

# Java Best Practices for Quarkus

Consolidated from: java-best-practices.mdc

## Type Definitions

### Basic Types and Interfaces
```java
// Prefer interfaces for contracts
public interface User {
    String getId();
    String getName();
    String getEmail();
    Role getRole(); // Enum
    LocalDateTime getCreatedAt();
    Optional<Map<String, Object>> getMetadata();
}

// Use records for immutable data
public record UserImpl(String id, String name, String email, Role role, LocalDateTime createdAt, Map<String, Object> metadata) implements User {}

// Enums for constants
public enum Role {
    ADMIN,
    USER,
    GUEST
}
```

### Generic Types
```java
// Generic interfaces
public interface ApiResponse<T> {
    T getData();
    Optional<String> getError();
    int getStatus();
    LocalDateTime getTimestamp();
}

// Generic methods
public <T, K> K getProperty(T obj, Function<T, K> getter) {
    return getter.apply(obj);
}

// Generic constraints
public interface Identifiable {
    String getId();
}

public <T extends Identifiable> Optional<T> findById(List<T> items, String id) {
    return items.stream().filter(item -> item.getId().equals(id)).findFirst();
}
```

### Utility Types
```java
// Use Optional for nullable
Optional<User> optionalUser = Optional.empty();

// Use records for partial
public record PartialUser(Optional<String> id, Optional<String> name) {}

// Pick/Omit via interfaces
public interface UserCredentials {
    String getEmail();
    String getPassword();
}

// Immutable collections
List<User> readonlyUsers = Collections.unmodifiableList(users);

// Custom utilities
public class Nullable<T> { private T value; /* ... */ }
public class AsyncReturnType { /* Use CompletableFuture */ }
```

## Component Typing
### CDI Beans

```java
@ApplicationScoped
public class ButtonService {
    private final String variant;
    private final String size;
    private final boolean disabled;

    @Inject
    public ButtonService(/* params */) { /* ... */ }

    public void onClick() { /* event handling */ }
}
```

### Generic Components
```java
public class SelectOption<T> {
    private final T value;
    private final String label;
    private final boolean disabled;
    // Constructor
}

@ApplicationScoped
public class SelectService<T> {
    public void select(List<SelectOption<T>> options, T value, Consumer<T> onChange) { /* ... */ }
}
```

### Components with Injection
```java
@ApplicationScoped
public class InputService {
    private final String label;
    private final Optional<String> error;

    @Inject
    public InputService(/* ... */) { /* ... */ }
}
```

### Polymorphic Components
```java
public abstract class Box<E> {
    protected final E element;
    // ...
}

public class DivBox extends Box<String> { /* ... */ }
```

## Hooks Typing

### API and Async Typing
#### API Response Types
```java
public sealed interface ApiResult<T> permits Success, Failure {}
public record Success<T>(T data) implements ApiResult<T> {}
public record Failure<T>(String error) implements ApiResult<T> {}

public CompletionStage<ApiResult<User>> fetchUser(String id) { /* ... */ }
```

#### Promise Types

```java
public <T> CompletionStage<T> apiCall(Supplier<CompletionStage<T>> fn) { /* ... */ }
```

### Database and Model Types
#### Database Schema Types

```java
// Use Panache entities
@Entity
public class UserEntity extends PanacheEntity {
    public String email;
    public String name;
    // ...
}

public class UserRepository extends PanacheRepository<UserEntity> { /* queries */ }
```

#### Query Builder Types

```java
// Use Panache query
List<UserEntity> users = UserEntity.find("email", "user@example.com").list();
```

### Advanced Patterns
#### Sealed Classes

```java
public sealed interface Action permits SetUser, Logout {}
public record SetUser(User user) implements Action {}
public record Logout() implements Action {}
```

#### String Templates (Java 21+)

```java
String cssValue = STR."\{number}\{unit}";
```

#### Type Guards

```java
public boolean isUser(Object value) { /* instanceof patterns */ }
```

#### Mapped Types

```java
// Use records or builders
```

### Environment and Config Types

#### Environment Variables

```java
// application.properties in Quarkus
quarkus.api.url=${API_URL}
```

#### Configuration Types

```java
@ConfigMapping(prefix = "app")
public interface AppConfig {
    String apiBaseUrl();
    int apiTimeout();
}
```

## Best Practices

### Do's
- Use strict null checks
- Prefer interfaces for contracts
- Leverage generics
- Use records for data
- Implement CDI
- Handle errors with sealed types
- Type configs

### Don'ts
- Avoid null without Optional
- Don't ignore unchecked casts
- Don't duplicate definitions
- Don't use raw types
- Handle CompletionStage properly

### Type Safety Checklist
- [ ] Use nullaway
- [ ] All methods typed
- [ ] Async handled
- [ ] Config injected
- [ ] Entities validated