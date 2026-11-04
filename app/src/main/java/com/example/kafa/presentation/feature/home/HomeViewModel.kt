package com.example.kafa.presentation.feature.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kafa.data.api.NoteRepository
import com.example.kafa.domain.usecase.GetRecommendationsUseCase
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

class HomeViewModel @Inject constructor(
    private val noteRepository: NoteRepository,
    getRecommendationsUseCase: GetRecommendationsUseCase
) : ViewModel() {

    val uiState: StateFlow<HomeUiState> = combine(
        noteRepository.observeNotes(),
        getRecommendationsUseCase()
    ) { notes, recommendations ->
        HomeUiState(
            notes = notes,
            recommendations = recommendations,
            isLoading = false
        )
    }.catch { throwable ->
        emit(HomeUiState(isLoading = false, error = throwable.message))
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = HomeUiState()
    )

    fun deleteNote(id: String) {
        viewModelScope.launch {
            noteRepository.deleteNote(id)
                .onFailure { throwable ->
                    _deleteError.emit(throwable.message ?: "Unknown error")
                }
        }
    }

    fun markAsDone(id: String) {
        viewModelScope.launch {
            noteRepository.markAsDone(id)
                .onFailure { throwable ->
                    _deleteError.emit(throwable.message ?: "Unknown error")
                }
        }
    }

    private val _deleteError = MutableSharedFlow<String>()
    val deleteError: SharedFlow<String> = _deleteError.asSharedFlow()
}
