package com.example.kafa.data.local

import com.example.kafa.domain.model.Frequency
import com.example.kafa.domain.model.Note

fun NoteEntity.toDomain(): Note = Note(
    id = id,
    title = title,
    content = content,
    createdAt = createdAt,
    lastDoneAt = lastDoneAt,
    frequency = Frequency.valueOf(frequency)
)

fun Note.toEntity(): NoteEntity = NoteEntity(
    id = id,
    title = title,
    content = content,
    createdAt = createdAt,
    lastDoneAt = lastDoneAt,
    frequency = frequency.name
)
