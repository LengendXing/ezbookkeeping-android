package com.ezbookkeeping.android.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "ezbk_cloud_settings")
data class CloudSettingEntity(
    @PrimaryKey val id: Int,
    val userId: Int,
    val key: String,
    val value: String
)
