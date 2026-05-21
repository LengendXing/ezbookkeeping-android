package com.ezbookkeeping.android.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "ezbk_external_auths")
data class ExternalAuthEntity(
    @PrimaryKey val id: Int,
    val userId: Int,
    val provider: String,
    val providerUserId: String
)
