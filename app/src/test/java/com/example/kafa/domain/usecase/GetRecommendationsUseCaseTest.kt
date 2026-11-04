package com.example.kafa.domain.usecase

import app.cash.turbine.test
import com.example.kafa.data.api.NoteRepository
import com.example.kafa.domain.model.Frequency
import com.example.kafa.domain.model.Note
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkStatic
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class GetRecommendationsUseCaseTest {

    private val repository = mockk<NoteRepository>()
    private val useCase = GetRecommendationsUseCase(repository)

    private val fixedNow = 1_000_000_000_000L
    private val day = 86_400_000L

    @Before
    fun setup() {
        mockkStatic(System::class)
        every { System.currentTimeMillis() } returns fixedNow
    }

    @After
    fun tearDown() {
        unmockkStatic(System::class)
    }

    private fun note(
        id: String = "1",
        title: String = "Title",
        content: String = "Content",
        createdAt: Long = fixedNow,
        lastDoneAt: Long? = null,
        frequency: Frequency = Frequency.WEEKLY
    ) = Note(id, title, content, createdAt, lastDoneAt, frequency)

    @Test
    fun emptyNotesList_returnsEmptyRecommendations() = runTest {
        every { repository.observeNotes() } returns flowOf(emptyList())
        useCase().test {
            assertTrue(awaitItem().isEmpty())
            awaitComplete()
        }
    }

    @Test
    fun allNotesInSchedule_returnsEmptyRecommendations() = runTest {
        val notes = listOf(
            note(id = "1", lastDoneAt = fixedNow, frequency = Frequency.DAILY),
            note(id = "2", lastDoneAt = fixedNow - 3 * day, frequency = Frequency.MONTHLY)
        )
        every { repository.observeNotes() } returns flowOf(notes)
        useCase().test {
            assertTrue(awaitItem().isEmpty())
            awaitComplete()
        }
    }

    @Test
    fun oneOverdueNote_returnsSingleRecommendation() = runTest {
        val notes = listOf(
            note(id = "1", lastDoneAt = fixedNow - 10 * day, frequency = Frequency.WEEKLY)
        )
        every { repository.observeNotes() } returns flowOf(notes)
        useCase().test {
            val result = awaitItem()
            assertEquals(1, result.size)
            assertTrue(result[0].urgencyScore >= 1.0f)
            assertEquals("1", result[0].note.id)
            awaitComplete()
        }
    }

    @Test
    fun neverDoneNote_returnsRecommendationWithNeverDoneMessage() = runTest {
        val notes = listOf(
            note(id = "1", lastDoneAt = null, createdAt = fixedNow - 10 * day, frequency = Frequency.WEEKLY)
        )
        every { repository.observeNotes() } returns flowOf(notes)
        useCase().test {
            val result = awaitItem()
            assertEquals(1, result.size)
            assertEquals("You haven't done this yet", result[0].message)
            awaitComplete()
        }
    }

    @Test
    fun multipleOverdueNotes_returnsSortedByUrgencyScoreDescending() = runTest {
        val notes = listOf(
            note(id = "daily", lastDoneAt = fixedNow - 10 * day, frequency = Frequency.DAILY),
            note(id = "weekly", lastDoneAt = fixedNow - 10 * day, frequency = Frequency.WEEKLY),
            note(id = "monthly", lastDoneAt = fixedNow - 30 * day, frequency = Frequency.MONTHLY)
        )
        every { repository.observeNotes() } returns flowOf(notes)
        useCase().test {
            val result = awaitItem()
            assertEquals(3, result.size)
            assertTrue(result[0].urgencyScore >= result[1].urgencyScore)
            assertTrue(result[1].urgencyScore >= result[2].urgencyScore)
            assertEquals("daily", result[0].note.id)
            assertEquals("weekly", result[1].note.id)
            assertEquals("monthly", result[2].note.id)
            awaitComplete()
        }
    }

    @Test
    fun sevenOverdueNotes_returnsAtMostFiveRecommendations() = runTest {
        val notes = (1..7).map { i ->
            note(id = "$i", lastDoneAt = fixedNow - 100 * day, frequency = Frequency.DAILY)
        }
        every { repository.observeNotes() } returns flowOf(notes)
        useCase().test {
            val result = awaitItem()
            assertEquals(5, result.size)
            awaitComplete()
        }
    }

    @Test
    fun differentFrequencies_dailyOverdueRanksHigherThanYearly() = runTest {
        val notes = listOf(
            note(id = "daily", lastDoneAt = fixedNow - 2 * day, frequency = Frequency.DAILY),
            note(id = "yearly", lastDoneAt = fixedNow - 400 * day, frequency = Frequency.YEARLY)
        )
        every { repository.observeNotes() } returns flowOf(notes)
        useCase().test {
            val result = awaitItem()
            assertEquals(2, result.size)
            assertEquals("daily", result[0].note.id)
            assertEquals(2.0f, result[0].urgencyScore, 0.001f)
            assertEquals("yearly", result[1].note.id)
            assertEquals(400f / 365f, result[1].urgencyScore, 0.001f)
            awaitComplete()
        }
    }
}
