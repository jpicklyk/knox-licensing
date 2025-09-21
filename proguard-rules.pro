# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.

# Knox SDK classes should not be obfuscated
-keep class com.samsung.android.knox.** { *; }
-dontwarn com.samsung.android.knox.**

# Keep all public API classes
-keep public class com.github.jpicklyk.knox.licensing.** {
    public *;
}