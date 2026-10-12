# Add these rules

# Keep SLF4J
-dontwarn org.slf4j.**
-keep class org.slf4j.** { *; }

# Keep iTextPDF
-dontwarn com.itextpdf.**
-keep class com.itextpdf.** { *; }

# Keep Yandex Ads
-dontwarn com.yandex.mobile.ads.**
-keep class com.yandex.mobile.ads.** { *; }

# Keep Room
-dontwarn androidx.room.**
-keep class androidx.room.** { *; }
-keep interface androidx.room.** { *; }

# Keep Compose
-dontwarn androidx.compose.**
-keep class androidx.compose.** { *; }

# Keep all app classes
-keep class com.mody.costkeeper.** { *; }

# Remove logs in release
-assumenosideeffects class android.util.Log {
    public static *** d(...);
    public static *** v(...);
    public static *** i(...);
    public static *** w(...);
    public static *** e(...);
}