package com.example.kafa.domain.usecase

import com.example.kafa.data.api.NoteRepository
import com.example.kafa.domain.model.Recommendation
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import kotlin.math.max

class GetRecommendationsUseCase @Inject constructor(
    private val noteRepository: NoteRepository
) {
    operator fun invoke(): Flow<List<Recommendation>> =
        noteRepository.observeNotes().map { notes ->
            val now = System.currentTimeMillis()
            val millisPerDay = 86_400_000L

            notes
                .map { note ->
                    val lastDone = note.lastDoneAt ?: note.createdAt
                    val overdueDays = max(0L, (now - lastDone) / millisPerDay)
                    val expectedDays = note.frequency.days.toLong()
                    val urgencyScore = if (expectedDays == 0L) 0f else overdueDays.toFloat() / expectedDays.toFloat()
                    val message = if (note.lastDoneAt == null) {
                        "You haven't done this yet"
                    } else {
                        "You haven't done this in $overdueDays days"
                    }
                    Recommendation(note, urgencyScore, overdueDays, message)
                }
                .filter { it.urgencyScore >= 1.0f }
                .sortedByDescending { it.urgencyScore }
                .take(MAX_RECOMMENDATIONS)
        }

    private companion object {
        const val MAX_RECOMMENDATIONS = 5
    }
}
