package com.example.kafa.presentation.feature.home

import app.cash.turbine.test
import com.example.kafa.domain.model.Note
import com.example.kafa.data.api.NoteRepository
import com.example.kafa.domain.usecase.GetRecommendationsUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private val repository = mockk<NoteRepository>()
    private val getRecommendationsUseCase = mockk<GetRecommendationsUseCase>()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun createViewModel(): HomeViewModel {
        every { getRecommendationsUseCase() } returns flowOf(emptyList())
        return HomeViewModel(repository, getRecommendationsUseCase)
    }

    @Test
    fun initialState_isLoadingWithEmptyNotes() = runTest(testDispatcher) {
        every { repository.observeNotes() } returns flowOf(emptyList())
        every { getRecommendationsUseCase() } returns flowOf(emptyList())
        val viewModel = HomeViewModel(repository, getRecommendationsUseCase)

        viewModel.uiState.test {
            val initial = awaitItem()
            assertTrue(initial.isLoading)
            assertTrue(initial.notes.isEmpty())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun loadNotes_whenRepositoryReturnsData_emitsSuccessStateWithNotes() = runTest(testDispatcher) {
        val notes = listOf(
            Note("1", "Title 1", "Content 1", 1000L),
            Note("2", "Title 2", "Content 2", 2000L)
        )
        every { repository.observeNotes() } returns flowOf(notes)
        every { getRecommendationsUseCase() } returns flowOf(emptyList())
        val viewModel = HomeViewModel(repository, getRecommendationsUseCase)

        viewModel.uiState.test {
            assertTrue(awaitItem().isLoading)
            advanceUntilIdle()
            val loaded = awaitItem()
            assertFalse(loaded.isLoading)
            assertEquals(notes, loaded.notes)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun loadNotes_whenRepositoryReturnsEmpty_emitsSuccessStateWithEmptyList() = runTest(testDispatcher) {
        every { repository.observeNotes() } returns flowOf(emptyList())
        every { getRecommendationsUseCase() } returns flowOf(emptyList())
        val viewModel = HomeViewModel(repository, getRecommendationsUseCase)

        viewModel.uiState.test {
            assertTrue(awaitItem().isLoading)
            advanceUntilIdle()
            val loaded = awaitItem()
            assertFalse(loaded.isLoading)
            assertTrue(loaded.notes.isEmpty())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun deleteNote_callsRepositoryDeleteNote() = runTest(testDispatcher) {
        coEvery { repository.deleteNote(any()) } returns Result.success(Unit)
        every { repository.observeNotes() } returns flowOf(emptyList())
        every { getRecommendationsUseCase() } returns flowOf(emptyList())
        val viewModel = HomeViewModel(repository, getRecommendationsUseCase)
        viewModel.deleteNote("note-id")
        advanceUntilIdle()
        coVerify(exactly = 1) { repository.deleteNote("note-id") }
    }

    @Test
    fun observeNotes_whenFlowEmitsError_setsErrorInUiState() = runTest(testDispatcher) {
        every { repository.observeNotes() } returns flow { throw RuntimeException("DB error") }
        every { getRecommendationsUseCase() } returns flowOf(emptyList())
        val viewModel = HomeViewModel(repository, getRecommendationsUseCase)

        viewModel.uiState.test {
            assertTrue(awaitItem().isLoading)
            advanceUntilIdle()
            val errorState = awaitItem()
            assertFalse(errorState.isLoading)
            assertNotNull(errorState.error)
            assertEquals("DB error", errorState.error)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun deleteNote_whenRepositoryFails_emitsDeleteError() = runTest(testDispatcher) {
        coEvery { repository.deleteNote(any()) } returns Result.failure(RuntimeException("Delete failed"))
        every { repository.observeNotes() } returns flowOf(emptyList())
        every { getRecommendationsUseCase() } returns flowOf(emptyList())
        val viewModel = HomeViewModel(repository, getRecommendationsUseCase)

        viewModel.deleteError.test {
            viewModel.deleteNote("note-id")
            advanceUntilIdle()
            val error = awaitItem()
            assertEquals("Delete failed", error)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun markAsDone_callsRepositoryMarkAsDone() = runTest(testDispatcher) {
        coEvery { repository.markAsDone(any()) } returns Result.success(Unit)
        every { repository.observeNotes() } returns flowOf(emptyList())
        every { getRecommendationsUseCase() } returns flowOf(emptyList())
        val viewModel = HomeViewModel(repository, getRecommendationsUseCase)
        viewModel.markAsDone("note-id")
        advanceUntilIdle()
        coVerify(exactly = 1) { repository.markAsDone("note-id") }
    }
}
