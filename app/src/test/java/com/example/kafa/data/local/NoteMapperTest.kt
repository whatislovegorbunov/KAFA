package com.example.kafa.data.local

import com.example.kafa.domain.model.Note
import org.junit.Assert.assertEquals
import org.junit.Test

class NoteMapperTest {

    @Test
    fun toDomain_mapsAllFieldsCorrectly() {
        val entity = NoteEntity("1", "Title", "Content", 1000L)
        val domain = entity.toDomain()
        assertEquals("1", domain.id)
        assertEquals("Title", domain.title)
        assertEquals("Content", domain.content)
        assertEquals(1000L, domain.createdAt)
    }

    @Test
    fun toEntity_mapsAllFieldsCorrectly() {
        val note = Note("2", "My Title", "My Content", 2000L)
        val entity = note.toEntity()
        assertEquals("2", entity.id)
        assertEquals("My Title", entity.title)
        assertEquals("My Content", entity.content)
        assertEquals(2000L, entity.createdAt)
    }

    @Test
    fun toDomain_toEntity_roundTrip_preservesAllFields() {
        val original = Note("3", "Round", "Trip", 3000L)
        val restored = original.toEntity().toDomain()
        assertEquals(original, restored)
    }
}
