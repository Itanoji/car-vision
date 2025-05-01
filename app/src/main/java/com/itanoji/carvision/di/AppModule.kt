package com.itanoji.carvision.di

import androidx.room.Room
import com.itanoji.carvision.data.local.AppDatabase
import com.itanoji.carvision.data.repository.InspectionRepositoryImpl
import com.itanoji.carvision.data.storage.FileStorageManager
import com.itanoji.carvision.domain.repository.InspectionRepository
import com.itanoji.carvision.ui.inspection.create.CreateInspectionViewModel
import com.itanoji.carvision.ui.inspection.edit.EditInspectionViewModel
import com.itanoji.carvision.ui.inspection.view.InspectionDetailViewModel
import com.itanoji.carvision.ui.inspections.InspectionsListViewModel
import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module
import org.koin.androidx.viewmodel.dsl.viewModel

val appModule = module {
    single {
        Room.databaseBuilder(
            androidApplication(),
            AppDatabase::class.java,
            "carvision.db"
        )
            .fallbackToDestructiveMigration()
            .build()
    }

    single { get<AppDatabase>().inspectionDao() }
    single { get<AppDatabase>().inspectionResultDao() }
    single { get<AppDatabase>().mediaTypeDao() }
    single { get<AppDatabase>().mediaDao() }

    single { FileStorageManager(androidApplication()) }

    single<InspectionRepository> {
        InspectionRepositoryImpl(
            inspectionDao      = get(),
            resultDao          = get(),
            mediaDao           = get()
        )
    }

    viewModel {
        InspectionsListViewModel(
            inspectionRepo = get(),
            fileStorage = get()
        )
    }

    viewModel {
        CreateInspectionViewModel(
            repo = get(),
            fileStorage = get()
        )
    }

    viewModel {
        InspectionDetailViewModel(
            repository = get(),
            fileStorage = get(),
            savedStateHandle = get()
        )
    }

    viewModel {
        EditInspectionViewModel(
            repository = get(),
            fileStorage = get(),
            savedStateHandle = get()
        )
    }
}

