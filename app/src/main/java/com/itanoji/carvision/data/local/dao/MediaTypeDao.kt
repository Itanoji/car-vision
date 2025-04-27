package com.itanoji.carvision.data.local.dao

import androidx.room.*
import com.itanoji.carvision.data.local.entities.MediaTypeEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MediaTypeDao {
    @Query("SELECT * FROM media_types")
    fun getAll(): Flow<List<MediaTypeEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(type: MediaTypeEntity)
}