# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

Knox Licensing is a reusable Android library for Samsung Knox Enterprise License Management. It provides a clean, coroutine-based API for license activation, deactivation, and monitoring with Flow-based state observation.

## Build Commands

- **Build the library**: `./gradlew :knox-licensing:build`
- **Run unit tests**: `./gradlew :knox-licensing:test`
- **Run specific test class**: `./gradlew :knox-licensing:test --tests "com.github.jpicklyk.knox.licensing.domain.KnoxLicenseInitializerTest"`
- **Run instrumentation tests**: `./gradlew :knox-licensing:connectedAndroidTest`

## Architecture

### Layer Structure

```
domain/                         # Public interfaces and models
├── KnoxLicenseHandler.kt      # Main interface for license operations
├── LicenseSelectionStrategy.kt # Strategy interface for custom license selection
├── KnoxLicenseInitializer.kt  # DI-injectable initialization class
├── KnoxLicensingStartupManager.kt # Static facade (KnoxStartupManager object)
└── Models: LicenseConfiguration, LicenseInfo, LicenseResult, LicenseState

data/                          # Internal implementations
├── KnoxLicenseHandlerImpl.kt  # KnoxLicenseHandler implementation
├── KnoxLicenseRepository.kt   # Knox SDK interaction layer
├── KnoxErrorMapper.kt         # Knox error code mapping
└── LicenseKeyProvider.kt      # BuildConfig key parsing

KnoxLicenseFactory.kt          # Entry point factory for creating handlers
```

### Key Patterns

- **Clean Architecture**: Domain layer exposes public interfaces; data layer contains implementations marked `internal`
- **Strategy Pattern**: `LicenseSelectionStrategy` allows custom device-based license selection without SDK dependencies
- **Factory Pattern**: `KnoxLicenseFactory` creates configured `KnoxLicenseHandler` instances
- **Coroutines + Flow**: All license operations are `suspend` functions; state changes observed via `StateFlow`

### Two Initialization Approaches

1. **KnoxLicenseInitializer** (Recommended for DI): Injectable class with reactive status via `StateFlow<LicenseStartupResult>`
2. **KnoxStartupManager** (Static facade): Object with static methods for non-DI usage, delegates to `KnoxLicenseInitializer`

### License Configuration

License keys are expected from the consuming app's BuildConfig:
- `KNOX_LICENSE_KEY`: Default/primary license key
- `KNOX_LICENSE_KEYS`: Array of `"name:key"` pairs for named licenses

Factory methods accept these values explicitly to avoid BuildConfig coupling between modules.

## Testing Notes

- Uses MockK for mocking (not Mockito)
- Unit tests don't require Knox SDK or device
- Full license operations require Samsung Knox-enabled device with Device Administrator privileges
- Error code 301 (ERROR_INTERNAL) typically indicates missing Device Administrator privileges

## Knox SDK Dependency

The module uses `compileOnly` for Knox SDK JAR (`libs/knoxsdk_ver38.jar`) to avoid conflicts when consumers provide their own SDK variant. Consuming applications must provide the Knox SDK at runtime.
