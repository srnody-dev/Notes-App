package com.example.notes.domain

import kotlinx.coroutines.flow.Flow

interface NotesRepository { //c интерфейсом работают useCases

 // Овечает на вопрос ЧТО делать,когда NotesRep в data слое отвечает на вопрос КАК делать

   suspend fun addNote(title:String, content:List<ContentItem>, isPinned:Boolean, updatedAt:Long)

    suspend fun  deleteNote(noteId:Int)

    suspend fun editNote(note: Note)

    fun getAllNotes():Flow<List<Note>>

    //suspend fun getNote(noteId:Int): Note
    fun getNote(noteId:Int):Flow<Note?>

    fun searchNotes(query:String):Flow<List<Note>> // поиск по определенному запросу

    suspend fun switchPinnedStatus(noteId: Int)


}