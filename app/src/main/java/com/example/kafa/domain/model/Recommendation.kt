package com.example.kafa.domain.model

data class Recommendation(
    val note: Note,
    val urgencyScore: Float,
    val overdueDays: Long,
    val message: String
)
