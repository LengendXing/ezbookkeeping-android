package com.ezbookkeeping.android.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "ezbk_tags")
data class TagEntity(
    @PrimaryKey val id: Int,
    val userId: Int,
    val groupId: Int,
    val name: String
)
