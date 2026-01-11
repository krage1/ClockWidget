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
-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
-renamesourcefileattribute SourceFile

# Сохранять аннотации (важно для библиотек типа Retrofit, Room, Dagger)
-keepattributes *Annotation*
-keepattributes Signature
-keepattributes EnclosingMethod

# Обычно Android сам знает, что нельзя удалять Activity, но для надежности:
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service

# Вариант Б (Проще): Сказать ProGuard не трогать ваши модели данных.
# Замените 'com.example.yourapp.models' на ваш пакет с POJO классами
-keep class ru.krage.clockwidget.models.** { *; }