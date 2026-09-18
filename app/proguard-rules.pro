# MindScale R8 / ProGuard Rules

# Room Database
-keep class * extends androidx.room.RoomDatabase
-keep @androidx.room.Entity class *
-dontwarn androidx.room.paging.**

# Kotlin Coroutines & Attributes
-keepattributes *Annotation*,Signature,InnerClasses,EnclosingMethod
-keepclassmembers class kotlinx.coroutines.** { *; }

# DataStore Preferences
-keep class androidx.datastore.** { *; }

# Jetpack Compose Runtime
-keep class androidx.compose.runtime.** { *; }
