package com.itanoji.carvision.data.storage

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.core.content.FileProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.nio.file.Files.exists
import kotlin.math.max
import kotlin.math.roundToInt
import androidx.core.graphics.scale

class FileStorageManager(private val context: Context) {

    /**
     * Корневая папка для кортинок
     */
    private val imagesDir: File by lazy {
        File(context.filesDir, "images")
            .apply { if (!exists()) mkdirs() }
    }

    /**
     * Сохраняет Bitmap в файл в imagesDir/relativePath, возвращает этот относительный путь.
     */
    suspend fun saveImage(
        relativePath: String,
        bitmap: Bitmap,
        format: Bitmap.CompressFormat = Bitmap.CompressFormat.JPEG,
        quality: Int = 85
    ): String = withContext(Dispatchers.IO) {
        val outFile = File(imagesDir, relativePath)
        // создаём вложенные папки, если их нет
        outFile.parentFile?.takeIf { !it.exists() }?.mkdirs()
        FileOutputStream(outFile).use { fos ->
            bitmap.compress(format, quality, fos)
        }
        relativePath
    }

    /** Возвращает File по относительному пути или null, если не найден. */
    fun getImageFile(relativePath: String?): File? {
        if (relativePath.isNullOrBlank()) return null
        val f = File(imagesDir, relativePath)
        return f.takeIf { it.exists() }
    }

    /** Удаляет файл, если он существует. */
    suspend fun deleteImage(relativePath: String) = withContext(Dispatchers.IO) {
        getImageFile(relativePath)?.delete()
    }

    /**
     * Загружает Bitmap из переданного галерейного Uri, масштабируя так,
     * чтобы ни ширина, ни высота не превышали maxSize px.
     *
     * @param uri     — content:// URI изображения
     * @param maxSize — максимальный размер стороны (width или height)
     * @throws IOException если не удалось прочитать поток или декодировать Bitmap
     */
    suspend fun loadBitmapFromUri(
        uri: Uri,
        maxSize: Int = 1024
    ): Bitmap = withContext(Dispatchers.IO) {
        val resolver = context.contentResolver

        // Сначала читаем только bounds, чтобы узнать исходные размеры
        val optsBounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
        resolver.openInputStream(uri).use { input ->
            BitmapFactory.decodeStream(input, null, optsBounds)
        }
        val (origW, origH) = optsBounds.outWidth to optsBounds.outHeight

        // Рассчитываем inSampleSize (степень деления сторон)
        var inSampleSize = 1
        val maxOrig = max(origW, origH)
        while (maxOrig / inSampleSize > maxSize) {
            inSampleSize *= 2
        }


        // Читаем уже реальный Bitmap с учётом inSampleSize
        val optsDecode = BitmapFactory.Options().apply {
            inJustDecodeBounds = false
            this.inSampleSize = inSampleSize
        }
        val bitmap = resolver.openInputStream(uri).use { input ->
            BitmapFactory.decodeStream(input, null, optsDecode)
        } ?: throw java.io.IOException("Cannot decode bitmap from URI: $uri")

        // Если после subsampling размер всё ещё великоват, подмасштабируем точечно
        val scale = maxSize.toFloat() / max(bitmap.width, bitmap.height)
        if (scale < 1f) {
            val newW = (bitmap.width * scale).roundToInt()
            val newH = (bitmap.height * scale).roundToInt()
            bitmap.scale(newW, newH)
        } else {
            bitmap
        }
    }
}