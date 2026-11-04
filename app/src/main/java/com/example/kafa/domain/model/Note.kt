package com.example.kafa.domain.model

data class Note(
    val id: String,
    val title: String,
    val content: String,
    val createdAt: Long,
    val lastDoneAt: Long? = null,
    val frequency: Frequency = Frequency.WEEKLY
)
