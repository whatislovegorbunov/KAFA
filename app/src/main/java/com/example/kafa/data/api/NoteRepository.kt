package com.example.kafa.data.api

import com.example.kafa.domain.model.Note
import kotlinx.coroutines.flow.Flow

interface NoteRepository {
    fun observeNotes(): Flow<List<Note>>
    suspend fun addNote(title: String, content: String): Result<Unit>
    suspend fun deleteNote(id: String): Result<Unit>
    suspend fun markAsDone(id: String): Result<Unit>
}
