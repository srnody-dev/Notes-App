package com.example.notes.data

import android.os.SystemClock
import com.example.notes.domain.Note
import com.example.notes.domain.NotesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update

object TestNotesRepositoryImpl : NotesRepository {

    private val testData = mutableListOf<Note>().apply { repeat(10){
        add(Note(it, title = "$it", content = "$it",System.currentTimeMillis(),false))
    } }

    private val notesListFlow = MutableStateFlow<List<Note>>(testData)


    override suspend fun addNote(title: String, content: String, isPinned: Boolean, updatedAt: Long) {
        notesListFlow.update { oldList ->

            val note = Note(
                id = oldList.size,
                content = content,
                title = title,
                updatedAt = updatedAt,
                isPin = isPinned
            )

            oldList.toMutableList().apply {
                add(note)
            }
        }
    }

    override suspend fun deleteNote(noteId: Int) {
        notesListFlow.update { oldList ->
            oldList.toMutableList().apply {
                removeIf { it.id == noteId }
            }
        }
    }

    override suspend fun editNote(note: Note) {
        notesListFlow.update { oldList ->
            oldList.map {
                if (it.id == note.id) {
                    note
                } else {
                    it
                }
            }
        }
    }


    override fun getAllNotes(): Flow<List<Note>> {
        return notesListFlow.asStateFlow()
    }

    override suspend fun getNote(noteId: Int): Note {
        return notesListFlow.value.first {
            it.id == noteId
        }
    }

    override fun searchNotes(query: String): Flow<List<Note>> {
        return notesListFlow.map { currentList ->
            currentList.filter {
                it.title.contains(query) or it.content.contains(query)
            }
        }
    }

    override suspend fun switchPinnedStatus(noteId: Int) {
        notesListFlow.update { oldList ->
            oldList.map {
                if (it.id == noteId) {
                    it.copy(isPin = !it.isPin)
                } else {
                    it
                }
            }
        }
    }
}