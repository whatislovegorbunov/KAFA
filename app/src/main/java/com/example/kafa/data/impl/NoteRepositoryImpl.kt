package com.example.kafa.data.impl

import com.example.kafa.data.api.NoteRepository
import com.example.kafa.data.local.NoteDao
import com.example.kafa.data.local.toDomain
import com.example.kafa.data.local.toEntity
import com.example.kafa.domain.model.Note
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.UUID
import javax.inject.Inject

class NoteRepositoryImpl @Inject constructor(
    private val noteDao: NoteDao
) : NoteRepository {

    override fun observeNotes(): Flow<List<Note>> =
        noteDao.observeAll().map { entities -> entities.map { it.toDomain() } }

    override suspend fun addNote(title: String, content: String): Result<Unit> = runCatching {
        val note = Note(
            id = UUID.randomUUID().toString(),
            title = title,
            content = content,
            createdAt = System.currentTimeMillis()
        )
        noteDao.insert(note.toEntity())
    }

    override suspend fun deleteNote(id: String): Result<Unit> = runCatching {
        noteDao.deleteById(id)
    }

    override suspend fun markAsDone(id: String): Result<Unit> = runCatching {
        noteDao.updateLastDoneAt(id, System.currentTimeMillis())
    }
}
