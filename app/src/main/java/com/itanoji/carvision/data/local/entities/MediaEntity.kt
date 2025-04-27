package com.itanoji.carvision.data.local.entities

import androidx.room.*
import java.util.*

@Entity(
    tableName = "media",
    foreignKeys = [
        // Справочник media_type
        ForeignKey(
            entity = MediaTypeEntity::class,
            parentColumns = ["media_type_id"],
            childColumns = ["media_type_id"],
            onDelete = ForeignKey.RESTRICT
        ),
        // Результат осмотра
        ForeignKey(
            entity = InspectionResultEntity::class,
            parentColumns = ["inspection_result_id"],
            childColumns = ["inspection_result_id"],
            onDelete = ForeignKey.CASCADE
        ),
        // Ссылка на сам осмотр
        ForeignKey(
            entity = InspectionEntity::class,
            parentColumns = ["inspection_id"],
            childColumns = ["inspection_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index("media_type_id"),
        Index("inspection_result_id"),
        Index("inspection_id")
    ]
)
data class MediaEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "media_id")
    val id: Long = 0,

    @ColumnInfo(name = "server_id")
    val serverId: Long? = null,

    @ColumnInfo(name = "media_type_id")
    val mediaTypeId: Long?,

    val filename: String,

    @ColumnInfo(name = "inspection_id")
    val inspectionId: Long?,

    @ColumnInfo(name = "inspection_result_id")
    val resultId: Long?,

    @ColumnInfo(name = "created_at")
    val createdAt: Date = Date()
)

