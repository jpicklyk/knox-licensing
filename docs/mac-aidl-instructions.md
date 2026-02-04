# IExecReceiverInterface AIDL Usage Guide

## Overview

This document explains how to integrate and use the `IExecReceiverInterface.aidl` interface for communicating with the SAM Services component.

## Service Details

| Property | Value |
|----------|-------|
| Package | `com.partech.samservices` |
| Service Class | `com.partech.samservices.SamService` |

## Manifest requirement:
```
<queries>
    <package android:name="com.partech.samservices" />
</queries>
```
## AIDL Interface Definition

The complete AIDL interface:

```java
package com.partech.samservices;

interface IExecReceiverInterface {

    /**
     * Returns a version code that describes this interface.
     * @return the version code
     */
    int getVersionCode();

    /**
     * Set the time with the time from millisec_epoch
     * @param millis the time in millis from epoch
     * @param the return value based on the command or -1 if there is an error
     */
    int setTime(long millis);

    /**
     * Execute the command with the provided arguments.
     * @param cmd the command to execute
     * @param args the arguments to the command
     * @param the return value based on the command or -1 if there is an error
     */
    int execute(String cmd, String args);

    /**
     * Get the interface mac address for the provided interface name.
     * @param ifacename the interface name
     * @return the mac address as a byte[] or an empty array if no
     * mac address is located.
     **/
    String getMacAddress(String ifacename);
}
```

## Adding AIDL to Your Project

### 1. Directory Structure

Place the AIDL file in your module's `src/main/aidl` directory matching the package structure:

```
app/src/main/aidl/com/partech/samservices/IExecReceiverInterface.aidl
```

### 2. Build Configuration

The Android build system automatically generates Java/Kotlin interface code from AIDL files during compilation. No additional Gradle configuration is needed.

After building the project, the generated interface will be available at:
```kotlin
import com.partech.samservices.IExecReceiverInterface
```

## Using the Interface

### 1. Binding to the Service

Create a ServiceConnection to bind to the remote service:

```kotlin
private var execService: IExecReceiverInterface? = null

private val serviceConnection = object : ServiceConnection {
    override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
        execService = IExecReceiverInterface.Stub.asInterface(service)
    }

    override fun onServiceDisconnected(name: ComponentName?) {
        execService = null
    }
}

// Bind to the service using explicit ComponentName (required for Android 5.0+)
val intent = Intent().apply {
    component = ComponentName(
        "com.partech.samservices",
        "com.partech.samservices.SamService"
    )
}
bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
```

### 2. Calling Interface Methods

Once bound, call the interface methods:

```kotlin
// Check interface version
val version = execService?.getVersionCode()

// Set system time (requires appropriate permissions)
val currentTimeMillis = System.currentTimeMillis()
val result = execService?.setTime(currentTimeMillis)

// Execute a command
val commandResult = execService?.execute("command", "arguments")

// Get MAC address for network interface
val macAddress = execService?.getMacAddress("wlan0")
```

### 3. Checking Service Availability

Before making AIDL calls, verify the service connection is alive:

```kotlin
fun isServiceAvailable(): Boolean {
    return execService?.asBinder()?.isBinderAlive == true
}
```

### 4. Error Handling

AIDL calls can throw `RemoteException`. Always wrap calls in try-catch:

```kotlin
try {
    if (!isServiceAvailable()) {
        Log.e(TAG, "Service not available")
        return
    }

    val macAddress = execService?.getMacAddress("eth0")
    if (macAddress.isNullOrEmpty()) {
        // No MAC address found
    } else {
        // Use MAC address
    }
} catch (e: RemoteException) {
    Log.e(TAG, "Failed to get MAC address", e)
}
```

### 5. Recommended Timeouts

When using coroutines, apply timeouts to avoid blocking indefinitely:

```kotlin
companion object {
    const val SERVICE_CONNECTION_TIMEOUT = 3_000L // 3 seconds
    const val AIDL_CALL_TIMEOUT = 2_000L          // 2 seconds
}

// Example with timeout
val macAddress = withTimeout(AIDL_CALL_TIMEOUT) {
    execService?.getMacAddress("eth0")
}
```

### 6. Unbinding the Service

Clean up when done. Handle the case where the service may already be unbound:

```kotlin
override fun onDestroy() {
    super.onDestroy()
    try {
        unbindService(serviceConnection)
    } catch (e: IllegalArgumentException) {
        // Service was already unbound
    }
    execService = null
}
```

## Recommended Architecture

For production use, consider wrapping the AIDL service with a **Repository** and **Use Case** pattern:

### Repository

Encapsulates connection lifecycle, thread safety, and timeouts:

```kotlin
interface EthernetAidlRepository {
    suspend fun getMacAddress(interfaceName: String): String?
    suspend fun isServiceAvailable(): Boolean
}

class EthernetAidlRepositoryImpl(
    private val context: Context,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : EthernetAidlRepository {

    private var serviceInterface: IExecReceiverInterface? = null
    private val connectionMutex = Mutex()

    override suspend fun getMacAddress(interfaceName: String): String? {
        return withContext(ioDispatcher) {
            try {
                ensureServiceConnection()
                ensureActive()
                withTimeout(AIDL_CALL_TIMEOUT) {
                    serviceInterface?.getMacAddress(interfaceName)
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                null
            }
        }
    }

    override suspend fun isServiceAvailable(): Boolean {
        return withContext(ioDispatcher) {
            try {
                ensureServiceConnection()
                serviceInterface?.asBinder()?.isBinderAlive == true
            } catch (e: Exception) {
                false
            }
        }
    }

    private suspend fun ensureServiceConnection() {
        connectionMutex.withLock {
            if (serviceInterface?.asBinder()?.isBinderAlive != true) {
                bindToService()
            }
        }
    }

    // ... binding logic from earlier sections
}
```

The `ioDispatcher` parameter allows injecting a test dispatcher for unit testing.

### Use Case

Provides clean error handling using Kotlin's `Result<T>`:

```kotlin
class GetMacAddressUseCase(
    private val repository: EthernetAidlRepository
) {
    suspend operator fun invoke(interfaceName: String): Result<String> {
        if (!repository.isServiceAvailable()) {
            return Result.failure(IllegalStateException("AIDL service not available"))
        }

        val macAddress = repository.getMacAddress(interfaceName)

        return if (macAddress.isNullOrBlank()) {
            Result.failure(IllegalArgumentException("Interface '$interfaceName' not found"))
        } else {
            Result.success(macAddress)
        }
    }
}
```

### Usage

```kotlin
val repository = EthernetAidlRepositoryImpl(context)
val getMacAddress = GetMacAddressUseCase(repository)

// Call the use case
getMacAddress("eth0")
    .onSuccess { mac -> Log.d(TAG, "MAC: $mac") }
    .onFailure { error -> Log.e(TAG, "Failed: ${error.message}") }
```

This pattern separates concerns: the repository handles low-level AIDL communication while the use case provides a clean API for consumers.

## Method Details

### `getVersionCode()`
- **Returns**: Interface version code
- **Use**: Verify compatibility between client and service

### `setTime(long millis)`
- **Parameters**: Time in milliseconds since epoch
- **Returns**: 0 on success, -1 on error
- **Note**: Requires system-level permissions

### `execute(String cmd, String args)`
- **Parameters**: Command and its arguments
- **Returns**: Command-specific return value, -1 on error
- **Note**: Command execution depends on service implementation and permissions

### `getMacAddress(String ifacename)`
- **Parameters**: Network interface name (e.g., "eth0", "wlan0")
- **Returns**: MAC address string or empty string if not found
- **Example interfaces**: "eth0", "wlan0", "rmnet0"

## Notes

- The service must be installed and accessible on the device
- Some methods require elevated permissions or device owner privileges
- All AIDL calls are synchronous and block the calling thread - use coroutines with timeouts
- Check return values: -1 typically indicates an error
- Use `isBinderAlive` to verify the connection before making calls
- Recommended timeouts: 3 seconds for connection, 2 seconds for individual calls
