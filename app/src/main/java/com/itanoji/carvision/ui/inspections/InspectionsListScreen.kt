package com.itanoji.carvision.ui.inspections

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.layout.Column
import androidx.compose.ui.unit.dp
import com.itanoji.carvision.domain.model.Inspection
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Card
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.ui.Alignment
import coil.compose.AsyncImage
import com.itanoji.carvision.R
import org.koin.androidx.compose.getViewModel
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InspectionsListScreen(
    onNavigateToDetail: (Long) -> Unit,
    onCreateInspection: () -> Unit,
    viewModel: InspectionsListViewModel = getViewModel()
) {
    val inspections = viewModel.inspections.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Список осмотров") },
                navigationIcon = {
                    IconButton(onClick = { /* открыть drawer */ }) {
                        Icon(Icons.Default.Menu, contentDescription = "Menu")
                    }
                },
                actions = {
                    IconButton(onClick = { /* профиль */ }) {
                        Icon(Icons.Default.Person, contentDescription = "Profile")
                    }
                }
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                content = { Text("Создать осмотр") },
                onClick = onCreateInspection,
                modifier = Modifier.padding(16.dp)
            )
        },
        containerColor = MaterialTheme.colorScheme.background,
        modifier = Modifier
            .navigationBarsPadding()
            .imePadding()
            .fillMaxSize()
    ) { padding ->
        LazyColumn(
            contentPadding = padding,
            modifier = Modifier.fillMaxSize()
        ) {
            items(inspections.value, key = { it.id }) { insp ->
                InspectionCard(
                    inspection = insp,
                    viewModel = viewModel,
                    onClick = { onNavigateToDetail(insp.id) },
                    onDelete = { viewModel.deleteInspection(insp) }
                )
            }
        }
    }
}

@Composable
fun InspectionCard(
    inspection: Inspection,
    viewModel: InspectionsListViewModel,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {

    val avatarFile by produceState<File?>(initialValue = null, inspection.avatarMediaId) {
        val id = inspection.avatarMediaId
        if (id != null) {
            // вызываем suspend-метод VM
            value = viewModel.getAvatarFile(id)
        }
    }
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = avatarFile ?: R.drawable.placeholder_car,
                contentDescription = null,
                modifier = Modifier
                    .size(56.dp)
                    .clip(MaterialTheme.shapes.small)
            )
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = inspection.title,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1
                )
                Spacer(Modifier.height(4.dp))
                inspection.description?.let {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.bodyMedium,
                        maxLines = 2
                    )
                }
            }
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, contentDescription = "Delete")
            }
        }
    }
}