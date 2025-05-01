package com.itanoji.carvision.ui.inspection.create

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.itanoji.carvision.R
import org.koin.androidx.compose.getViewModel
import java.io.File
import java.util.UUID


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateInspectionScreen(
    navController: NavController,
    viewModel: CreateInspectionViewModel = getViewModel()
) {
    val title       by viewModel.title.collectAsState()
    val comment     by viewModel.comment.collectAsState()
    val previewFile by viewModel.previewFile.collectAsState()
    val context = LocalContext.current
    var cameraUri by remember { mutableStateOf<Uri?>(null) }


    val pickLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            // Если из галереи: data.data != null, иначе — это наш cameraUri
            val uri = result.data?.data ?: cameraUri
            uri?.let { viewModel.onImagePicked(it) }
        }
    }

    // Лончер для запроса разрешения CAMERA
    val cameraPermLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            // только после grant — формируем и запускаем chooser
            // Сгенерировать файл и Uri
            val imagesDir = File(context.cacheDir, "images").apply { if (!exists()) mkdirs() }
            val file = File(imagesDir, "${UUID.randomUUID()}.jpg")
            cameraUri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.provider",
                file
            )

            // Intent на галерею
            val pickIntent = Intent(
                Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            ).apply {
                type = "image/*"
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }

            // Intent на камеру
            val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE).apply {
                putExtra(MediaStore.EXTRA_OUTPUT, cameraUri)
                addFlags(
                    Intent.FLAG_GRANT_READ_URI_PERMISSION or
                            Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                )
            }

            // Chooser
            val chooser = Intent.createChooser(pickIntent, "Выберите фото").apply {
                putExtra(Intent.EXTRA_INITIAL_INTENTS, arrayOf(cameraIntent))
                addFlags(
                    Intent.FLAG_GRANT_READ_URI_PERMISSION or
                            Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                )
            }

            pickLauncher.launch(chooser)
        } else {
            Toast.makeText(context, "Без доступа к камере снять фото нельзя", Toast.LENGTH_SHORT).show()
        }
    }

    // Следим за событием создания, чтобы навигировать назад
    LaunchedEffect(Unit) {
        viewModel.created.collect {
            navController.popBackStack()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Новый осмотр") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        bottomBar = {
            Button(
                onClick = { viewModel.createInspection() },
                enabled = title.isNotBlank(),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .height(48.dp),
                shape = RoundedCornerShape(24.dp)
            ) {
                Text("Создать")
            }
        },
        modifier = Modifier
            .navigationBarsPadding()
    ) { padding ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(padding)
                .imePadding()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Превью картинки
            AsyncImage(
                model = previewFile ?: R.drawable.placeholder_car,
                contentDescription = null,
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
                    .align(Alignment.CenterHorizontally),
                contentScale = ContentScale.Crop
            )
            Spacer(Modifier.height(8.dp))
            // Кнопка "Редактировать" запускает галерею
            IconButton(onClick = {
                // Запрашиваем CAMERA permission
                cameraPermLauncher.launch(Manifest.permission.CAMERA)
            }) {
                Icon(Icons.Default.Edit, contentDescription = "Изменить фото")
            }

            Spacer(Modifier.height(24.dp))

            OutlinedTextField(
                value = title,
                onValueChange = viewModel::onTitleChange,
                label = { Text("Название осмотра") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(Modifier.height(16.dp))

            OutlinedTextField(
                value = comment,
                onValueChange = viewModel::onCommentChange,
                label = { Text("Комментарий") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
            )
        }
    }
}