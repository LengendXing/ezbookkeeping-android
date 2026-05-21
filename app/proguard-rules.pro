# Kotlin Serialization
-keepattributes *Annotation*, StandardClassName
-keep class kotlinx.serialization.Serializable
-keepclassmembers class kotlinx.serialization.Serializable {
    ** Companion;
    *** serializer();
}
-keepclasseswithmembers class kotlinx.serialization.Serializable {
    kotlinx.serialization.KSerializer serializer(...);
}
-keepclassmembers class **.**$$serializer {
    *** Companion;
    *** serializer(...);
}
-keepclassmembers class **.**Serializer {
    *** Companion;
    *** serializer(...);
}
-keepclassmembers class **.**$$serializer { *; }

# Room
-keep class * extends androidx.room.RoomDatabase
-keep @androidx.room.Entity class *

# Hilt
-dontwarn dagger.hilt.**

# Retrofit
-keepattributes Signature
-keepattributes Exceptions
-keep class retrofit2.** { *; }
-keepclassmembers,allowobfuscation interface * {
    @retrofit2.http.* <methods>;
}
