# Knox SDK classes should not be obfuscated in consumer apps
-keep class com.samsung.android.knox.** { *; }
-dontwarn com.samsung.android.knox.**