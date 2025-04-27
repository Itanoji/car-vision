package com.itanoji.carvision.data.local.dao

import androidx.room.*
import com.itanoji.carvision.data.local.entities.MediaEntity
import com.itanoji.carvision.domain.model.Media
import kotlinx.coroutines.flow.Flow
import java.util.*

@Dao
interface MediaDao {
    @Query("SELECT * FROM media WHERE inspection_result_id = :resId")
    fun getByResultId(resId: Long): Flow<List<MediaEntity>>

    @Query("SELECT * FROM media WHERE media_id = :mediaId")
    fun getById(mediaId: Long): Flow<MediaEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(media: MediaEntity): Long

    @Delete
    suspend fun delete(media: MediaEntity)
}