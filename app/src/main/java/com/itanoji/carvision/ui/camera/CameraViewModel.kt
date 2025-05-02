package com.itanoji.carvision.ui.camera

import android.util.Log
import androidx.camera.core.ImageAnalysis
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.itanoji.carvision.data.storage.FileStorageManager
import com.itanoji.carvision.domain.analyzer.FrameProcessor
import com.itanoji.carvision.domain.model.InspectionResult
import com.itanoji.carvision.domain.repository.InspectionRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.util.Date

class CameraViewModel(
    private val repository: InspectionRepository,
    private val fileStorage: FileStorageManager,
    savedStateHandle : SavedStateHandle
) : ViewModel() {
    private val inspectionId: Long = checkNotNull(savedStateHandle["id"])

    private val processor = FrameProcessor()

    val analyzer = ImageAnalysis.Analyzer { imageProxy ->
        processor.process(imageProxy)
        imageProxy.close()
    }

    private val _uiState = MutableStateFlow(CameraUiState())
    val uiState: StateFlow<CameraUiState> = _uiState.asStateFlow()

    private var timerJob: Job? = null
    private var processingJob: Job? = null
    private var startTime = 0L

    fun startRecording() {
        startTime = System.currentTimeMillis()
        _uiState.update { it.copy(isRecording = true) }

        timerJob = viewModelScope.launch {
            while (isActive) {
                val elapsed = (System.currentTimeMillis() - startTime) / 1000
                _uiState.update { it.copy(recordTime = formatSeconds(elapsed)) }
                delay(1000)
            }
        }
    }

    fun stopRecording() {
        _uiState.update { it.copy(isRecording = false) }
        timerJob?.cancel()
        processingJob?.cancel()
    }

    fun startProcessing() {
        processingJob = viewModelScope.launch {
            while (isActive) {
                delay(500)
                processFramePlaceholder()
            }
        }
    }

    fun stopProcessing() {
        processingJob?.cancel()
    }

    private fun processFramePlaceholder() {
        // Тут потом подключишь нейросеть
        Log.d("CameraVM", "Обработка кадра…")
    }

    private fun formatSeconds(seconds: Long): String {
        val m = seconds / 60
        val s = seconds % 60
        return "%02d:%02d".format(m, s)
    }

    fun finishInspection(onResultReady: (Long) -> Unit) {
        viewModelScope.launch {
            val result = InspectionResult(
                id = 0L,
                inspectionId = inspectionId,
                licensePlate = "В777ОР77",
                color = "негр",
                brand = "BMW",
                date = Date(),
                timeSeconds = 42
            )

            val savedResultId = repository.saveInspectionResult(result)
            val insp = repository.getInspectionById(inspectionId).first()!!.copy(
                inspectionResultId = savedResultId
            )
            repository.updateInspection(insp)

            onResultReady(savedResultId)
        }
    }


    private suspend fun saveInspectionResultToDb(): Long {
        // Пока просто заглушка
        // В будущем сюда передаёшь кэшированные данные из Orchestrator
        delay(200) // имитация работы с БД
        return 0L // допустим, ID — временный timestamp
    }
}

data class CameraUiState(
    val isRecording: Boolean = false,
    val recordTime: String = "00:00"
)

