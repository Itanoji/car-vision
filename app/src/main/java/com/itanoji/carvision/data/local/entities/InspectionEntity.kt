package com.itanoji.carvision.data.local.entities

import androidx.room.*
import java.util.*

@Entity(
    tableName = "inspections",
    foreignKeys = [
        ForeignKey(
            entity = InspectionResultEntity::class,
            parentColumns = ["inspection_result_id"],
            childColumns = ["inspection_result_id"],
            onDelete = ForeignKey.SET_NULL
        ),
        ForeignKey(
            entity = MediaEntity::class,
            parentColumns = ["media_id"],
            childColumns = ["avatar_media_id"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [
        Index("inspection_result_id"),
        Index("avatar_media_id")
    ]
)
data class InspectionEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "inspection_id")
    val id: Long = 0,
    @ColumnInfo(name = "server_id")
    val serverId: Long? = null,
    val title: String,
    val description: String? = null,
    @ColumnInfo(name = "inspection_result_id")
    val inspectionResultId: Long? = null,
    @ColumnInfo(name = "avatar_media_id")
    val avatarMediaId: Long? = null
)
