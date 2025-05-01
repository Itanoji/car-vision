package com.itanoji.carvision.ui.inspection.view

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.itanoji.carvision.data.storage.FileStorageManager
import com.itanoji.carvision.domain.model.Inspection
import com.itanoji.carvision.domain.repository.InspectionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.File


class InspectionDetailViewModel(
    private val repository: InspectionRepository,
    private val fileStorage: FileStorageManager,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val inspectionId: Long = checkNotNull(savedStateHandle["id"])

    private val _uiState = MutableStateFlow(InspectionDetailUiState())
    val uiState: StateFlow<InspectionDetailUiState> = _uiState.asStateFlow()

    suspend fun loadData() {
        val insp = repository.getInspectionById(inspectionId).first()
        if (insp != null) {
            _uiState.update { it.copy(
                inspection = insp,
                title = insp.title,
                comment = insp.description,
                previewFile = insp.avatarMediaId?.let{getAvatarFile(insp.avatarMediaId)}
            ) }
        }
    }

    fun startInspection(navController: NavController) {
        navController.navigate("inspectionResult/$inspectionId")
    }

    /**
     * Получить файл аватара по ID медиа.
     * Если медиа не найдётся или файла нет — вернёт null.
     */
    private suspend fun getAvatarFile(mediaId: Long): File? {
        val media = repository.getMediaById(mediaId).firstOrNull()
        return media?.let { fileStorage.getImageFile(it.filename) }
    }
}

data class InspectionDetailUiState(
    val inspection: Inspection? = null,
    val title: String = "",
    val comment: String? = null,
    val previewFile: File? = null
)

