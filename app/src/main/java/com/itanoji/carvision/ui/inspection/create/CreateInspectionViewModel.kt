package com.itanoji.carvision.ui.inspection.create

import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.itanoji.carvision.data.storage.FileStorageManager
import com.itanoji.carvision.domain.model.Inspection
import com.itanoji.carvision.domain.model.Media
import com.itanoji.carvision.domain.repository.InspectionRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.io.File
import java.io.IOException
import java.util.UUID

class CreateInspectionViewModel(
    private val repo: InspectionRepository,
    private val fileStorage: FileStorageManager
) : ViewModel() {

    // Title
    private val _title = MutableStateFlow("")
    val title: StateFlow<String> = _title.asStateFlow()

    // Comment / description
    private val _comment = MutableStateFlow("")
    val comment: StateFlow<String> = _comment.asStateFlow()

    // Chosen image file (preview)
    private val _previewFile = MutableStateFlow<File?>(null)
    val previewFile: StateFlow<File?> = _previewFile.asStateFlow()

    // Для возврата назад или успеха
    private val _created = MutableSharedFlow<Unit>(replay = 0)
    val created: SharedFlow<Unit> = _created.asSharedFlow()

    fun onTitleChange(value: String) {
        _title.value = value
    }

    fun onCommentChange(value: String) {
        _comment.value = value
    }

    fun onImagePicked(uri: Uri) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val relPath = "${UUID.randomUUID()}.jpg"
                val bitmap = fileStorage.loadBitmapFromUri(uri, maxSize = 1024)
                fileStorage.saveImage(relPath, bitmap)
                _previewFile.value = fileStorage.getImageFile(relPath)
            } catch (e: IOException) {
                Log.e("CreateInspectionVM", "Ошибка при загрузке изображения: ${e.message}")
            } catch (e: Exception) {
                Log.e("CreateInspectionVM", "Неизвестная ошибка: ${e.message}")
            }
        }
    }

    fun createInspection() {
        viewModelScope.launch {
            // Собираем поля из state flows
            val t = title.value
            val c = comment.value
            // создаём запись без аватара
            val newId = repo.insertInspection(
                Inspection(
                    id = 0L,
                    inspectionResultId = null,
                    title = t,
                    description = c,
                    avatarMediaId = null
                )
            )
            // если есть выбранный локальный файл
            previewFile.value?.let { file ->
                val mediaId = repo.insertMedia(
                    Media(
                        id = 0L,
                        inspectionId = newId,
                        resultId = null,
                        type = null,
                        filename = file.name
                    )
                )
                repo.getInspectionById(newId).first()?.let {
                    repo.updateInspection(it.copy(avatarMediaId = mediaId))
                }
            }
            // сигналим успех
            _created.emit(Unit)
        }
    }
}
