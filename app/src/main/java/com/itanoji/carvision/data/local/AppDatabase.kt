package com.itanoji.carvision.data.local

import androidx.room.*
import com.itanoji.carvision.data.local.dao.InspectionDao
import com.itanoji.carvision.data.local.dao.InspectionResultDao
import com.itanoji.carvision.data.local.dao.MediaDao
import com.itanoji.carvision.data.local.dao.MediaTypeDao
import com.itanoji.carvision.data.local.entities.InspectionEntity
import com.itanoji.carvision.data.local.entities.InspectionResultEntity
import com.itanoji.carvision.data.local.entities.MediaEntity
import com.itanoji.carvision.data.local.entities.MediaTypeEntity

@Database(
    entities = [
        InspectionEntity::class,
        InspectionResultEntity::class,
        MediaTypeEntity::class,
        MediaEntity::class
    ],
    version = 3,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun inspectionDao(): InspectionDao
    abstract fun inspectionResultDao(): InspectionResultDao
    abstract fun mediaTypeDao(): MediaTypeDao
    abstract fun mediaDao(): MediaDao
}