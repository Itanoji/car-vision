package com.itanoji.carvision.data.local.dao

import androidx.room.*
import com.itanoji.carvision.data.local.entities.InspectionResultEntity
import kotlinx.coroutines.flow.Flow
import java.util.*

@Dao
interface InspectionResultDao {
    @Query("SELECT * FROM inspection_results WHERE inspection_id = :inspId")
    fun getByInspectionId(inspId: Long): Flow<InspectionResultEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(res: InspectionResultEntity)

    @Delete
    suspend fun delete(res: InspectionResultEntity)
}
