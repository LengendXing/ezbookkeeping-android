package com.ezbookkeeping.android.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "ezbk_tag_groups")
data class TagGroupEntity(
    @PrimaryKey val id: Int,
    val userId: Int,
    val name: String,
    val order: Int = 0
)
