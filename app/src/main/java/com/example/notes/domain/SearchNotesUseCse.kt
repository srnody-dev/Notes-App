package com.example.notes.domain

import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SearchNotesUseCse @Inject constructor(private val repository: NotesRepository) {
    operator fun invoke(query:String):Flow<List<Note>>{ // поиск по определенному запросу
        return repository.searchNotes(query)
}
 }