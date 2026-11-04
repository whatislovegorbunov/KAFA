package com.example.kafa.di

import androidx.lifecycle.ViewModel
import com.example.kafa.presentation.feature.addnote.AddNoteViewModel
import com.example.kafa.presentation.feature.home.HomeViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class ViewModelModule {

    @Binds
    @IntoMap
    @ViewModelKey(HomeViewModel::class)
    abstract fun bindHomeViewModel(viewModel: HomeViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(AddNoteViewModel::class)
    abstract fun bindAddNoteViewModel(viewModel: AddNoteViewModel): ViewModel
}
