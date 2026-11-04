# Kafa

An app that helps you keep track of activities and hobbies you once enjoyed.
Add activities, log when you do them, and the app reminds you when it's been
too long since you last did something.

## Tech Stack

- Kotlin 1.9.24, AGP 8.5.2
- Jetpack Compose (BOM 2024.06.00), Material3
- Room 2.6.1 — local database
- Dagger 2.51.1 — dependency injection
- Navigation Compose 2.7.7 — navigation
- Coroutines + Flow — async and reactivity
- Version Catalog (`gradle/libs.versions.toml`)

## Architecture

Clean Architecture + MVVM. Single-module with the following package structure:

```
presentation → domain → data:api ← data:impl
```

- **data/api** — repository interfaces (pure Kotlin)
- **data/impl** — repository implementations (Room)
- **data/local** — Room DAO, entities, mappers
- **domain/model** — domain models
- **presentation/feature** — screens (Screen + ViewModel)
- **presentation/navigation** — NavHost, routes
- **di** — Dagger modules (App, Data, ViewModel)

## Build & Run

```bash
./gradlew assembleDebug
./gradlew installDebug
```

Requirements: Android Studio Hedgehog+, JDK 17, Android SDK 34.

## Testing

```bash
./gradlew test
```

Stack: JUnit4, MockK, Turbine, kotlinx-coroutines-test, Room Testing.
