# Weather forecast

<p align="center">
  <img src="recording.gif" height="800" width="400" />
</p>

This project demonstrates a modern Android application built with a focus on modularity, and clean architecture.
Before building the project please make sure to set *OPEN_WEATHER_API_KEY* and *GOOGLE_MAPS_API_KEY* in gradle.properties

## Table of Contents

1.  [Modularization Strategy](#modularization-strategy)
2.  [Feature Architecture](#feature-architecture)
    *   [Domain Layer Responsibility](#domain-layer-responsibility)
    *   [ViewModel Responsibility](#viewmodel-responsibility)
3.  [Testing Strategy](#testing-strategy)
    *   [Screenshot Tests](#screenshot-tests)


## Modularization Strategy

This project is built on a multi-module architecture to promote separation of concerns, improve build times, and enhance code maintainability and scalability. The key principles of our modularization are:

*   **Feature Modules (`:feature:*`)**: Each feature of the project (e.g., `:feature:location`) resides in its own module.
    *   **Interaction via Contracts**: Feature modules **do not interact directly with each other. Instead, they depend on **contract modules**.
*   **Contract Modules (`:contract:*`)**: These modules define the public interfaces and data models that are required from features and are being implemented in repositories or services.
    *   For example, if `:feature:weather` needs user authentication status, it would depend on an `AuthService` interface defined in a `:contract:auth` module. The actual implementation of this `AuthService` would live in a separate feature or service module.

*   **Core Module (`:core`)**:
    *   The `:core` module contains shared code, utilities, base classes, UI components, and extensions that are used across multiple modules in the application.
    *   Feature modules and service modules can depend on `:core`.

## Feature Architecture

*   **Data**: Contains class that interact with various data sources such as network and local storage classes.
*   **Domain**: Contains the core business logic of each feature.
*   **Presentation (UI)**: Contains Jetpack Compose UI elements (`@Composable` functions) and ViewModels.

### Domain Layer Responsibility

The domain classes are responsible for:

*   **Data Fetching**: Requests and updates data.
*   **Applying Business Logic**: Implementing any rules or transformations related to the request data (e.g., filtering, zipping, sorting based on complex criteria).
*   **Managing Data State**: Holding and exposing the needed state of all data required by the feature (e.g., weather response) typically as a `Flow<State>`.

Essentially, the domain class acts as the single source of truth for the feature's data state and business operations.

### ViewModel Responsibility

The ViewModel within the UI layer of a feature has a very specific and limited role:

*   **Observing Domain State**: It collects the state exposed by the domain class.
*   **Mapping to UI State**: It maps the domain state objects into UI-specific state classes.
*   **Delegating User Actions**: It receives user actions from the UI and delegates them to the appropriate methods in the domain class.
*   **Exposing UI State and Effects**: It exposes the UiState (typically as a `StateFlow`) for the Composables to observe and can also expose one-time UI effects (e.g., show Snackbar, navigate) via a `Channel`.

**The ViewModel does NOT contain business logic or directly fetch data from repositories.** Its primary job is to act as a bridge between the domain and the UI, preparing data for display and user interactions.

## Testing Strategy

### Screenshot Tests

*   Located in dedicated `screenshotTest` source sets.
*   Screenshot tests are a powerful way to **verify the visual appearance of Jetpack Compose UI components and entire screens without the need for full UI (Espresso) tests.**
*   They work by rendering a Composable into an image and comparing it against a previously approved "golden" image. If there are visual differences, the test fails.
*   **Benefits**:
    *   **Fast Execution**: Much faster than Espresso tests as they don't require an emulator or device to run through UI interactions.
    *   **Reliability**: Less flaky than Espresso tests because they don't depend on complex UI synchronization or timing issues.
    *   **Early Detection of UI Regressions**: Catch unintended visual changes (styling, layout, etc.) early in the development cycle.
    *   **Comprehensive Visual Coverage**: Ensure all UI states (loading, error, success with different data, dark/light mode) look as intended.
* To record run `./gradlew :module:updateDebugScreenshotTest` and to verify  run `./gradlew :module:validateDebugScreenshotTest`


