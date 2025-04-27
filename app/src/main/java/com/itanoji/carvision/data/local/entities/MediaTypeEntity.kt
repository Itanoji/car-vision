package com.itanoji.carvision.data.local.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity(tableName = "media_types")
data class MediaTypeEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "media_type_id")
    val id: Long = 0,
    val code: String,
    val description: String
)
