package com.itanoji.carvision.ui.inspections

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.itanoji.carvision.data.storage.FileStorageManager
import com.itanoji.carvision.domain.model.Inspection
import com.itanoji.carvision.domain.repository.InspectionRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.io.File

class InspectionsListViewModel(
    private val inspectionRepo: InspectionRepository,
    private val fileStorage: FileStorageManager
) : ViewModel() {
    private val _inspections = MutableStateFlow<List<Inspection>>(emptyList())
    val inspections: StateFlow<List<Inspection>> = _inspections

    init {
        viewModelScope.launch {
            inspectionRepo.getAllInspections()
                .collect { list ->
                    _inspections.value = list
                }
        }
    }

    fun deleteInspection(inspection: Inspection) {
        viewModelScope.launch {
            // 1) удаляем сам осмотр (каскадно уйдёт и результат, и связанное медиа-аватар)
            inspectionRepo.deleteInspection(inspection)

            inspection.avatarMediaId?.let { mediaId ->
                val media = inspectionRepo.getMediaById(mediaId).first()
                fileStorage.deleteImage(media.filename)
            }
        }
    }

    /**
     * Получить файл аватара по ID медиа.
     * Если медиа не найдётся или файла нет — вернёт null.
     */
    suspend fun getAvatarFile(mediaId: Long): File? {
        val media = inspectionRepo.getMediaById(mediaId).firstOrNull()
        return media?.let { fileStorage.getImageFile(it.filename) }
    }
}