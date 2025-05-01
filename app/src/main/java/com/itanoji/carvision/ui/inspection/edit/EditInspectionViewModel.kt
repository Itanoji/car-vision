package com.itanoji.carvision.ui.inspection.edit

import android.net.Uri
import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.itanoji.carvision.data.storage.FileStorageManager
import com.itanoji.carvision.domain.model.Inspection
import com.itanoji.carvision.domain.model.Media
import com.itanoji.carvision.domain.repository.InspectionRepository
import com.itanoji.carvision.ui.inspection.view.InspectionDetailUiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.File
import java.io.IOException
import java.util.UUID

class EditInspectionViewModel(
    private val repository: InspectionRepository,
    private val fileStorage: FileStorageManager,
    savedStateHandle : SavedStateHandle
) : ViewModel() {

    private val inspectionId: Long = checkNotNull(savedStateHandle["id"])

    private val _uiState = MutableStateFlow(InspectionDetailUiState())
    val uiState: StateFlow<InspectionDetailUiState> = _uiState.asStateFlow()

    private val _updated = MutableSharedFlow<Unit>(replay = 0)
    val updated: SharedFlow<Unit> = _updated.asSharedFlow()

    init {
        viewModelScope.launch {
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
    }

    fun onTitleChange(value: String) {
        _uiState.update { it.copy(
            title = value
        ) }
    }

    fun onCommentChange(value: String) {
        _uiState.update { it.copy(
            comment = value
        ) }
    }

    fun onImagePicked(uri: Uri) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val relPath = "${UUID.randomUUID()}.jpg"
                val bitmap = fileStorage.loadBitmapFromUri(uri, maxSize = 1024)
                fileStorage.saveImage(relPath, bitmap)
                _uiState.update { it.copy(
                    previewFile = fileStorage.getImageFile(relPath)
                ) }
            } catch (e: IOException) {
                Log.e("CreateInspectionVM", "Ошибка при загрузке изображения: ${e.message}")
            } catch (e: Exception) {
                Log.e("CreateInspectionVM", "Неизвестная ошибка: ${e.message}")
            }
        }
    }

    fun updateInspection() {
        viewModelScope.launch {
            // Собираем поля из state flows
            val t = _uiState.value.title
            val c = _uiState.value.comment
            val old_avatar = _uiState.value.inspection?.avatarMediaId
            var mediaId = old_avatar

            // если есть выбранный локальный файл
            _uiState.value.previewFile?.let { file ->
                //Если аватарка была - обновляем, если нет - создаём новую
                if (old_avatar != null) {
                    repository.updateMedia(
                        Media(
                            id = old_avatar,
                            inspectionId = inspectionId,
                            resultId = null,
                            type = null,
                            filename = file.name
                        )
                    )
                }
                else {
                    mediaId = repository.insertMedia(
                        Media(
                            id = 0L,
                            inspectionId = inspectionId,
                            resultId = null,
                            type = null,
                            filename = file.name
                        )
                    )
                }
            }

            // сохраняем
            repository.updateInspection(
                Inspection(
                    id = inspectionId,
                    inspectionResultId = _uiState.value.inspection?.inspectionResultId,
                    title = t,
                    description = c,
                    avatarMediaId = mediaId
                )
            )

            // сигналим успех
            _updated.emit(Unit)
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

data class InspectionDetailUiState(
    val inspection: Inspection? = null,
    val title: String = "",
    val comment: String? = null,
    val previewFile: File? = null,
)
