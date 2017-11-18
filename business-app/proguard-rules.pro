# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /Users/browserwang/Develop/tools/android-sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

-dontoptimize
-dontpreverify
-dontobfuscate
-keepattributes *Annotation*
-keepattributes Signature
-keep class retrofit2.** { *; }
-keepclasseswithmembers class * {
    @retrofit.http.* <methods>;
}
-keep class org.apache.mina.** { *; }
-keep class okhttp3.** { *; }
-keep interface okhttp3.** { *; }
-keep class net.jodah.typetools.** { *; }
-keep class org.joda.time.** { *; }
-keep class org.slf4j.** { *; }
-dontwarn rx.**
-dontwarn retrofit2.**
-dontwarn okio.**
-dontwarn javax.annotation.**
-dontwarn okhttp3.**
-dontwarn java.lang.invoke.**
-dontwarn org.apache.mina.**
-dontwarn net.jodah.typetools.**
-dontwarn org.joda.time.**
-dontwarn org.slf4j.**

-dontwarn com.tencent.bugly.**
-keep public class com.tencent.bugly.**{*;}

-keep class sun.misc.Unsafe {*;}
-keep class com.google.gson.examples.android.model.** {*;}

-keep class com.raizlabs.android.dbflow.** {*;}

-dontwarn com.squareup.okhttp.**

