package com.example.kafa.di

import com.example.kafa.data.api.NoteRepository
import com.example.kafa.data.impl.NoteRepositoryImpl
import dagger.Binds
import dagger.Module
import javax.inject.Singleton

@Module
abstract class AppModule {

    @Binds
    @Singleton
    abstract fun bindNoteRepository(impl: NoteRepositoryImpl): NoteRepository
}
