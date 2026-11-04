package com.example.kafa.presentation.feature.addnote

import app.cash.turbine.test
import com.example.kafa.data.api.NoteRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class AddNoteViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private val noteRepository = mockk<NoteRepository>()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun initialState_hasEmptyFieldsAndNotSaving() = runTest(testDispatcher) {
        val viewModel = AddNoteViewModel(noteRepository)

        val state = viewModel.uiState.value
        assertEquals("", state.title)
        assertEquals("", state.content)
        assertFalse(state.isSaving)
        assertFalse(state.isSaved)
        assertNull(state.error)
    }

    @Test
    fun onTitleChange_updatesTitleInState() = runTest(testDispatcher) {
        val viewModel = AddNoteViewModel(noteRepository)

        viewModel.uiState.test {
            assertEquals("", awaitItem().title)
            viewModel.onTitleChange("New Title")
            assertEquals("New Title", awaitItem().title)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun onContentChange_updatesContentInState() = runTest(testDispatcher) {
        val viewModel = AddNoteViewModel(noteRepository)

        viewModel.uiState.test {
            assertEquals("", awaitItem().content)
            viewModel.onContentChange("New Content")
            assertEquals("New Content", awaitItem().content)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun saveNote_withNonBlankTitle_callsRepositoryAndSetsIsSaved() = runTest(testDispatcher) {
        coEvery { noteRepository.addNote(any(), any()) } returns Result.success(Unit)
        val viewModel = AddNoteViewModel(noteRepository)
        viewModel.onTitleChange("Test Title")
        viewModel.onContentChange("Test Content")

        viewModel.saveNote()
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertFalse(state.isSaving)
        assertTrue(state.isSaved)
        assertNull(state.error)

        coVerify(exactly = 1) { noteRepository.addNote("Test Title", "Test Content") }
    }

    @Test
    fun saveNote_withBlankTitle_doesNotCallRepository() = runTest(testDispatcher) {
        val viewModel = AddNoteViewModel(noteRepository)

        viewModel.saveNote()
        advanceUntilIdle()

        coVerify(exactly = 0) { noteRepository.addNote(any(), any()) }
    }

    @Test
    fun saveNote_whenRepositoryFails_setsErrorAndDoesNotSetSaved() = runTest(testDispatcher) {
        val errorMessage = "Save failed"
        coEvery { noteRepository.addNote(any(), any()) } returns Result.failure(RuntimeException(errorMessage))
        val viewModel = AddNoteViewModel(noteRepository)
        viewModel.onTitleChange("Test")

        viewModel.saveNote()
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertFalse(state.isSaving)
        assertFalse(state.isSaved)
        assertEquals(errorMessage, state.error)
    }

    @Test
    fun saveNote_setsIsSavingTrueDuringExecution_andFalseAfter() = runTest(testDispatcher) {
        val latch = CompletableDeferred<Unit>()
        coEvery { noteRepository.addNote(any(), any()) } coAnswers {
            latch.await()
            Result.success(Unit)
        }
        val viewModel = AddNoteViewModel(noteRepository)
        viewModel.onTitleChange("Test")

        viewModel.uiState.test {
            awaitItem()
            viewModel.saveNote()
            advanceUntilIdle()

            val savingState = awaitItem()
            assertTrue(savingState.isSaving)
            assertFalse(savingState.isSaved)

            latch.complete(Unit)
            advanceUntilIdle()

            val savedState = awaitItem()
            assertFalse(savedState.isSaving)
            assertTrue(savedState.isSaved)

            cancelAndIgnoreRemainingEvents()
        }
    }
}
