package com.stellarworker.geonote.di

import com.stellarworker.geonote.repository.LocalRepository
import com.stellarworker.geonote.repository.LocalRepositoryImpl
import com.stellarworker.geonote.ui.maps.MapsFragmentViewModel
import com.stellarworker.geonote.ui.markers.MarkersFragmentViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val mainKoinModule = module {
    single<LocalRepository> { LocalRepositoryImpl() }
    viewModel { MapsFragmentViewModel(get(), get()) }
    viewModel { MarkersFragmentViewModel(get()) }
}