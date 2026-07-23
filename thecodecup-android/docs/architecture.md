# Clean Architecture Guidelines

## Architectural Rules & Boundaries

### 1. Data Layer (`data/`)
- Do **NOT** modify files in `app/src/main/java/com/example/thecodecup/data/local` or `app/src/main/java/com/example/thecodecup/data/remote` (DTOs, DAOs, Retrofit interfaces are complete). If there are any errors or bugs in these files, ask me to fix.
- You are ONLY allowed to touch `app/src/main/java/com/example/thecodecup/data/repositories/` to implement repository contracts.

### 2. Domain Layer (`domain/`)
- Pure Kotlin only (No Android SDK, Retrofit, Room, or Compose imports).
- Create Domain Models in `app/src/main/java/com/example/thecodecup/domain/models`.
- Create Use Cases in `app/src/main/java/com/example/thecodecup/domain/usecases/[feature]/`. Every Use Case must have a single responsibility.

### 3. UI Layer (`ui/`)
- **ZERO DTO PERMISSION:** Never import or reference classes ending in `Dto` or inside `app/src/main/java/com/example/thecodecup/data/remote/`. Use Domain Models only.
- You can use or edit any components in `app/src/main/java/com/example/thecodecup/ui/components/`
- ViewModels must call **Domain Use Cases**, never Repositories directly, and only ViewModels can call **Domain Use Cases**.
- No network/API error parsing logic in the UI layer. Expose state via `StateFlow`.
- Add navigational logic in the UI layer via `app/src/main/java/com/example/thecodecup/ui/navigation/`
---

## Example Reference Files

When generating code, read and follow the structure, naming conventions, and coding style used in these existing project files:

- **Repository Implementation Pattern:**
    - `app/src/main/java/com/example/thecodecup/data/repositories/UserRepositoryImpl.kt`

- **Domain Use Case Pattern:**
    - `app/src/main/java/com/example/thecodecup/domain/usecases/auth/`

- **UI & ViewModel Pattern:**
    - `app/src/main/java/com/example/thecodecup/ui/core/profile/`
    - `app/src/main/java/com/example/thecodecup/ui/auth/register/`