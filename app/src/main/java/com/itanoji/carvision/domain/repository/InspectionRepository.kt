package com.itanoji.carvision.domain.repository

import com.itanoji.carvision.domain.model.Inspection
import com.itanoji.carvision.domain.model.InspectionResult
import com.itanoji.carvision.domain.model.Media
import kotlinx.coroutines.flow.Flow

interface InspectionRepository {
    /**
     * Получить все осмотры
     */
    fun getAllInspections(): Flow<List<Inspection>>

    /**
     * Получить осмотр по идентификатору
     */
    fun getInspectionById(id: Long): Flow<Inspection?>

    /**
     * Сохранить осмотр
     */
    suspend fun insertInspection(inspection: Inspection): Long

    suspend fun updateInspection(inspection: Inspection)

    suspend fun deleteInspection(inspection: Inspection)

    /**
     * Получить результат осмотра
     */
    fun getResultForInspection(id: Long): Flow<InspectionResult?>

    fun getInspectionResultById(id: Long): Flow<InspectionResult?>

    /**
     * Сохранить результат осмотра
     */
    suspend fun saveInspectionResult(result: InspectionResult): Long

    /**
     * Получить список медиа для результата осмотра
     */
    fun getMediaForResult(resultId: Long): Flow<List<Media>>

    /**
     * Создать медия
     */
    suspend fun insertMedia(media: Media): Long

    /**
     * Обновить медиа
     */
    suspend fun updateMedia(media: Media)

    /**
     * Получить медиа по идентификатору
     */
    fun getMediaById(mediaId: Long): Flow<Media>
}