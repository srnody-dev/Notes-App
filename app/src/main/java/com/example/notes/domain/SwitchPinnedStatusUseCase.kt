package com.example.notes.domain

import javax.inject.Inject

class SwitchPinnedStatusUseCase @Inject constructor (private val repository: NotesRepository) {
    suspend operator fun invoke(noteId:Int){
        repository.switchPinnedStatus(noteId)
    }
}