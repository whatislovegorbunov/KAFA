package com.example.kafa.presentation.feature.home

import com.example.kafa.domain.model.Note
import com.example.kafa.domain.model.Recommendation

data class HomeUiState(
    val notes: List<Note> = emptyList(),
    val recommendations: List<Recommendation> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null
)
