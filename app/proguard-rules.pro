# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.kts.kts.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Knox SDK - Keep all Knox classes and methods
-keep class com.samsung.android.knox.** { *; }
-keep class com.sec.enterprise.** { *; }
-keep interface com.samsung.android.knox.** { *; }
-keep interface com.sec.enterprise.** { *; }

# Knox Tactical SDK - Keep all tactical classes
-keep class com.samsung.android.knox.tactical.** { *; }

# HDM (Hardware Device Management) specific classes
-keep class com.samsung.android.knox.hdm.** { *; }

# Knox License Manager
-keep class com.samsung.android.knox.license.** { *; }

# Don't warn about missing Knox classes (they're provided at runtime)
-dontwarn com.samsung.android.knox.**
-dontwarn com.sec.enterprise.**
-dontwarn com.samsung.android.knox.hdm.HdmManager

# Keep Knox use cases and domain classes
-keep class net.sfelabs.knox_enterprise.** { *; }
-keep class net.sfelabs.knox_tactical.** { *; }

# Keep reflection-based classes
-keepclassmembers class ** {
    @com.samsung.android.knox.** *;
}

# Preserve line numbers for debugging
-keepattributes SourceFile,LineNumberTable

# Keep native methods
-keepclasseswithmembernames class * {
    native <methods>;
}

# Hilt/Dagger
-keep class dagger.hilt.** { *; }
-keep class javax.inject.** { *; }
-keep class * extends dagger.hilt.android.lifecycle.HiltViewModel

# Compose
-keep class androidx.compose.** { *; }

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}