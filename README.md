Testing DPC
Enable DPC:
adb shell dpm set-device-owner net.sfelabs.knoxmoduleshowcase/.receivers.AdminReceiver

Disable DPC: (Can only be done when AndroidManifest contains android:testOnly="true")
adb shell dpm remove-active-admin net.sfelabs.knoxmoduleshowcase/.receivers.AdminReceiver