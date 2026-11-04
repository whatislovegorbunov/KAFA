package com.example.kafa.di

import android.content.Context
import androidx.room.Room
import com.example.kafa.data.local.NoteDao
import com.example.kafa.data.local.NoteDatabase
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
object DataModule {

    @Provides
    @Singleton
    fun provideNoteDatabase(context: Context): NoteDatabase =
        Room.databaseBuilder(context, NoteDatabase::class.java, "notes.db")
            .fallbackToDestructiveMigration()
            .build()

    @Provides
    @Singleton
    fun provideNoteDao(database: NoteDatabase): NoteDao = database.noteDao()
}
