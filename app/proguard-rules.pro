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

# Room - keep all entities and DAOs fully
-keep class * extends androidx.room.RoomDatabase { *; }
-keep @androidx.room.Entity class *
-keep @androidx.room.Dao class *
-keepclassmembers class * {
    @androidx.room.Query <methods>;
    @androidx.room.Insert <methods>;
    @androidx.room.Update <methods>;
    @androidx.room.Delete <methods>;
}

# Room TypeConverters
-keep class * implements androidx.room.TypeConverter { *; }
-keepclassmembers class * {
    @androidx.room.TypeConverters *;
}

# Hilt
-dontwarn dagger.hilt.**
-keep class dagger.hilt.** { *; }

# Retrofit
-keepattributes Signature
-keepattributes Exceptions
-keep class retrofit2.** { *; }
-keepclassmembers,allowobfuscation interface * {
    @retrofit2.http.* <methods>;
}

# OkHttp
-dontwarn okhttp3.**
-dontwarn okio.**

# Compose - prevent stripping composables
-keep class androidx.compose.** { *; }
-dontwarn androidx.compose.**

# Keep all entity/model classes used by the app
-keep class com.ezbookkeeping.android.data.db.entity.** { *; }
-keep class com.ezbookkeeping.android.data.db.dao.** { *; }
-keep class com.ezbookkeeping.android.data.db.converter.** { *; }
-keep class com.ezbookkeeping.android.data.remote.dto.** { *; }
-keep class com.ezbookkeeping.android.data.remote.api.** { *; }
-keep class com.ezbookkeeping.android.data.local.** { *; }
-keep class com.ezbookkeeping.android.data.repository.** { *; }
-keep class com.ezbookkeeping.android.service.** { *; }
-keep class com.ezbookkeeping.android.util.** { *; }
-keep class com.ezbookkeeping.android.ui.navigation.** { *; }

# ViewModels
-keep class * extends androidx.lifecycle.ViewModel { *; }

# Keep Compose screen functions
-keepclassmembers class com.ezbookkeeping.android.ui.screen.** {
    *** ***(...);
}
