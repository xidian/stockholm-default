# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile
-dontoptimize
-dontpreverify
-dontobfuscate
-keepattributes *Annotation*
-keepattributes Signature
-keep class com.morgoo.droidplugin.** {*;}
-keep class retrofit2.** { *; }
-keepclasseswithmembers class * {
    @retrofit.http.* <methods>;
}
-keep class okhttp3.** { *; }
-keep class org.apache.mina.** { *; }
-keep class net.jodah.typetools.** { *; }
-keep class org.joda.time.** { *; }
-keep class org.slf4j.** { *; }
-keep interface okhttp3.** { *; }
-dontwarn com.morgoo.droidplugin.**
-dontwarn rx.**
-dontwarn retrofit2.**
-dontwarn okio.**
-dontwarn javax.annotation.**
-dontwarn okhttp3.**
-dontwarn org.apache.mina.**
-dontwarn net.jodah.typetools.**
-dontwarn org.joda.time.**
-dontwarn org.slf4j.**
-dontwarn java.lang.invoke.**

-dontwarn cn.jpush.**
-keep class cn.jpush.** { *; }
-keep class * extends cn.jpush.android.helpers.JPushMessageReceiver { *; }

-dontwarn cn.jiguang.**
-keep class cn.jiguang.** { *; }

-dontwarn com.tencent.bugly.**
-keep public class com.tencent.bugly.**{*;}

-keep class sun.misc.Unsafe { *; }
-keep class com.google.gson.examples.android.model.** { *; }

-keep class com.stockholm.meow.common.** { *; }
-keep class com.raizlabs.android.dbflow.** { *; }