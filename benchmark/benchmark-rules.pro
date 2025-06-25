# Benchmark specific ProGuard rules

# Keep all benchmark related classes
-keep class androidx.benchmark.** { *; }

# Preserve line numbers for better stack traces
-keepattributes SourceFile,LineNumberTable

# Keep Knox APIs for proper functionality
-keep class com.samsung.android.knox.** { *; }
-keep class com.sec.enterprise.** { *; }

# Don't obfuscate test classes
-keep class **.*Test { *; }
-keep class **.*Benchmark { *; }