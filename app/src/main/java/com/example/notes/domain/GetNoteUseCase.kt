package com.example.notes.domain

import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetNoteUseCase @Inject constructor(private val repository: NotesRepository) { //получение заметки по айди
    /*suspend operator fun invoke(noteId:Int):Note { //invoke(noteId:Int):Note
        return repository.getNote(noteId)
    } */



    operator fun invoke(noteId: Int): Flow<Note?> { //invoke(noteId:Int):Note
        return repository.getNote(noteId)
    }
}