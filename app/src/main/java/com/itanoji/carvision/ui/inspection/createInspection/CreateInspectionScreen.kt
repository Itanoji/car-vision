package com.itanoji.carvision.ui.inspection.createInspection

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.itanoji.carvision.R
import com.itanoji.carvision.ui.theme.BlueOnPrimary
import com.itanoji.carvision.ui.theme.BluePrimary
import org.koin.androidx.compose.getViewModel
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
    val tempCameraUri by viewModel.tempCameraUri.collectAsState()


    // Один лончер для обоих — галереи и камеры
    val pickLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            // либо data.data для галереи, либо tempCameraUri для камеры
            val uri = result.data?.data ?: tempCameraUri
            uri?.let { viewModel.onImagePicked(it) }
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
            .imePadding()
            .fillMaxSize()
    ) { padding ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(padding)
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
            )
            Spacer(Modifier.height(8.dp))
            // Кнопка "Редактировать" запускает галерею
            IconButton(onClick = {
                // 1) Intent галереи
                val pickIntent = Intent(
                    Intent.ACTION_PICK,
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                ).apply {
                    type = "image/*"
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                }

                // 2) Готовим URI для камеры в VM
                viewModel.prepareCameraUri()

                // 3) Intent камеры
                val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE).apply {
                    // Ждем, пока URI будет готов
                    tempCameraUri?.let { uri ->
                        putExtra(MediaStore.EXTRA_OUTPUT, uri)
                        addFlags(
                            Intent.FLAG_GRANT_READ_URI_PERMISSION or
                            Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                        )
                    }
                }

                // 4) Собираем chooser только если URI камеры готов
                val chooser = if (tempCameraUri != null) {
                    Intent.createChooser(pickIntent, "Выберите фото").apply {
                        putExtra(Intent.EXTRA_INITIAL_INTENTS, arrayOf(cameraIntent))
                        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
                    }
                } else {
                    pickIntent
                }

                // 5) Ланчаем
                pickLauncher.launch(chooser)
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