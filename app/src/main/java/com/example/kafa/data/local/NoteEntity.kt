package com.example.kafa.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "notes")
data class NoteEntity(
    @PrimaryKey val id: String,
    val title: String,
    val content: String,
    val createdAt: Long,
    val lastDoneAt: Long? = null,
    val frequency: String = "WEEKLY"
)
