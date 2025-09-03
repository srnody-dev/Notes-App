package com.example.notes.domain

import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAllNotesUseCase @Inject constructor(private val repository: NotesRepository){

    /*fun getAllNotes():Flow<List<Note>>{
    }*/
    operator fun invoke(): Flow<List<Note>> { // таким способом мы избегаем дублирования одиннаковых названий
        // тк по своей сути useCase это и есть метод для одного действия пользователя
        return repository.getAllNotes()
    }
}