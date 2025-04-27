package com.itanoji.carvision.data.repository

import com.itanoji.carvision.data.local.dao.InspectionDao
import com.itanoji.carvision.data.local.dao.InspectionResultDao
import com.itanoji.carvision.data.local.dao.MediaDao
import com.itanoji.carvision.domain.model.Inspection
import com.itanoji.carvision.domain.model.InspectionResult
import com.itanoji.carvision.domain.model.Media
import com.itanoji.carvision.domain.repository.InspectionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import com.itanoji.carvision.data.local.entities.*
import kotlinx.coroutines.flow.mapNotNull

class InspectionRepositoryImpl(
    private val inspectionDao: InspectionDao,
    private val resultDao: InspectionResultDao,
    private val mediaDao: MediaDao
) : InspectionRepository {

    override fun getAllInspections(): Flow<List<Inspection>> =
        inspectionDao.getAll().map { list ->
            list.map { it.toDomain() }
        }

    override fun getInspectionById(id: Long): Flow<Inspection?> =
        inspectionDao.getById(id).map { it?.toDomain() }

    override suspend fun deleteInspection(inspection: Inspection) {
        inspectionDao.delete(inspection.toEntity())
    }

    override suspend fun insertInspection(inspection: Inspection): Long {
        val entity = inspection.toEntity()
        return inspectionDao.insert(entity)
    }

    override suspend fun updateInspection(inspection: Inspection) {
        val entity = inspection.toEntity()
        inspectionDao.update(entity)
    }

    override fun getResultForInspection(id: Long): Flow<InspectionResult?> =
        resultDao.getByInspectionId(id).mapNotNull { it?.toDomain() }

    override suspend fun saveInspectionResult(result: InspectionResult) {
        val entity = result.toEntity()
        resultDao.insert(entity)
    }

    override fun getMediaForResult(resultId: Long): Flow<List<Media>> =
        mediaDao.getByResultId(resultId).map { list ->
            list.map { it.toDomain() }
        }

    override suspend fun saveMedia(media: Media): Long {
        val entity = media.toEntity()
        return mediaDao.insert(entity)
    }

    override fun getMediaById(mediaId: Long): Flow<Media> =
        mediaDao.getById(mediaId).mapNotNull { it.toDomain() }
}
