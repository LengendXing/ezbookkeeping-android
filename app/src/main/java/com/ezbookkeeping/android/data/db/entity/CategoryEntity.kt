package com.ezbookkeeping.android.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "ezbk_categories")
data class CategoryEntity(
    @PrimaryKey val id: Int,
    val userId: Int,
    val type: CategoryType,
    val parentId: Int? = null,
    val name: String,
    val icon: String,
    val color: String,
    val order: Int = 0,
    val isHidden: Boolean = false
)
