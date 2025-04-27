package com.itanoji.carvision.data.local.entities

import androidx.room.*
import java.util.*

@Entity(
    tableName = "inspection_results",
    foreignKeys = [
        ForeignKey(
            entity = InspectionEntity::class,
            parentColumns = ["inspection_id"],
            childColumns = ["inspection_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("inspection_id")]
)
data class InspectionResultEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "inspection_result_id")
    val id: Long = 0,
    @ColumnInfo(name = "server_id")
    val serverId: Long? = null,
    @ColumnInfo(name = "license_plate") val licensePlate: String?,
    val color: String?,
    val brand: String?,
    @ColumnInfo(name = "inspection_time") val inspectionTime: Int?,
    @ColumnInfo(name = "inspection_date") val inspectionDate: Date?,
    @ColumnInfo(name = "inspection_id") val inspectionId: Long
)
