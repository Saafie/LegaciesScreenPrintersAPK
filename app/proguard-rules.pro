# R8 keep rules for the release build.
# Reference: Android Developers (2025) Shrink, obfuscate, and optimize your app - https://developer.android.com/build/shrink-code

# Gson: keep the network model fields so JSON still maps after shrinking.
-keep class com.example.legaciesscreenprintersapk.ui.remote.models.** { *; }
-keepattributes Signature, *Annotation*, EnclosingMethod, InnerClasses
-keep class com.google.gson.reflect.TypeToken { *; }
-keep class * extends com.google.gson.reflect.TypeToken

# Retrofit: keep the API interface annotations and return types.
-keep,allowobfuscation interface com.example.legaciesscreenprintersapk.ui.remote.ApiService
-keepattributes RuntimeVisibleAnnotations, RuntimeVisibleParameterAnnotations, AnnotationDefault
-keep,allowobfuscation,allowshrinking class kotlin.coroutines.Continuation
-keep,allowobfuscation,allowshrinking class retrofit2.Response

# Google sign-in (Credential Manager).
-keep class com.google.android.libraries.identity.googleid.** { *; }
-keep class androidx.credentials.** { *; }
-if class androidx.credentials.CredentialManager
-keep class androidx.credentials.playservices.** { *; }

# Keep line numbers for crash reports.
-keepattributes SourceFile, LineNumberTable
-renamesourcefileattribute SourceFile
