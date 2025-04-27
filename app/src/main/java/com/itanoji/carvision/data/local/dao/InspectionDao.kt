package com.itanoji.carvision.data.local.dao

import androidx.room.*
import com.itanoji.carvision.data.local.entities.InspectionEntity

import kotlinx.coroutines.flow.Flow

@Dao
interface InspectionDao {
    @Query("SELECT * FROM inspections")
    fun getAll(): Flow<List<InspectionEntity>>

    @Query("SELECT * FROM inspections WHERE inspection_id = :id")
    fun getById(id: Long): Flow<InspectionEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(ins: InspectionEntity): Long

    @Update
    suspend fun update(ins: InspectionEntity)

    @Delete
    suspend fun delete(ins: InspectionEntity)
}