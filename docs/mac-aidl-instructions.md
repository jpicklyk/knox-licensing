# IExecReceiverInterface AIDL Usage Guide

## Overview

This document explains how to integrate and use the `IExecReceiverInterface.aidl` interface for communicating with the SAM Services component.

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

// Bind to the service
val intent = Intent().apply {
    component = ComponentName(
        "com.partech.samservices",
        "com.partech.samservices.ExecReceiverService" // Adjust service name as needed
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

### 3. Error Handling

AIDL calls can throw `RemoteException`. Always wrap calls in try-catch:

```kotlin
try {
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

### 4. Unbinding the Service

Clean up when done:

```kotlin
override fun onDestroy() {
    super.onDestroy()
    if (execService != null) {
        unbindService(serviceConnection)
        execService = null
    }
}
```

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

- The service must be running and accessible (same app or exported service)
- Some methods require elevated permissions or device owner privileges
- All AIDL calls are synchronous and may block - consider calling from background thread
- Check return values: -1 typically indicates an error
