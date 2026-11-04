package com.example.kafa.data.impl

import app.cash.turbine.test
import com.example.kafa.data.local.NoteDao
import com.example.kafa.data.local.NoteEntity
import com.example.kafa.domain.model.Note
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class NoteRepositoryImplTest {

    private val noteDao = mockk<NoteDao>(relaxed = true)
    private val repository = NoteRepositoryImpl(noteDao)

    @Test
    fun observeNotes_initiallyReturnsEmptyList() = runTest {
        every { noteDao.observeAll() } returns flowOf(emptyList())
        repository.observeNotes().test {
            assertEquals(emptyList<Note>(), awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun addNote_callsDaoInsertAndReturnsSuccess() = runTest {
        coEvery { noteDao.insert(any()) } just Runs
        val result = repository.addNote("Title", "Content")
        assertTrue(result.isSuccess)
        coVerify(exactly = 1) { noteDao.insert(any()) }
    }

    @Test
    fun deleteNote_callsDaoDeleteByIdAndReturnsSuccess() = runTest {
        coEvery { noteDao.deleteById(any()) } just Runs
        val result = repository.deleteNote("note-id")
        assertTrue(result.isSuccess)
        coVerify(exactly = 1) { noteDao.deleteById("note-id") }
    }

    @Test
    fun observeNotes_mapsEntityToDomainCorrectly() = runTest {
        val entity = NoteEntity("1", "Title", "Content", 1000L)
        every { noteDao.observeAll() } returns flowOf(listOf(entity))
        repository.observeNotes().test {
            val notes = awaitItem()
            assertEquals("1", notes[0].id)
            assertEquals("Title", notes[0].title)
            assertEquals("Content", notes[0].content)
            assertEquals(1000L, notes[0].createdAt)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun addNote_generatesUniqueIdsForDifferentNotes() = runTest {
        val dao = FakeNoteDao()
        val repo = NoteRepositoryImpl(dao)

        repo.addNote("First", "Content 1")
        repo.addNote("Second", "Content 2")

        val notes = repo.observeNotes().first()
        assertEquals(2, notes.size)
        assertNotEquals(notes[0].id, notes[1].id)
    }

    @Test
    fun addNote_correctlySavesTitleAndContentFields() = runTest {
        val dao = FakeNoteDao()
        val repo = NoteRepositoryImpl(dao)

        repo.addNote("My Title", "My Content")

        val notes = repo.observeNotes().first()
        assertEquals("My Title", notes[0].title)
        assertEquals("My Content", notes[0].content)
    }

    @Test
    fun deleteNote_withSpecificId_passesIdToDao() = runTest {
        coEvery { noteDao.deleteById(any()) } just Runs

        repository.deleteNote("specific-id")

        coVerify(exactly = 1) { noteDao.deleteById("specific-id") }
    }

    @Test
    fun addNote_whenDaoThrows_returnsFailure() = runTest {
        coEvery { noteDao.insert(any()) } throws RuntimeException("DB error")

        val result = repository.addNote("Title", "Content")

        assertTrue(result.isFailure)
        assertEquals("DB error", result.exceptionOrNull()?.message)
    }

    @Test
    fun deleteNote_whenDaoThrows_returnsFailure() = runTest {
        coEvery { noteDao.deleteById(any()) } throws RuntimeException("DB error")

        val result = repository.deleteNote("note-id")

        assertTrue(result.isFailure)
        assertEquals("DB error", result.exceptionOrNull()?.message)
    }

    @Test
    fun markAsDone_callsDaoUpdateLastDoneAtAndReturnsSuccess() = runTest {
        coEvery { noteDao.updateLastDoneAt(any(), any()) } just Runs
        val result = repository.markAsDone("note-id")
        assertTrue(result.isSuccess)
        coVerify(exactly = 1) { noteDao.updateLastDoneAt("note-id", any()) }
    }

    @Test
    fun markAsDone_whenDaoThrows_returnsFailure() = runTest {
        coEvery { noteDao.updateLastDoneAt(any(), any()) } throws RuntimeException("DB error")
        val result = repository.markAsDone("note-id")
        assertTrue(result.isFailure)
        assertEquals("DB error", result.exceptionOrNull()?.message)
    }

    @Test
    fun addNote_thenObserveNotes_emitsInsertedNote() = runTest {
        val dao = FakeNoteDao()
        val repo = NoteRepositoryImpl(dao)

        repo.addNote("Title", "Content")

        repo.observeNotes().test {
            val notes = awaitItem()
            assertEquals(1, notes.size)
            assertEquals("Title", notes[0].title)
            assertEquals("Content", notes[0].content)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun deleteNote_thenObserveNotes_emitsEmptyList() = runTest {
        val dao = FakeNoteDao()
        val repo = NoteRepositoryImpl(dao)

        repo.addNote("Title", "Content")
        val noteId = repo.observeNotes().first()[0].id
        repo.deleteNote(noteId)

        repo.observeNotes().test {
            assertTrue(awaitItem().isEmpty())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun markAsDone_thenObserveNotes_emitsNoteWithLastDoneAt() = runTest {
        val dao = FakeNoteDao()
        val repo = NoteRepositoryImpl(dao)

        repo.addNote("Title", "Content")
        val noteId = repo.observeNotes().first()[0].id
        repo.markAsDone(noteId)

        repo.observeNotes().test {
            val notes = awaitItem()
            assertEquals(1, notes.size)
            assertNotNull(notes[0].lastDoneAt)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun observeNotes_emptyDb_returnsEmptyList() = runTest {
        val dao = FakeNoteDao()
        val repo = NoteRepositoryImpl(dao)

        repo.observeNotes().test {
            assertTrue(awaitItem().isEmpty())
            cancelAndIgnoreRemainingEvents()
        }
    }

    private class FakeNoteDao : NoteDao {
        private val notes = MutableStateFlow<List<NoteEntity>>(emptyList())

        override fun observeAll(): Flow<List<NoteEntity>> = notes

        override suspend fun insert(entity: NoteEntity) {
            notes.value = notes.value + entity
        }

        override suspend fun deleteById(id: String) {
            notes.value = notes.value.filter { it.id != id }
        }

        override suspend fun updateLastDoneAt(id: String, timestamp: Long) {
            notes.value = notes.value.map {
                if (it.id == id) it.copy(lastDoneAt = timestamp) else it
            }
        }
    }
}
