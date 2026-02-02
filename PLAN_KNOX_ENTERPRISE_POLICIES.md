# Knox Enterprise Policies Implementation Plan (Comprehensive)

## Overview

This plan outlines the addition of standard mobility restrictions, device controls, and policy use cases to the `knox-enterprise` module based on a comprehensive review of the Knox SDK documentation.

**Total Knox SDK APIs Reviewed:** ~600+ methods across 15+ policy classes

**Excluded Complex Features:** VPN configuration, DEX, KIOSK mode, UCM, seams, containerization, multi-user management, certificate provisioning (beyond basic)

---

## API Sources Summary

| Policy Class | Methods | Category |
|--------------|---------|----------|
| `RestrictionPolicy` | ~70 | Core device restrictions |
| `AdvancedRestrictionPolicy` | ~20 | Advanced restrictions |
| `PhoneRestrictionPolicy` | ~70 | Telephony controls |
| `RoamingPolicy` | 8 | Roaming settings |
| `ApplicationPolicy` | ~80 | App management |
| `PasswordPolicy` | ~30 | Security/auth |
| `WifiPolicy` | ~40 | WiFi management |
| `DateTimePolicy` | ~14 | Date/time controls |
| `LocationPolicy` | ~7 | Location services |
| `Firewall` | ~15 | Network firewall |
| `BrowserPolicy` | ~16 | Browser settings |
| `NfcPolicy` | 4 | NFC controls |
| `Font` | 6 | Display fonts |
| `DeviceInventory` | ~16 | Device info |
| `SystemManager` | ~150+ | System customization |
| `SettingsManager` | ~20 | Settings controls |

---

## Architecture Pattern

Each policy consists of:

1. **Use Cases** - Get/Set operations extending `SuspendingUseCase<P, R>`
2. **Policy Class** - Annotated with `@PolicyDefinition`, extending `BooleanStatePolicy` or `ConfigurableStatePolicy`
3. **Domain Models** - Type-safe enumerations and state objects (when needed)

### File Organization

```
knox-enterprise/src/main/java/net/sfelabs/knox_enterprise/
├── domain/
│   ├── model/
│   │   ├── device/          # Device state models
│   │   ├── connectivity/    # Network models
│   │   ├── telephony/       # Phone/SMS models
│   │   ├── security/        # Security models
│   │   └── application/     # App management models
│   ├── policy/
│   │   ├── device/          # Device-level restrictions
│   │   ├── connectivity/    # Network/connectivity controls
│   │   ├── telephony/       # Phone/SMS policies
│   │   ├── security/        # Security policies
│   │   ├── media/           # Camera, audio, video controls
│   │   ├── display/         # Screen, brightness, UI
│   │   ├── hardware/        # Hardware controls
│   │   ├── browser/         # Browser policies
│   │   └── application/     # App management policies
│   └── use_cases/
│       ├── device/
│       ├── connectivity/
│       ├── telephony/
│       ├── security/
│       ├── media/
│       ├── display/
│       ├── hardware/
│       ├── browser/
│       ├── datetime/
│       ├── system/
│       └── application/
```

---

## Phase 1: Core Device Restrictions (Priority: Critical)

### 1.1 RestrictionPolicy - Device Control

| # | Policy | Use Cases | Knox API | Capabilities |
|---|--------|-----------|----------|--------------|
| 1 | `AllowFactoryResetPolicy` | `AllowFactoryResetUseCase`, `IsFactoryResetAllowedUseCase` | `allowFactoryReset()`, `isFactoryResetAllowed()` | `MODIFIES_SECURITY`, `SECURITY_SENSITIVE`, `PERSISTENT_ACROSS_REBOOT` |
| 2 | `AllowSafeModePolicy` | `AllowSafeModeUseCase`, `IsSafeModeAllowedUseCase` | `allowSafeMode()`, `isSafeModeAllowed()` | `MODIFIES_SECURITY`, `SECURITY_SENSITIVE` |
| 3 | `AllowPowerOffPolicy` | `AllowPowerOffUseCase`, `IsPowerOffAllowedUseCase` | `allowPowerOff()`, `isPowerOffAllowed()` | `MODIFIES_HARDWARE`, `SECURITY_SENSITIVE` |
| 4 | `AllowDeveloperModePolicy` | `AllowDeveloperModeUseCase`, `IsDeveloperModeAllowedUseCase` | `allowDeveloperMode()`, `isDeveloperModeAllowed()` | `MODIFIES_SECURITY`, `SECURITY_SENSITIVE` |
| 5 | `AllowSettingsChangesPolicy` | `AllowSettingsChangesUseCase`, `IsSettingsChangesAllowedUseCase` | `allowSettingsChanges()`, `isSettingsChangesAllowed()` | `MODIFIES_SECURITY`, `EASILY_REVERSIBLE` |
| 6 | `AllowPowerSavingModePolicy` | `AllowPowerSavingModeUseCase`, `IsPowerSavingModeAllowedUseCase` | `allowPowerSavingMode()`, `isPowerSavingModeAllowed()` | `MODIFIES_HARDWARE`, `AFFECTS_BATTERY`, `EASILY_REVERSIBLE` |
| 7 | `AllowScreenPinningPolicy` | `AllowScreenPinningUseCase`, `IsScreenPinningAllowedUseCase` | `allowScreenPinning()`, `isScreenPinningAllowed()` | `MODIFIES_DISPLAY`, `EASILY_REVERSIBLE` |
| 8 | `AllowStatusBarExpansionPolicy` | `AllowStatusBarExpansionUseCase`, `IsStatusBarExpansionAllowedUseCase` | `allowStatusBarExpansion()`, `isStatusBarExpansionAllowed()` | `MODIFIES_DISPLAY`, `EASILY_REVERSIBLE` |
| 9 | `AllowWallpaperChangePolicy` | `AllowWallpaperChangeUseCase`, `IsWallpaperChangeAllowedUseCase` | `allowWallpaperChange()`, `isWallpaperChangeAllowed()` | `MODIFIES_DISPLAY`, `EASILY_REVERSIBLE` |
| 10 | `AllowBackgroundProcessLimitPolicy` | `AllowBackgroundProcessLimitUseCase`, `IsBackgroundProcessLimitAllowedUseCase` | `allowBackgroundProcessLimit()`, `isBackgroundProcessLimitAllowed()` | `MODIFIES_HARDWARE`, `AFFECTS_BATTERY` |
| 11 | `AllowKillingActivitiesOnLeavePolicy` | `AllowKillingActivitiesOnLeaveUseCase`, `IsKillingActivitiesOnLeaveAllowedUseCase` | `allowKillingActivitiesOnLeave()`, `isKillingActivitiesOnLeaveAllowed()` | `MODIFIES_HARDWARE` |
| 12 | `AllowShareListPolicy` | `AllowShareListUseCase`, `IsShareListAllowedUseCase` | `allowShareList()`, `isShareListAllowed()` | `MODIFIES_SECURITY`, `EASILY_REVERSIBLE` |
| 13 | `AllowSmartClipModePolicy` | `AllowSmartClipModeUseCase`, `IsSmartClipModeAllowedUseCase` | `allowSmartClipMode()`, `isSmartClipModeAllowed()` | `MODIFIES_HARDWARE`, `EASILY_REVERSIBLE` |
| 14 | `AllowStopSystemAppPolicy` | `AllowStopSystemAppUseCase`, `IsStopSystemAppAllowedUseCase` | `allowStopSystemApp()`, `isStopSystemAppAllowed()` | `MODIFIES_SECURITY` |
| 15 | `AllowDataSavingPolicy` | `AllowDataSavingUseCase`, `IsDataSavingAllowedUseCase` | `allowDataSaving()`, `isDataSavingAllowed()` | `MODIFIES_NETWORK`, `AFFECTS_BATTERY` |

### 1.2 RestrictionPolicy - Backup & Sync

| # | Policy | Use Cases | Knox API | Capabilities |
|---|--------|-----------|----------|--------------|
| 16 | `BackupEnabledPolicy` | `SetBackupEnabledUseCase`, `IsBackupAllowedUseCase` | `setBackup()`, `isBackupAllowed()` | `MODIFIES_SECURITY`, `SECURITY_SENSITIVE` |
| 17 | `AllowGoogleAccountsAutoSyncPolicy` | `AllowGoogleAccountsAutoSyncUseCase`, `IsGoogleAccountsAutoSyncAllowedUseCase` | `allowGoogleAccountsAutoSync()`, `isGoogleAccountsAutoSyncAllowed()` | `MODIFIES_NETWORK`, `AFFECTS_BATTERY` |
| 18 | `AllowGoogleCrashReportPolicy` | `AllowGoogleCrashReportUseCase`, `IsGoogleCrashReportAllowedUseCase` | `allowGoogleCrashReport()`, `isGoogleCrashReportAllowed()` | `MODIFIES_NETWORK`, `SECURITY_SENSITIVE` |

---

## Phase 2: Connectivity Controls (Priority: Critical)

### 2.1 RestrictionPolicy - Radio & Network

| # | Policy | Use Cases | Knox API | Capabilities |
|---|--------|-----------|----------|--------------|
| 19 | `AllowAirplaneModePolicy` | `AllowAirplaneModeUseCase`, `IsAirplaneModeAllowedUseCase` | `allowAirplaneMode()`, `isAirplaneModeAllowed()` | `MODIFIES_RADIO`, `AFFECTS_CONNECTIVITY`, `EASILY_REVERSIBLE` |
| 20 | `BluetoothEnabledPolicy` | `SetBluetoothEnabledUseCase`, `IsBluetoothEnabledUseCase` | `allowBluetooth()`, `isBluetoothEnabled()` | `MODIFIES_BLUETOOTH`, `AFFECTS_CONNECTIVITY`, `EASILY_REVERSIBLE` |
| 21 | `AllowWifiDirectPolicy` | `AllowWifiDirectUseCase`, `IsWifiDirectAllowedUseCase` | `allowWifiDirect()`, `isWifiDirectAllowed()` | `MODIFIES_WIFI`, `AFFECTS_CONNECTIVITY`, `EASILY_REVERSIBLE` |
| 22 | `AllowAndroidBeamPolicy` | `AllowAndroidBeamUseCase`, `IsAndroidBeamAllowedUseCase` | `allowAndroidBeam()`, `isAndroidBeamAllowed()` | `MODIFIES_NETWORK`, `AFFECTS_CONNECTIVITY`, `EASILY_REVERSIBLE` |
| 23 | `AllowSBeamPolicy` | `AllowSBeamUseCase`, `IsSBeamAllowedUseCase` | `allowSBeam()`, `isSBeamAllowed()` | `MODIFIES_NETWORK`, `AFFECTS_CONNECTIVITY`, `EASILY_REVERSIBLE` |
| 24 | `CellularDataEnabledPolicy` | `SetCellularDataEnabledUseCase`, `IsCellularDataAllowedUseCase` | `setCellularData()`, `isCellularDataAllowed()` | `MODIFIES_RADIO`, `AFFECTS_CONNECTIVITY`, `REQUIRES_SIM` |
| 25 | `BackgroundDataEnabledPolicy` | `SetBackgroundDataEnabledUseCase`, `IsBackgroundDataEnabledUseCase` | `setBackgroundData()`, `isBackgroundDataEnabled()` | `MODIFIES_NETWORK`, `AFFECTS_BATTERY`, `AFFECTS_CONNECTIVITY` |
| 26 | `AllowUserMobileDataLimitPolicy` | `AllowUserMobileDataLimitUseCase`, `IsUserMobileDataLimitAllowedUseCase` | `allowUserMobileDataLimit()`, `isUserMobileDataLimitAllowed()` | `MODIFIES_RADIO`, `REQUIRES_SIM` |

### 2.2 RestrictionPolicy - Tethering

| # | Policy | Use Cases | Knox API | Capabilities |
|---|--------|-----------|----------|--------------|
| 27 | `TetheringEnabledPolicy` | `SetTetheringEnabledUseCase`, `IsTetheringEnabledUseCase` | `setTethering()`, `isTetheringEnabled()` | `MODIFIES_NETWORK`, `AFFECTS_CONNECTIVITY`, `REQUIRES_SIM` |
| 28 | `UsbTetheringEnabledPolicy` | `SetUsbTetheringEnabledUseCase`, `IsUsbTetheringEnabledUseCase` | `setUsbTethering()`, `isUsbTetheringEnabled()` | `MODIFIES_NETWORK`, `AFFECTS_CONNECTIVITY` |
| 29 | `WifiTetheringEnabledPolicy` | `SetWifiTetheringEnabledUseCase`, `IsWifiTetheringEnabledUseCase` | `setWifiTethering()`, `isWifiTetheringEnabled()` | `MODIFIES_WIFI`, `AFFECTS_CONNECTIVITY` |
| 30 | `BluetoothTetheringEnabledPolicy` | `SetBluetoothTetheringEnabledUseCase`, `IsBluetoothTetheringEnabledUseCase` | `setBluetoothTethering()`, `isBluetoothTetheringEnabled()` | `MODIFIES_BLUETOOTH`, `AFFECTS_CONNECTIVITY` |

### 2.3 WifiPolicy Controls

| # | Policy | Use Cases | Knox API | Capabilities |
|---|--------|-----------|----------|--------------|
| 31 | `WifiStateChangeAllowedPolicy` | `SetWifiStateChangeAllowedUseCase`, `IsWifiStateChangeAllowedUseCase` | `setWifiStateChangeAllowed()`, `isWifiStateChangeAllowed()` | `MODIFIES_WIFI`, `AFFECTS_CONNECTIVITY` |
| 32 | `AllowUserWifiProfilesPolicy` | `SetAllowUserWifiProfilesUseCase`, `GetAllowUserWifiProfilesUseCase` | `setAllowUserProfiles()`, `getAllowUserProfiles()` | `MODIFIES_WIFI`, `EASILY_REVERSIBLE` |
| 33 | `AllowUserWifiPolicyChangesPolicy` | `SetAllowUserWifiPolicyChangesUseCase`, `GetAllowUserWifiPolicyChangesUseCase` | `setAllowUserPolicyChanges()`, `getAllowUserPolicyChanges()` | `MODIFIES_WIFI`, `SECURITY_SENSITIVE` |
| 34 | `AllowOpenWifiApPolicy` | `AllowOpenWifiApUseCase`, `IsOpenWifiApAllowedUseCase` | `allowOpenWifiAp()`, `isOpenWifiApAllowed()` | `MODIFIES_WIFI`, `SECURITY_SENSITIVE` |
| 35 | `AllowWifiApSettingModificationPolicy` | `AllowWifiApSettingModificationUseCase`, `IsWifiApSettingModificationAllowedUseCase` | `allowWifiApSettingUserModification()`, `isWifiApSettingUserModificationAllowed()` | `MODIFIES_WIFI` |
| 36 | `AutomaticWifiConnectionPolicy` | `SetAutomaticWifiConnectionUseCase`, `GetAutomaticWifiConnectionUseCase` | `setAutomaticConnectionToWifi()`, `getAutomaticConnectionToWifi()` | `MODIFIES_WIFI`, `AFFECTS_CONNECTIVITY` |
| 37 | `WifiSsidRestrictionActivePolicy` | `ActivateWifiSsidRestrictionUseCase`, `IsWifiSsidRestrictionActiveUseCase` | `activateWifiSsidRestriction()`, `isWifiSsidRestrictionActive()` | `MODIFIES_WIFI`, `SECURITY_SENSITIVE` |
| 38 | `WifiPasswordHiddenPolicy` | `SetWifiPasswordHiddenUseCase`, `GetWifiPasswordHiddenUseCase` | `setPasswordHidden()`, `getPasswordHidden()` | `MODIFIES_WIFI`, `MODIFIES_DISPLAY` |

### 2.4 AdvancedRestrictionPolicy - Connectivity

| # | Policy | Use Cases | Knox API | Capabilities |
|---|--------|-----------|----------|--------------|
| 39 | `AllowBlePolicy` | `AllowBleUseCase`, `IsBleAllowedUseCase` | `allowBLE()`, `isBLEAllowed()` | `MODIFIES_BLUETOOTH`, `AFFECTS_CONNECTIVITY` |
| 40 | `AllowWifiScanningPolicy` | `AllowWifiScanningUseCase`, `IsWifiScanningAllowedUseCase` | `allowWifiScanning()`, `isWifiScanningAllowed()` | `MODIFIES_WIFI`, `AFFECTS_CONNECTIVITY` |

### 2.5 NfcPolicy

| # | Policy | Use Cases | Knox API | Capabilities |
|---|--------|-----------|----------|--------------|
| 41 | `NfcEnabledPolicy` | `SetNfcEnabledUseCase`, `IsNfcStartedUseCase` | `startNFC()`, `isNFCStarted()` | `MODIFIES_NETWORK`, `AFFECTS_CONNECTIVITY` |
| 42 | `AllowNfcStateChangePolicy` | `AllowNfcStateChangeUseCase`, `IsNfcStateChangeAllowedUseCase` | `allowNFCStateChange()`, `isNFCStateChangeAllowed()` | `MODIFIES_NETWORK`, `SECURITY_SENSITIVE` |

### 2.6 RoamingPolicy

| # | Policy | Use Cases | Knox API | Capabilities |
|---|--------|-----------|----------|--------------|
| 43 | `RoamingDataEnabledPolicy` | `SetRoamingDataUseCase`, `IsRoamingDataEnabledUseCase` | `setRoamingData()`, `isRoamingDataEnabled()` | `MODIFIES_RADIO`, `REQUIRES_SIM`, `AFFECTS_CONNECTIVITY` |
| 44 | `RoamingVoiceCallsEnabledPolicy` | `SetRoamingVoiceCallsUseCase`, `IsRoamingVoiceCallsEnabledUseCase` | `setRoamingVoiceCalls()`, `isRoamingVoiceCallsEnabled()` | `MODIFIES_RADIO`, `MODIFIES_CALLING`, `REQUIRES_SIM` |
| 45 | `RoamingSyncEnabledPolicy` | `SetRoamingSyncUseCase`, `IsRoamingSyncEnabledUseCase` | `setRoamingSync()`, `isRoamingSyncEnabled()` | `MODIFIES_NETWORK`, `REQUIRES_SIM` |
| 46 | `RoamingPushEnabledPolicy` | `SetRoamingPushUseCase`, `IsRoamingPushEnabledUseCase` | `setRoamingPush()`, `isRoamingPushEnabled()` | `MODIFIES_NETWORK`, `REQUIRES_SIM` |

---

## Phase 3: Media & Hardware Controls (Priority: High)

### 3.1 RestrictionPolicy - Media

| # | Policy | Use Cases | Knox API | Capabilities |
|---|--------|-----------|----------|--------------|
| 47 | `CameraEnabledPolicy` | `SetCameraEnabledUseCase`, `IsCameraEnabledUseCase` | `setCameraState()`, `isCameraEnabled()` | `MODIFIES_HARDWARE`, `SECURITY_SENSITIVE`, `EASILY_REVERSIBLE` |
| 48 | `MicrophoneEnabledPolicy` | `SetMicrophoneEnabledUseCase`, `IsMicrophoneEnabledUseCase` | `setMicrophoneState()`, `isMicrophoneEnabled()` | `MODIFIES_HARDWARE`, `MODIFIES_AUDIO`, `SECURITY_SENSITIVE`, `EASILY_REVERSIBLE` |
| 49 | `AllowAudioRecordPolicy` | `AllowAudioRecordUseCase`, `IsAudioRecordAllowedUseCase` | `allowAudioRecord()`, `isAudioRecordAllowed()` | `MODIFIES_AUDIO`, `SECURITY_SENSITIVE`, `EASILY_REVERSIBLE` |
| 50 | `AllowVideoRecordPolicy` | `AllowVideoRecordUseCase`, `IsVideoRecordAllowedUseCase` | `allowVideoRecord()`, `isVideoRecordAllowed()` | `MODIFIES_HARDWARE`, `SECURITY_SENSITIVE`, `EASILY_REVERSIBLE` |
| 51 | `ScreenCaptureEnabledPolicy` | `SetScreenCaptureEnabledUseCase`, `IsScreenCaptureEnabledUseCase` | `setScreenCapture()`, `isScreenCaptureEnabled()` | `MODIFIES_DISPLAY`, `SECURITY_SENSITIVE`, `EASILY_REVERSIBLE` |

### 3.2 RestrictionPolicy - Audio/Hardware

| # | Policy | Use Cases | Knox API | Capabilities |
|---|--------|-----------|----------|--------------|
| 52 | `HeadphoneEnabledPolicy` | `SetHeadphoneEnabledUseCase`, `IsHeadphoneEnabledUseCase` | `setHeadphoneState()`, `isHeadphoneEnabled()` | `MODIFIES_AUDIO`, `MODIFIES_HARDWARE`, `EASILY_REVERSIBLE` |
| 53 | `HomeKeyEnabledPolicy` | `SetHomeKeyEnabledUseCase`, `IsHomeKeyEnabledUseCase` | `setHomeKeyState()`, `isHomeKeyEnabled()` | `MODIFIES_HARDWARE`, `SECURITY_SENSITIVE` |

### 3.3 RestrictionPolicy - Storage

| # | Policy | Use Cases | Knox API | Capabilities |
|---|--------|-----------|----------|--------------|
| 54 | `SdCardEnabledPolicy` | `SetSdCardEnabledUseCase`, `IsSdCardEnabledUseCase` | `setSdCardState()`, `isSdCardEnabled()` | `MODIFIES_HARDWARE`, `SECURITY_SENSITIVE` |
| 55 | `AllowSDCardWritePolicy` | `AllowSDCardWriteUseCase`, `IsSDCardWriteAllowedUseCase` | `allowSDCardWrite()`, `isSDCardWriteAllowed()` | `MODIFIES_HARDWARE`, `SECURITY_SENSITIVE` |
| 56 | `AllowSDCardMovePolicy` | `AllowSDCardMoveUseCase`, `IsSDCardMoveAllowedUseCase` | `allowSDCardMove()`, `isSDCardMoveAllowed()` | `MODIFIES_HARDWARE` |
| 57 | `UsbMediaPlayerAvailablePolicy` | `SetUsbMediaPlayerAvailableUseCase`, `IsUsbMediaPlayerAvailableUseCase` | `setUsbMediaPlayerAvailability()`, `isUsbMediaPlayerAvailable()` | `MODIFIES_HARDWARE`, `EASILY_REVERSIBLE` |

---

## Phase 4: Security Controls (Priority: High)

### 4.1 RestrictionPolicy - Clipboard & Data

| # | Policy | Use Cases | Knox API | Capabilities |
|---|--------|-----------|----------|--------------|
| 58 | `ClipboardEnabledPolicy` | `SetClipboardEnabledUseCase`, `IsClipboardAllowedUseCase` | `setClipboardEnabled()`, `isClipboardAllowed()` | `MODIFIES_SECURITY`, `SECURITY_SENSITIVE`, `EASILY_REVERSIBLE` |
| 59 | `AllowClipboardSharePolicy` | `AllowClipboardShareUseCase`, `IsClipboardShareAllowedUseCase` | `allowClipboardShare()`, `isClipboardShareAllowed()` | `MODIFIES_SECURITY`, `SECURITY_SENSITIVE`, `EASILY_REVERSIBLE` |

### 4.2 RestrictionPolicy - Debug & Development

| # | Policy | Use Cases | Knox API | Capabilities |
|---|--------|-----------|----------|--------------|
| 60 | `UsbDebuggingEnabledPolicy` | `SetUsbDebuggingEnabledUseCase`, `IsUsbDebuggingEnabledUseCase` | `setUsbDebuggingEnabled()`, `isUsbDebuggingEnabled()` | `MODIFIES_SECURITY`, `SECURITY_SENSITIVE` |
| 61 | `MockLocationEnabledPolicy` | `SetMockLocationEnabledUseCase`, `IsMockLocationEnabledUseCase` | `setMockLocation()`, `isMockLocationEnabled()` | `MODIFIES_SECURITY`, `SECURITY_SENSITIVE` |

### 4.3 RestrictionPolicy - Lock Screen

| # | Policy | Use Cases | Knox API | Capabilities |
|---|--------|-----------|----------|--------------|
| 62 | `LockScreenEnabledPolicy` | `SetLockScreenEnabledUseCase`, `IsLockScreenEnabledUseCase` | `setLockScreenState()`, `isLockScreenEnabled()` | `MODIFIES_SECURITY`, `SECURITY_SENSITIVE` |

### 4.4 PasswordPolicy - Authentication

| # | Policy | Use Cases | Knox API | Capabilities |
|---|--------|-----------|----------|--------------|
| 63 | `PasswordVisibilityEnabledPolicy` | `SetPasswordVisibilityEnabledUseCase`, `IsPasswordVisibilityEnabledUseCase` | `setPasswordVisibilityEnabled()`, `isPasswordVisibilityEnabled()` | `MODIFIES_SECURITY`, `MODIFIES_DISPLAY` |
| 64 | `ScreenLockPatternVisibilityPolicy` | `SetScreenLockPatternVisibilityUseCase`, `IsScreenLockPatternVisibilityEnabledUseCase` | `setScreenLockPatternVisibilityEnabled()`, `isScreenLockPatternVisibilityEnabled()` | `MODIFIES_SECURITY`, `MODIFIES_DISPLAY` |
| 65 | `MultifactorAuthenticationEnabledPolicy` | `SetMultifactorAuthenticationEnabledUseCase`, `IsMultifactorAuthenticationEnabledUseCase` | `setMultifactorAuthenticationEnabled()`, `isMultifactorAuthenticationEnabled()` | `MODIFIES_SECURITY`, `SECURITY_SENSITIVE` |

### 4.5 AdvancedRestrictionPolicy - Security

| # | Policy | Use Cases | Knox API | Capabilities |
|---|--------|-----------|----------|--------------|
| 66 | `AllowLocalContactStoragePolicy` | `AllowLocalContactStorageUseCase`, `IsLocalContactStorageAllowedUseCase` | `allowLocalContactStorage()`, `isLocalContactStorageAllowed()` | `MODIFIES_SECURITY`, `SECURITY_SENSITIVE` |
| 67 | `AllowFirmwareAutoUpdatePolicy` | `AllowFirmwareAutoUpdateUseCase`, `IsFirmwareAutoUpdateAllowedUseCase` | `allowFirmwareAutoUpdate()`, `isFirmwareAutoUpdateAllowed()` | `MODIFIES_SECURITY`, `PERSISTENT_ACROSS_REBOOT` |
| 68 | `AllowOnlySecureConnectionsPolicy` | `AllowOnlySecureConnectionsUseCase`, `IsOnlySecureConnectionsAllowedUseCase` | `allowOnlySecureConnections()`, `isOnlySecureConnectionsAllowed()` | `MODIFIES_SECURITY`, `MODIFIES_NETWORK`, `SECURITY_SENSITIVE` |
| 69 | `AllowUserSetAlwaysOnPolicy` | `AllowUserSetAlwaysOnUseCase`, `IsUserSetAlwaysOnAllowedUseCase` | `allowUserSetAlwaysOn()`, `isUserSetAlwaysOnAllowed()` | `MODIFIES_SECURITY`, `MODIFIES_NETWORK` |
| 70 | `AllowIntelligenceOnlineProcessingPolicy` | `AllowIntelligenceOnlineProcessingUseCase`, `IsIntelligenceOnlineProcessingAllowedUseCase` | `allowIntelligenceOnlineProcessing()`, `isIntelligenceOnlineProcessingAllowed()` | `MODIFIES_SECURITY`, `MODIFIES_NETWORK` |

---

## Phase 5: Telephony Controls (Priority: High)

### 5.1 PhoneRestrictionPolicy - Calls

| # | Policy | Use Cases | Knox API | Capabilities |
|---|--------|-----------|----------|--------------|
| 71 | `EmergencyCallOnlyPolicy` | `SetEmergencyCallOnlyUseCase`, `GetEmergencyCallOnlyUseCase` | `setEmergencyCallOnly()`, `getEmergencyCallOnly()` | `MODIFIES_CALLING`, `REQUIRES_SIM`, `SECURITY_SENSITIVE` |
| 72 | `AllowCallerIdDisplayPolicy` | `AllowCallerIdDisplayUseCase`, `IsCallerIdDisplayAllowedUseCase` | `allowCallerIDDisplay()`, `isCallerIDDisplayAllowed()` | `MODIFIES_CALLING`, `MODIFIES_DISPLAY` |
| 73 | `LimitNumberOfCallsEnabledPolicy` | `EnableLimitNumberOfCallsUseCase`, `IsLimitNumberOfCallsEnabledUseCase` | `enableLimitNumberOfCalls()`, `isLimitNumberOfCallsEnabled()` | `MODIFIES_CALLING`, `REQUIRES_SIM` |

### 5.2 PhoneRestrictionPolicy - SMS/MMS

| # | Policy | Use Cases | Knox API | Capabilities |
|---|--------|-----------|----------|--------------|
| 74 | `AllowIncomingSmsPolicy` | `AllowIncomingSmsUseCase`, `IsIncomingSmsAllowedUseCase` | `allowIncomingSms()`, `isIncomingSmsAllowed()` | `MODIFIES_CALLING`, `REQUIRES_SIM` |
| 75 | `AllowOutgoingSmsPolicy` | `AllowOutgoingSmsUseCase`, `IsOutgoingSmsAllowedUseCase` | `allowOutgoingSms()`, `isOutgoingSmsAllowed()` | `MODIFIES_CALLING`, `REQUIRES_SIM` |
| 76 | `AllowIncomingMmsPolicy` | `AllowIncomingMmsUseCase`, `IsIncomingMmsAllowedUseCase` | `allowIncomingMms()`, `isIncomingMmsAllowed()` | `MODIFIES_CALLING`, `REQUIRES_SIM` |
| 77 | `AllowOutgoingMmsPolicy` | `AllowOutgoingMmsUseCase`, `IsOutgoingMmsAllowedUseCase` | `allowOutgoingMms()`, `isOutgoingMmsAllowed()` | `MODIFIES_CALLING`, `REQUIRES_SIM` |
| 78 | `LimitNumberOfSmsEnabledPolicy` | `EnableLimitNumberOfSmsUseCase`, `IsLimitNumberOfSmsEnabledUseCase` | `enableLimitNumberOfSms()`, `isLimitNumberOfSmsEnabled()` | `MODIFIES_CALLING`, `REQUIRES_SIM` |
| 79 | `BlockSmsWithStorageEnabledPolicy` | `SetBlockSmsWithStorageUseCase`, `IsBlockSmsWithStorageEnabledUseCase` | `blockSmsWithStorage()`, `isBlockSmsWithStorageEnabled()` | `MODIFIES_CALLING`, `REQUIRES_SIM` |
| 80 | `BlockMmsWithStorageEnabledPolicy` | `SetBlockMmsWithStorageUseCase`, `IsBlockMmsWithStorageEnabledUseCase` | `blockMmsWithStorage()`, `isBlockMmsWithStorageEnabled()` | `MODIFIES_CALLING`, `REQUIRES_SIM` |

### 5.3 PhoneRestrictionPolicy - Contacts

| # | Policy | Use Cases | Knox API | Capabilities |
|---|--------|-----------|----------|--------------|
| 81 | `AllowCopyContactToSimPolicy` | `AllowCopyContactToSimUseCase`, `IsCopyContactToSimAllowedUseCase` | `allowCopyContactToSim()`, `isCopyContactToSimAllowed()` | `MODIFIES_CALLING`, `REQUIRES_SIM` |

### 5.4 PhoneRestrictionPolicy - SIM PIN (Use Cases Only)

| # | Use Cases | Knox API | Description |
|---|-----------|----------|-------------|
| 82 | `EnableSimPinLockUseCase` | `enableSimPinLock(String pinCode)` | Enable SIM PIN lock |
| 83 | `DisableSimPinLockUseCase` | `disableSimPinLock(String pinCode)` | Disable SIM PIN lock |
| 84 | `ChangeSimPinCodeUseCase` | `changeSimPinCode(String current, String new)` | Change SIM PIN code |

### 5.5 PhoneRestrictionPolicy - Data Usage

| # | Policy | Use Cases | Knox API | Capabilities |
|---|--------|-----------|----------|--------------|
| 85 | `DataCallLimitEnabledPolicy` | `SetDataCallLimitEnabledUseCase`, `IsDataCallLimitEnabledUseCase` | `setDataCallLimitEnabled()`, `getDataCallLimitEnabled()` | `MODIFIES_RADIO`, `REQUIRES_SIM` |

---

## Phase 6: Display & UI Controls (Priority: Medium)

### 6.1 SystemManager - Display

| # | Policy | Use Cases | Knox API | Capabilities |
|---|--------|-----------|----------|--------------|
| 86 | `LcdBacklightPolicy` | `SetLcdBacklightStateUseCase`, `GetLcdBacklightStateUseCase` | `setLcdBacklightState()`, `getLcdBacklightState()` | `MODIFIES_DISPLAY`, `EASILY_REVERSIBLE` |
| 87 | `AutoRotationPolicy` | `SetAutoRotationStateUseCase`, `GetAutoRotationStateUseCase` | `setAutoRotationState()`, `getAutoRotationState()` | `MODIFIES_DISPLAY`, `EASILY_REVERSIBLE` |
| 88 | `ToastEnabledPolicy` | `SetToastEnabledStateUseCase`, `GetToastEnabledStateUseCase` | `setToastEnabledState()`, `getToastEnabledState()` | `MODIFIES_DISPLAY`, `EASILY_REVERSIBLE` |
| 89 | `VolumePanelEnabledPolicy` | `SetVolumePanelEnabledStateUseCase`, `GetVolumePanelEnabledStateUseCase` | `setVolumePanelEnabledState()`, `getVolumePanelEnabledState()` | `MODIFIES_DISPLAY`, `MODIFIES_AUDIO`, `EASILY_REVERSIBLE` |
| 90 | `DisplayMirroringPolicy` | `SetDisplayMirroringStateUseCase`, `GetDisplayMirroringStateUseCase` | `setDisplayMirroringState()`, `getDisplayMirroringState()` | `MODIFIES_DISPLAY`, `EASILY_REVERSIBLE` |

### 6.2 SystemManager - Status Bar

| # | Policy | Use Cases | Knox API | Capabilities |
|---|--------|-----------|----------|--------------|
| 91 | `StatusBarClockPolicy` | `SetStatusBarClockStateUseCase`, `GetStatusBarClockStateUseCase` | `setStatusBarClockState()`, `getStatusBarClockState()` | `MODIFIES_DISPLAY`, `EASILY_REVERSIBLE` |
| 92 | `StatusBarIconsPolicy` | `SetStatusBarIconsStateUseCase`, `GetStatusBarIconsStateUseCase` | `setStatusBarIconsState()`, `getStatusBarIconsState()` | `MODIFIES_DISPLAY`, `EASILY_REVERSIBLE` |
| 93 | `StatusBarNotificationsPolicy` | `SetStatusBarNotificationsStateUseCase`, `GetStatusBarNotificationsStateUseCase` | `setStatusBarNotificationsState()`, `getStatusBarNotificationsState()` | `MODIFIES_DISPLAY`, `EASILY_REVERSIBLE` |

### 6.3 Font Controls

| # | Use Cases | Knox API | Description |
|---|-----------|----------|-------------|
| 94 | `GetSystemActiveFontUseCase` | `getSystemActiveFont()` | Get current font |
| 95 | `SetSystemActiveFontUseCase` | `setSystemActiveFont(String, String)` | Set device font |
| 96 | `GetSystemFontsUseCase` | `getSystemFonts()` | Get available fonts |
| 97 | `GetSystemActiveFontSizeUseCase` | `getSystemActiveFontSize()` | Get font size |
| 98 | `SetSystemActiveFontSizeUseCase` | `setSystemActiveFontSize(float)` | Set font size |
| 99 | `GetSystemFontSizesUseCase` | `getSystemFontSizes()` | Get available sizes |

---

## Phase 7: Hardware & System Controls (Priority: Medium)

### 7.1 SystemManager - Hardware

| # | Policy | Use Cases | Knox API | Capabilities |
|---|--------|-----------|----------|--------------|
| 100 | `DeviceSpeakerEnabledPolicy` | `SetDeviceSpeakerEnabledStateUseCase`, `GetDeviceSpeakerEnabledStateUseCase` | `setDeviceSpeakerEnabledState()`, `getDeviceSpeakerEnabledState()` | `MODIFIES_AUDIO`, `MODIFIES_HARDWARE`, `EASILY_REVERSIBLE` |
| 101 | `InfraredPolicy` | `SetInfraredStateUseCase`, `GetInfraredStateUseCase` | `setInfraredState()`, `getInfraredState()` | `MODIFIES_HARDWARE`, `EASILY_REVERSIBLE` |
| 102 | `ChargerConnectionSoundPolicy` | `SetChargerConnectionSoundEnabledStateUseCase`, `GetChargerConnectionSoundEnabledStateUseCase` | `setChargerConnectionSoundEnabledState()`, `getChargerConnectionSoundEnabledState()` | `MODIFIES_AUDIO`, `MODIFIES_CHARGING`, `EASILY_REVERSIBLE` |
| 103 | `TorchOnVolumeButtonsPolicy` | `SetTorchOnVolumeButtonsStateUseCase`, `GetTorchOnVolumeButtonsStateUseCase` | `setTorchOnVolumeButtonsState()`, `getTorchOnVolumeButtonsState()` | `MODIFIES_HARDWARE`, `EASILY_REVERSIBLE` |
| 104 | `VolumeButtonRotationPolicy` | `SetVolumeButtonRotationStateUseCase`, `GetVolumeButtonRotationStateUseCase` | `setVolumeButtonRotationState()`, `getVolumeButtonRotationState()` | `MODIFIES_HARDWARE`, `MODIFIES_DISPLAY`, `EASILY_REVERSIBLE` |

### 7.2 SystemManager - USB

| # | Policy | Use Cases | Knox API | Capabilities |
|---|--------|-----------|----------|--------------|
| 105 | `UsbMassStoragePolicy` | `SetUsbMassStorageStateUseCase`, `GetUsbMassStorageStateUseCase` | `setUsbMassStorageState()`, `getUsbMassStorageState()` | `MODIFIES_HARDWARE`, `SECURITY_SENSITIVE` |

### 7.3 SystemManager - Power

| # | Policy | Use Cases | Knox API | Capabilities |
|---|--------|-----------|----------|--------------|
| 106 | `ForceAutoShutDownPolicy` | `SetForceAutoShutDownStateUseCase`, `GetForceAutoShutDownStateUseCase` | `setForceAutoShutDownState()`, `getForceAutoShutDownState()` | `MODIFIES_HARDWARE`, `PERSISTENT_ACROSS_REBOOT` |
| 107 | `ForceAutoStartUpPolicy` | `SetForceAutoStartUpStateUseCase`, `GetForceAutoStartUpStateUseCase` | `setForceAutoStartUpState()`, `getForceAutoStartUpState()` | `MODIFIES_HARDWARE`, `PERSISTENT_ACROSS_REBOOT` |
| 108 | `PowerMenuLockedPolicy` | `SetPowerMenuLockedStateUseCase`, `GetPowerMenuLockedStateUseCase` | `setPowerMenuLockedState()`, `getPowerMenuLockedState()` | `MODIFIES_SECURITY`, `SECURITY_SENSITIVE` |

### 7.4 SystemManager - Input

| # | Policy | Use Cases | Knox API | Capabilities |
|---|--------|-----------|----------|--------------|
| 109 | `ScreenOffOnHomeLongPressPolicy` | `SetScreenOffOnHomeLongPressStateUseCase`, `GetScreenOffOnHomeLongPressStateUseCase` | `setScreenOffOnHomeLongPressState()`, `getScreenOffOnHomeLongPressState()` | `MODIFIES_DISPLAY`, `MODIFIES_HARDWARE` |
| 110 | `ScreenOffOnStatusBarDoubleTapPolicy` | `SetScreenOffOnStatusBarDoubleTapStateUseCase`, `GetScreenOffOnStatusBarDoubleTapStateUseCase` | `setScreenOffOnStatusBarDoubleTapState()`, `getScreenOffOnStatusBarDoubleTapState()` | `MODIFIES_DISPLAY` |
| 111 | `ExtendedCallInfoPolicy` | `SetExtendedCallInfoStateUseCase`, `GetExtendedCallInfoStateUseCase` | `setExtendedCallInfoState()`, `getExtendedCallInfoState()` | `MODIFIES_CALLING`, `MODIFIES_DISPLAY` |

### 7.5 SystemManager - Unlock

| # | Policy | Use Cases | Knox API | Capabilities |
|---|--------|-----------|----------|--------------|
| 112 | `UnlockSimOnBootPolicy` | `SetUnlockSimOnBootStateUseCase`, `GetUnlockSimOnBootStateUseCase` | `setUnlockSimOnBootState()`, `getUnlockSimOnBootState()` | `MODIFIES_SECURITY`, `REQUIRES_SIM`, `PERSISTENT_ACROSS_REBOOT` |

---

## Phase 8: DateTime Controls (Priority: Medium)

### 8.1 DateTimePolicy

| # | Policy | Use Cases | Knox API | Capabilities |
|---|--------|-----------|----------|--------------|
| 113 | `DateTimeChangeEnabledPolicy` | `SetDateTimeChangeEnabledUseCase`, `IsDateTimeChangeEnabledUseCase` | `setDateTimeChangeEnabled()`, `isDateTimeChangeEnabled()` | `MODIFIES_SECURITY`, `SECURITY_SENSITIVE` |
| 114 | `AutomaticTimePolicy` | `SetAutomaticTimeUseCase`, `GetAutomaticTimeUseCase` | `setAutomaticTime()`, `getAutomaticTime()` | `MODIFIES_SECURITY`, `MODIFIES_NETWORK` |

### 8.2 DateTimePolicy - Use Cases Only

| # | Use Cases | Knox API | Description |
|---|-----------|----------|-------------|
| 115 | `SetDateTimeUseCase` | `setDateTime(d,m,y,h,m,s)` | Set device date/time |
| 116 | `GetDateTimeUseCase` | `getDateTime()` | Get device date/time |
| 117 | `SetTimeZoneUseCase` | `setTimeZone(String)` | Set timezone |
| 118 | `GetTimeZoneUseCase` | `getTimeZone()` | Get timezone |
| 119 | `SetTimeFormatUseCase` | `setTimeFormat(String)` | Set 12/24h format |
| 120 | `GetTimeFormatUseCase` | `getTimeFormat()` | Get time format |
| 121 | `GetDateFormatUseCase` | `getDateFormat()` | Get date format |
| 122 | `SetNtpInfoUseCase` | `setNtpInfo(NtpInfo)` | Configure NTP server |
| 123 | `GetNtpInfoUseCase` | `getNtpInfo()` | Get NTP configuration |

---

## Phase 9: Browser Controls (Priority: Medium)

### 9.1 BrowserPolicy

| # | Policy | Use Cases | Knox API | Capabilities |
|---|--------|-----------|----------|--------------|
| 124 | `BrowserJavaScriptPolicy` | `SetBrowserJavaScriptSettingUseCase`, `GetBrowserJavaScriptSettingUseCase` | `setJavaScriptSetting()`, `getJavaScriptSetting()` | `MODIFIES_SECURITY`, `SECURITY_SENSITIVE` |
| 125 | `BrowserCookiesPolicy` | `SetBrowserCookiesSettingUseCase`, `GetBrowserCookiesSettingUseCase` | `setCookiesSetting()`, `getCookiesSetting()` | `MODIFIES_SECURITY` |
| 126 | `BrowserPopupsPolicy` | `SetBrowserPopupsSettingUseCase`, `GetBrowserPopupsSettingUseCase` | `setPopupsSetting()`, `getPopupsSetting()` | `MODIFIES_SECURITY`, `MODIFIES_DISPLAY` |
| 127 | `BrowserForceFraudWarningPolicy` | `SetBrowserForceFraudWarningSettingUseCase`, `GetBrowserForceFraudWarningSettingUseCase` | `setForceFraudWarningSetting()`, `getForceFraudWarningSetting()` | `MODIFIES_SECURITY`, `SECURITY_SENSITIVE` |

### 9.2 BrowserPolicy - Use Cases Only

| # | Use Cases | Knox API | Description |
|---|-----------|----------|-------------|
| 128 | `SetBrowserHttpProxyUseCase` | `setHttpProxy(String)` | Set HTTP proxy |
| 129 | `GetBrowserHttpProxyUseCase` | `getHttpProxy()` | Get HTTP proxy |
| 130 | `ClearBrowserHttpProxyUseCase` | `clearHttpProxy()` | Clear HTTP proxy |
| 131 | `AddWebBookmarkUseCase` | `addWebBookmarkBitmap()` | Add bookmark |
| 132 | `DeleteWebBookmarkUseCase` | `deleteWebBookmark()` | Remove bookmark |

---

## Phase 10: Firewall Controls (Priority: Medium)

### 10.1 Firewall Policy

| # | Policy | Use Cases | Knox API | Capabilities |
|---|--------|-----------|----------|--------------|
| 133 | `FirewallEnabledPolicy` | `SetFirewallEnabledUseCase`, `IsFirewallEnabledUseCase` | `enableFirewall()`, `isFirewallEnabled()` | `MODIFIES_NETWORK`, `SECURITY_SENSITIVE` |
| 134 | `DomainFilterReportEnabledPolicy` | `SetDomainFilterReportEnabledUseCase`, `IsDomainFilterReportEnabledUseCase` | `enableDomainFilterReport()`, `isDomainFilterReportEnabled()` | `MODIFIES_NETWORK`, `SECURITY_SENSITIVE` |
| 135 | `DomainFilterOnIptablesEnabledPolicy` | `SetDomainFilterOnIptablesEnabledUseCase`, `IsDomainFilterOnIptablesEnabledUseCase` | `enableDomainFilterOnIptables()`, `isDomainFilterOnIptablesEnabled()` | `MODIFIES_NETWORK`, `SECURITY_SENSITIVE` |

### 10.2 Firewall - Use Cases Only (Complex)

| # | Use Cases | Knox API | Description |
|---|-----------|----------|-------------|
| 136 | `AddFirewallRulesUseCase` | `addRules(FirewallRule[])` | Add IP rules |
| 137 | `RemoveFirewallRulesUseCase` | `removeRules(FirewallRule[])` | Remove IP rules |
| 138 | `GetFirewallRulesUseCase` | `getRules(int, Status)` | Get rules |
| 139 | `ClearFirewallRulesUseCase` | `clearRules(int)` | Clear rules |
| 140 | `AddDomainFilterRulesUseCase` | `addDomainFilterRules(List)` | Add domain rules |
| 141 | `RemoveDomainFilterRulesUseCase` | `removeDomainFilterRules(List)` | Remove domain rules |
| 142 | `GetDomainFilterRulesUseCase` | `getDomainFilterRules(List)` | Get domain rules |
| 143 | `GetDomainFilterReportUseCase` | `getDomainFilterReport(List)` | Get blocked URLs |

---

## Phase 11: Application Controls (Priority: Medium)

### 11.1 ApplicationPolicy - Mode Controls (Use Cases Only)

| # | Use Cases | Knox API | Description |
|---|-----------|----------|-------------|
| 144 | `SetApplicationInstallationModeUseCase` | `setApplicationInstallationMode(int)` | Set install mode |
| 145 | `GetApplicationInstallationModeUseCase` | `getApplicationInstallationMode()` | Get install mode |
| 146 | `SetApplicationUninstallationModeUseCase` | `setApplicationUninstallationMode(int)` | Set uninstall mode |
| 147 | `GetApplicationUninstallationModeUseCase` | `getApplicationUninstallationMode()` | Get uninstall mode |
| 148 | `SetApplicationNotificationModeUseCase` | `setApplicationNotificationMode(int)` | Set notification mode |
| 149 | `GetApplicationNotificationModeUseCase` | `getApplicationNotificationMode()` | Get notification mode |

### 11.2 ApplicationPolicy - State Management

| # | Use Cases | Knox API | Description |
|---|-----------|----------|-------------|
| 150 | `SetApplicationStateUseCase` | `setApplicationStateList(String[], boolean)` | Enable/disable apps |
| 151 | `GetApplicationStateUseCase` | `getApplicationStateEnabled(String)` | Get app state |
| 152 | `GetApplicationStateListUseCase` | `getApplicationStateList(boolean)` | Get enabled/disabled list |

### 11.3 ApplicationPolicy - Blacklist/Whitelist

| # | Use Cases | Knox API | Description |
|---|-----------|----------|-------------|
| 153 | `AddAppToBlackListUseCase` | `addAppPackageNameToBlackList(String)` | Blacklist app |
| 154 | `RemoveAppFromBlackListUseCase` | `removeAppPackageNameFromBlackList(String)` | Remove from blacklist |
| 155 | `AddAppToWhiteListUseCase` | `addAppPackageNameToWhiteList(String)` | Whitelist app |
| 156 | `RemoveAppFromWhiteListUseCase` | `removeAppPackageNameFromWhiteList(String)` | Remove from whitelist |
| 157 | `ClearAppListsUseCase` | `clearAppPackageNameFromList()` | Clear all lists |
| 158 | `GetBlacklistedAppsUseCase` | `getAppPackageNamesAllBlackLists()` | Get blacklisted |
| 159 | `GetWhitelistedAppsUseCase` | `getAppPackageNamesAllWhiteLists()` | Get whitelisted |

### 11.4 ApplicationPolicy - Notification Controls

| # | Use Cases | Knox API | Description |
|---|-----------|----------|-------------|
| 160 | `AddPackagesToNotificationBlackListUseCase` | `addPackagesToNotificationBlackList(List)` | Block notifications |
| 161 | `RemovePackagesFromNotificationBlackListUseCase` | `removePackagesFromNotificationBlackList(List)` | Unblock notifications |
| 162 | `AddPackagesToNotificationWhiteListUseCase` | `addPackagesToNotificationWhiteList(List, boolean)` | Whitelist notifications |
| 163 | `RemovePackagesFromNotificationWhiteListUseCase` | `removePackagesFromNotificationWhiteList(List)` | Remove from whitelist |

### 11.5 ApplicationPolicy - Prevent Start

| # | Use Cases | Knox API | Description |
|---|-----------|----------|-------------|
| 164 | `AddPackagesToPreventStartBlackListUseCase` | `addPackagesToPreventStartBlackList(List)` | Block app launch |
| 165 | `RemovePackagesFromPreventStartBlackListUseCase` | `removePackagesFromPreventStartBlackList(List)` | Allow app launch |
| 166 | `ClearPreventStartBlackListUseCase` | `clearPreventStartBlackList()` | Clear prevent list |

### 11.6 ApplicationPolicy - Force Stop Controls

| # | Use Cases | Knox API | Description |
|---|-----------|----------|-------------|
| 167 | `AddPackagesToForceStopBlackListUseCase` | `addPackagesToForceStopBlackList(List)` | Prevent force stop |
| 168 | `RemovePackagesFromForceStopBlackListUseCase` | `removePackagesFromForceStopBlackList(List)` | Allow force stop |
| 169 | `AddPackagesToForceStopWhiteListUseCase` | `addPackagesToForceStopWhiteList(List)` | Only allow force stop |
| 170 | `RemovePackagesFromForceStopWhiteListUseCase` | `removePackagesFromForceStopWhiteList(List)` | Remove from whitelist |
| 171 | `ClearForceStopListsUseCase` | `clearPackagesFromForceStopList()` | Clear all force stop lists |

### 11.7 ApplicationPolicy - Update Controls

| # | Use Cases | Knox API | Description |
|---|-----------|----------|-------------|
| 172 | `AddPackagesToDisableUpdateBlackListUseCase` | `addPackagesToDisableUpdateBlackList(List)` | Block updates |
| 173 | `RemovePackagesFromDisableUpdateBlackListUseCase` | `removePackagesFromDisableUpdateBlackList(List)` | Allow updates |
| 174 | `AddPackagesToDisableUpdateWhiteListUseCase` | `addPackagesToDisableUpdateWhiteList(List)` | Only allow updates |
| 175 | `RemovePackagesFromDisableUpdateWhiteListUseCase` | `removePackagesFromDisableUpdateWhiteList(List)` | Remove from whitelist |

### 11.8 ApplicationPolicy - Cache Controls

| # | Use Cases | Knox API | Description |
|---|-----------|----------|-------------|
| 176 | `AddPackagesToClearCacheBlackListUseCase` | `addPackagesToClearCacheBlackList(List)` | Prevent cache clear |
| 177 | `AddPackagesToClearCacheWhiteListUseCase` | `addPackagesToClearCacheWhiteList(List)` | Only allow cache clear |

### 11.9 ApplicationPolicy - Widget Controls

| # | Use Cases | Knox API | Description |
|---|-----------|----------|-------------|
| 178 | `AddPackagesToWidgetBlackListUseCase` | `addPackagesToWidgetBlackList(List)` | Block widgets |
| 179 | `RemovePackagesFromWidgetBlackListUseCase` | `removePackagesFromWidgetBlackList(List)` | Allow widgets |
| 180 | `AddPackagesToWidgetWhiteListUseCase` | `addPackagesToWidgetWhiteList(List, boolean)` | Whitelist widgets |
| 181 | `RemovePackagesFromWidgetWhiteListUseCase` | `removePackagesFromWidgetWhiteList(List)` | Remove from whitelist |
| 182 | `GetAllWidgetsUseCase` | `getAllWidgets(String)` | Get active widgets |

### 11.10 ApplicationPolicy - Information & Actions

| # | Use Cases | Knox API | Description |
|---|-----------|----------|-------------|
| 183 | `IsApplicationInstalledUseCase` | `isApplicationInstalled(String)` | Check if installed |
| 184 | `IsApplicationRunningUseCase` | `isApplicationRunning(String)` | Check if running |
| 185 | `GetApplicationNameUseCase` | `getApplicationName(String)` | Get app name |
| 186 | `GetApplicationVersionUseCase` | `getApplicationVersion(String)` | Get version string |
| 187 | `GetApplicationVersionCodeUseCase` | `getApplicationVersionCode(String)` | Get version code |
| 188 | `GetApplicationUidUseCase` | `getApplicationUid(String)` | Get app UID |
| 189 | `GetInstalledApplicationsUseCase` | `getInstalledApplicationsIDList()` | List all apps |
| 190 | `StartApplicationUseCase` | `startApp(String, String)` | Launch app |
| 191 | `StopApplicationUseCase` | `stopApp(String)` | Stop app |
| 192 | `UninstallApplicationUseCase` | `uninstallApplication(String, boolean)` | Remove app |
| 193 | `UninstallApplicationsUseCase` | `uninstallApplications(List)` | Remove multiple apps |
| 194 | `WipeApplicationDataUseCase` | `wipeApplicationData(String)` | Clear app data |
| 195 | `UpdateApplicationUseCase` | `updateApplication(String)` | Update app |

### 11.11 ApplicationPolicy - Resource Usage

| # | Use Cases | Knox API | Description |
|---|-----------|----------|-------------|
| 196 | `GetApplicationTotalSizeUseCase` | `getApplicationTotalSize(String)` | Get total size |
| 197 | `GetApplicationCacheSizeUseCase` | `getApplicationCacheSize(String)` | Get cache size |
| 198 | `GetApplicationCodeSizeUseCase` | `getApplicationCodeSize(String)` | Get code size |
| 199 | `GetApplicationDataSizeUseCase` | `getApplicationDataSize(String)` | Get data size |
| 200 | `GetApplicationMemoryUsageUseCase` | `getApplicationMemoryUsage(String)` | Get RAM usage |
| 201 | `GetApplicationCpuUsageUseCase` | `getApplicationCpuUsage(String)` | Get CPU usage |

### 11.12 ApplicationPolicy - Battery Optimization

| # | Use Cases | Knox API | Description |
|---|-----------|----------|-------------|
| 202 | `AddPackageToBatteryOptimizationWhiteListUseCase` | `addPackageToBatteryOptimizationWhiteList(AppIdentity)` | Exempt from battery optimization |
| 203 | `RemovePackageFromBatteryOptimizationWhiteListUseCase` | `removePackageFromBatteryOptimizationWhiteList(AppIdentity)` | Remove exemption |
| 204 | `GetBatteryOptimizationWhiteListUseCase` | `getPackagesFromBatteryOptimizationWhiteList()` | Get exemption list |

### 11.13 ApplicationPolicy - Focus Monitoring

| # | Use Cases | Knox API | Description |
|---|-----------|----------|-------------|
| 205 | `AddPackagesToFocusMonitoringListUseCase` | `addPackagesToFocusMonitoringList(List)` | Start monitoring |
| 206 | `RemovePackagesFromFocusMonitoringListUseCase` | `removePackagesFromFocusMonitoringList(List)` | Stop monitoring |
| 207 | `ClearFocusMonitoringListUseCase` | `clearFocusMonitoringList()` | Clear monitoring |

### 11.14 ApplicationPolicy - Concentration Mode

| # | Policy | Use Cases | Knox API | Capabilities |
|---|--------|-----------|----------|--------------|
| 208 | `ConcentrationModePolicy` | `SetConcentrationModeUseCase`, `GetConcentrationModeUseCase` | `setConcentrationMode()`, `getConcentrationMode()` | `MODIFIES_SECURITY`, `SECURITY_SENSITIVE` |

---

## Phase 12: Device Inventory (Priority: Low)

### 12.1 DeviceInventory - Use Cases Only

| # | Use Cases | Knox API | Description |
|---|-----------|----------|-------------|
| 209 | `GetSerialNumberUseCase` | `getSerialNumber()` | Device serial |
| 210 | `GetSalesCodeUseCase` | `getSalesCode()` | Samsung CSC code |
| 211 | `GetDeviceOsUseCase` | `getDeviceOS()` | OS name |
| 212 | `GetDeviceOsVersionUseCase` | `getDeviceOSVersion()` | OS version |
| 213 | `GetKnoxServiceIdUseCase` | `getKnoxServiceId()` | Knox service ID |
| 214 | `IsDeviceLockedUseCase` | `isDeviceLocked()` | Lock status |
| 215 | `IsDeviceSecureUseCase` | `isDeviceSecure()` | Security status |
| 216 | `GetAvailableStorageInternalUseCase` | `getAvailableCapacityInternal()` | Internal free space |
| 217 | `GetAvailableStorageExternalUseCase` | `getAvailableCapacityExternal()` | External free space |
| 218 | `GetTotalStorageInternalUseCase` | `getTotalCapacityInternal()` | Internal total space |
| 219 | `GetTotalStorageExternalUseCase` | `getTotalCapacityExternal()` | External total space |
| 220 | `GetDroppedCallsCountUseCase` | `getDroppedCallsCount()` | Dropped calls |
| 221 | `GetMissedCallsCountUseCase` | `getMissedCallsCount()` | Missed calls |
| 222 | `GetSuccessCallsCountUseCase` | `getSuccessCallsCount()` | Successful calls |
| 223 | `ResetCallsCountUseCase` | `resetCallsCount()` | Reset call counters |
| 224 | `GetLastSimChangeInfoUseCase` | `getLastSimChangeInfo()` | SIM change info |

---

## Phase 13: WiFi SSID Management (Priority: Low)

### 13.1 WifiPolicy - SSID Blacklist/Whitelist

| # | Use Cases | Knox API | Description |
|---|-----------|----------|-------------|
| 225 | `AddWifiSsidsToBlackListUseCase` | `addWifiSsidsToBlackList(List)` | Block SSIDs |
| 226 | `RemoveWifiSsidsFromBlackListUseCase` | `removeWifiSsidsFromBlackList(List)` | Unblock SSIDs |
| 227 | `ClearWifiSsidsFromBlackListUseCase` | `clearWifiSsidsFromBlackList()` | Clear SSID blacklist |
| 228 | `AddWifiSsidsToWhiteListUseCase` | `addWifiSsidsToWhiteList(List)` | Whitelist SSIDs |
| 229 | `RemoveWifiSsidsFromWhiteListUseCase` | `removeWifiSsidsFromWhiteList(List)` | Remove from whitelist |
| 230 | `ClearWifiSsidsFromWhiteListUseCase` | `clearWifiSsidsFromWhiteList()` | Clear SSID whitelist |
| 231 | `ClearWifiSsidsFromListUseCase` | `clearWifiSsidsFromList()` | Clear all SSID lists |
| 232 | `GetWifiSsidsFromBlackListsUseCase` | `getWifiSsidsFromBlackLists()` | Get blacklisted SSIDs |
| 233 | `GetWifiSsidsFromWhiteListsUseCase` | `getWifiSsidsFromWhiteLists()` | Get whitelisted SSIDs |

### 13.2 WifiPolicy - Network Management

| # | Use Cases | Knox API | Description |
|---|-----------|----------|-------------|
| 234 | `AddBlockedNetworkUseCase` | `addBlockedNetwork(String)` | Block network |
| 235 | `RemoveBlockedNetworkUseCase` | `removeBlockedNetwork(String)` | Unblock network |
| 236 | `GetBlockedNetworksUseCase` | `getBlockedNetworks()` | Get blocked networks |
| 237 | `IsNetworkBlockedUseCase` | `isNetworkBlocked(String, boolean)` | Check if blocked |
| 238 | `GetNetworkSsidListUseCase` | `getNetworkSSIDList()` | Get enterprise SSIDs |
| 239 | `RemoveNetworkConfigurationUseCase` | `removeNetworkConfiguration(String)` | Remove WLAN config |

### 13.3 WifiPolicy - Security

| # | Use Cases | Knox API | Description |
|---|-----------|----------|-------------|
| 240 | `SetMinimumWifiSecurityUseCase` | `setMinimumRequiredSecurity(int)` | Set min security level |
| 241 | `GetMinimumWifiSecurityUseCase` | `getMinimumRequiredSecurity()` | Get min security level |

---

## Phase 14: PasswordPolicy - Advanced (Priority: Low)

### 14.1 Password Requirements (Use Cases Only)

| # | Use Cases | Knox API | Description |
|---|-----------|----------|-------------|
| 242 | `SetMinPasswordComplexCharsUseCase` | `setMinPasswordComplexChars(ComponentName, int)` | Min complex chars |
| 243 | `GetMinPasswordComplexCharsUseCase` | `getMinPasswordComplexChars(ComponentName)` | Get min complex chars |
| 244 | `SetMaxCharacterOccurrencesUseCase` | `setMaximumCharacterOccurrences(int)` | Max char repeats |
| 245 | `GetMaxCharacterOccurrencesUseCase` | `getMaximumCharacterOccurences()` | Get max repeats |
| 246 | `SetMaxCharacterSequenceLengthUseCase` | `setMaximumCharacterSequenceLength(int)` | Max alphabetic sequence |
| 247 | `GetMaxCharacterSequenceLengthUseCase` | `getMaximumCharacterSequenceLength()` | Get max sequence |
| 248 | `SetMaxNumericSequenceLengthUseCase` | `setMaximumNumericSequenceLength(int)` | Max numeric sequence |
| 249 | `GetMaxNumericSequenceLengthUseCase` | `getMaximumNumericSequenceLength()` | Get max numeric |
| 250 | `SetMinCharacterChangeLengthUseCase` | `setMinimumCharacterChangeLength(int)` | Min password diff |
| 251 | `GetMinCharacterChangeLengthUseCase` | `getMinimumCharacterChangeLength()` | Get min diff |
| 252 | `SetForbiddenStringsUseCase` | `setForbiddenStrings(List)` | Set forbidden strings |
| 253 | `GetForbiddenStringsUseCase` | `getForbiddenStrings(boolean)` | Get forbidden strings |
| 254 | `SetRequiredPasswordPatternUseCase` | `setRequiredPasswordPattern(String)` | Set regex pattern |
| 255 | `GetRequiredPasswordPatternUseCase` | `getRequiredPwdPatternRestrictions(boolean)` | Get pattern |

### 14.2 Password Expiration

| # | Use Cases | Knox API | Description |
|---|-----------|----------|-------------|
| 256 | `SetPasswordExpiresUseCase` | `setPasswordExpires(ComponentName, int)` | Set expiration days |
| 257 | `GetPasswordExpiresUseCase` | `getPasswordExpires(ComponentName)` | Get expiration |
| 258 | `SetPasswordHistoryUseCase` | `setPasswordHistory(ComponentName, int)` | Set history count |
| 259 | `GetPasswordHistoryUseCase` | `getPasswordHistory(ComponentName)` | Get history count |

### 14.3 Password Lock

| # | Use Cases | Knox API | Description |
|---|-----------|----------|-------------|
| 260 | `SetPasswordLockDelayUseCase` | `setPasswordLockDelay(int)` | Set lock delay |
| 261 | `GetPasswordLockDelayUseCase` | `getPasswordLockDelay()` | Get lock delay |
| 262 | `EnforcePwdChangeUseCase` | `enforcePwdChange()` | Force password change |
| 263 | `SetPasswordChangeTimeoutUseCase` | `setPasswordChangeTimeout(int)` | Set change timeout |
| 264 | `GetPasswordChangeTimeoutUseCase` | `getPasswordChangeTimeout()` | Get change timeout |

### 14.4 Failed Attempts

| # | Use Cases | Knox API | Description |
|---|-----------|----------|-------------|
| 265 | `SetMaxFailedPasswordsForDeviceDisableUseCase` | `setMaximumFailedPasswordsForDeviceDisable(int)` | Set max failures |
| 266 | `GetMaxFailedPasswordsForDeviceDisableUseCase` | `getMaximumFailedPasswordsForDeviceDisable()` | Get max failures |
| 267 | `ExcludeExternalStorageForFailedPasswordsWipeUseCase` | `excludeExternalStorageForFailedPasswordsWipe(boolean)` | Exclude external storage |
| 268 | `IsExternalStorageForFailedPasswordsWipeExcludedUseCase` | `isExternalStorageForFailedPasswordsWipeExcluded()` | Check exclusion |

### 14.5 Biometric Authentication

| # | Use Cases | Knox API | Description |
|---|-----------|----------|-------------|
| 269 | `SetBiometricAuthEnabledUseCase` | `setBiometricAuthenticationEnabled(int, boolean)` | Enable/disable biometric |
| 270 | `IsBiometricAuthEnabledUseCase` | `isBiometricAuthenticationEnabled(int)` | Check biometric state |
| 271 | `GetSupportedBiometricAuthenticationsUseCase` | `getSupportedBiometricAuthentications()` | Get available biometrics |

---

## Phase 15: SystemManager - Audio (Priority: Low)

### 15.1 Audio Controls

| # | Use Cases | Knox API | Description |
|---|-----------|----------|-------------|
| 272 | `SetAudioVolumeUseCase` | `setAudioVolume(int, int)` | Set volume level |
| 273 | `SetVibrationIntensityUseCase` | `setVibrationIntensity(int, int)` | Set vibration |
| 274 | `GetVibrationIntensityUseCase` | `getVibrationIntensity(int)` | Get vibration |
| 275 | `SetSystemRingtoneUseCase` | `setSystemRingtone(int, String)` | Set ringtone |

---

## Phase 16: SystemManager - Quick Panel (Priority: Low)

### 16.1 Quick Panel Controls

| # | Use Cases | Knox API | Description |
|---|-----------|----------|-------------|
| 276 | `SetQuickPanelButtonsUseCase` | `setQuickPanelButtons(int)` | Show/hide buttons |
| 277 | `GetQuickPanelButtonsUseCase` | `getQuickPanelButtons()` | Get button state |
| 278 | `SetQuickPanelEditModeUseCase` | `setQuickPanelEditMode(int)` | Set edit mode |
| 279 | `GetQuickPanelEditModeUseCase` | `getQuickPanelEditMode()` | Get edit mode |
| 280 | `SetQuickPanelItemsUseCase` | `setQuickPanelItems(List)` | Set items |
| 281 | `GetQuickPanelItemsUseCase` | `getQuickPanelItems()` | Get items |

---

## Implementation Summary

### Total Components

| Category | Policies | Use Cases |
|----------|----------|-----------|
| Device Restrictions | 18 | 36 |
| Connectivity | 28 | 56 |
| Media & Hardware | 11 | 22 |
| Security | 13 | 26 |
| Telephony | 16 | 32+ |
| Display & UI | 8 | 30+ |
| Hardware & System | 13 | 26 |
| DateTime | 2 | 18 |
| Browser | 4 | 10 |
| Firewall | 3 | 16 |
| Application | 1 | 65+ |
| Device Inventory | - | 16 |
| WiFi SSID Management | - | 17 |
| Password Advanced | - | 28 |
| Audio | - | 4 |
| Quick Panel | - | 6 |
| **Total** | **~117 policies** | **~400+ use cases** |

### Domain Models to Create

1. `InstallationMode` - App installation mode enum
2. `UninstallationMode` - App uninstallation mode enum
3. `NotificationMode` - App notification mode enum
4. `BiometricType` - Biometric authentication types
5. `ApplicationInfo` - Application information data class
6. `WifiSecurityLevel` - WiFi security level enum
7. `TimeFormat` - 12h/24h format enum
8. `AudioStream` - Audio stream type enum
9. `VibrationMode` - Vibration mode enum
10. `QuickPanelButton` - Quick panel button flags
11. `StatusBarMode` - Status bar visibility modes
12. `FirewallRuleType` - Firewall rule categories
13. `DomainFilterRule` - Domain filter configuration
14. `SimChangeInfo` - SIM card change data class
15. `NtpInfo` - NTP server configuration
16. `CallRestrictionPattern` - Phone restriction patterns
17. `RoamingSettings` - Roaming configuration bundle

### New PolicyCapabilities to Add

```kotlin
// Suggested additions to PolicyCapability enum
MODIFIES_STORAGE,      // SD card, USB storage policies
MODIFIES_USB,          // USB-specific policies
MODIFIES_TETHERING,    // Tethering-specific policies
MODIFIES_APPLICATIONS, // Application management policies
MODIFIES_DATETIME,     // Date/time policies
MODIFIES_BROWSER,      // Browser policies
MODIFIES_FIREWALL,     // Firewall policies
MODIFIES_TELEPHONY,    // SMS/MMS/Call policies
```

---

## Recommended Implementation Order

1. **Phase 1-2** - Core Device Restrictions & Connectivity (most commonly needed)
2. **Phase 3-4** - Media/Hardware & Security (privacy/security critical)
3. **Phase 5** - Telephony Controls (SMS/call management)
4. **Phase 6-7** - Display/UI & Hardware Controls
5. **Phase 8-10** - DateTime, Browser, Firewall
6. **Phase 11** - Application Controls (largest set)
7. **Phase 12-16** - Device Inventory, WiFi SSID, Password, Audio, Quick Panel

---

## Notes

- All use cases should use `WithAndroidApplicationContext` for Knox SDK access
- Use `StateMapping.INVERTED` when the Knox API uses opposite semantics
- Complex policies requiring parameters should be use-cases only, not registered policies
- Action-based operations (start/stop app, wipe data) should be use-cases only
- Deprecated APIs are included for completeness but should be noted in documentation
- Some SystemManager methods require `KNOX_CUSTOM_SETTING` permission
