package com.itanoji.carvision.data.local.dao

import androidx.room.*
import com.itanoji.carvision.data.local.entities.InspectionResultEntity
import kotlinx.coroutines.flow.Flow
import java.util.*

@Dao
interface InspectionResultDao {
    @Query("SELECT * FROM inspection_results WHERE inspection_id = :inspId")
    fun getByInspectionId(inspId: Long): Flow<InspectionResultEntity?>

    @Query("SELECT * FROM inspection_results WHERE inspection_result_id = :inspResultId")
    fun getByInspectionResultId(inspResultId: Long): Flow<InspectionResultEntity?>

    @Insert()
    suspend fun insert(res: InspectionResultEntity): Long

    @Delete
    suspend fun delete(res: InspectionResultEntity)
}
