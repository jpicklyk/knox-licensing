Cloning Project:
- After cloning the project, you may need to rename the folder due to case sensitivity reasons.
- Rename knoxmoduleshowcase to KnoxModuleShowcase prior to opening in Android Studio

Testing DPC
Enable DPC:
adb shell dpm set-device-owner net.sfelabs.knoxmoduleshowcase/.app.receivers.AdminReceiver

Disable DPC: (Can only be done when AndroidManifest contains android:testOnly="true")
adb shell dpm remove-active-admin net.sfelabs.knoxmoduleshowcase/.app.receivers.AdminReceiver