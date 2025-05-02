package com.itanoji.carvision.ui.inspection_result

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.itanoji.carvision.R
import com.itanoji.carvision.data.storage.FileStorageManager
import com.itanoji.carvision.domain.model.Inspection
import com.itanoji.carvision.domain.repository.InspectionRepository
import com.itanoji.carvision.navigation.Screen
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.File


class InspectionResultViewModel(
    private val repository: InspectionRepository,
    private val fileStorage: FileStorageManager,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val inspectionResultId: Long = checkNotNull(savedStateHandle["id"])

    private val _uiState = MutableStateFlow(InspectionResultUiState())
    val uiState: StateFlow<InspectionResultUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            val res = repository.getInspectionResultById(inspectionResultId).first()
            if (res != null) {
                _uiState.update { it.copy(
                    inspectionId = res.inspectionId
                ) }
            }
        }
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

data class InspectionResultUiState(
    val inspectionId: Long = 0L,
    val licensePlate: String = "B964EX197",
    val color: String = "синий",
    val brand: String = "BMW",
    val scratchImageIds: List<Int> = listOf(R.drawable.mock_scratch_1, R.drawable.mock_scratch_2),
    val dentImageIds: List<Int> = listOf(R.drawable.mock_dent_1, R.drawable.mock_dent_2, R.drawable.mock_dent_1, R.drawable.mock_dent_3)
)

