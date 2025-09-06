package com.example.notes.presentation.screens.editing

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.notes.domain.DeleteNoteUseCase
import com.example.notes.domain.EditNoteUseCase
import com.example.notes.domain.GetNoteUseCase
import com.example.notes.domain.Note
import com.example.notes.domain.SwitchPinnedStatusUseCase
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel(assistedFactory = EditNoteViewModel.Factory::class)
class EditNoteViewModel @AssistedInject constructor(
    @Assisted("noteId") private val noteId: Int,
    private val editNoteUseCase: EditNoteUseCase,
    private val getNoteUseCase: GetNoteUseCase,
    private val deleteNoteUseCase: DeleteNoteUseCase,
    private val switchPinnedStatusUseCase: SwitchPinnedStatusUseCase

) : ViewModel(
) {


    private val _state = MutableStateFlow<EditNoteState>(EditNoteState.Initial)

    val state = _state.asStateFlow()


    init {/*
        viewModelScope.launch {
            _state.update {
                val note = getNoteUseCase(noteId)
                EditNoteState.Editing(note)//EditNoteState.Editing(note)
            }
        }*/

        viewModelScope.launch {
            getNoteUseCase(noteId).onEach { note ->
                when (note) {
                    null -> {
                        _state.update {
                            EditNoteState.Finished
                        }

                    }

                    else -> {
                        _state.update { EditNoteState.Editing(note) }
                    }
                }

            }.catch {
                _state.update { EditNoteState.Finished }
            }.launchIn(viewModelScope)


        }


    }


    fun proccesCommand(command: EditNoteCommand) { //обратока всех комманд

        when (command) {
            is EditNoteCommand.Back -> _state.update { EditNoteState.Finished }

            is EditNoteCommand.InputContent -> _state.update { previousState ->
                if (previousState is EditNoteState.Editing) {
                    val newNote = previousState.note.copy(content = command.content)

                    previousState.copy(note = newNote)
                } else previousState // возвращем предыдушее состояние если не в режиме редактирования
            }

            is EditNoteCommand.InputTittle -> _state.update { previousState ->
                if (previousState is EditNoteState.Editing) {
                    val newNote = previousState.note.copy(title = command.title)
                    previousState.copy(note = newNote)
                } else previousState
            }

            EditNoteCommand.Save -> viewModelScope.launch {
                _state.update { previousState ->
                    if (previousState is EditNoteState.Editing) {
                        val note = previousState.note
                        editNoteUseCase(note)
                        EditNoteState.Finished
                    } else
                        previousState // если что то пошло не так то мы возващаем предыдущее состояние без изменений

                }
            }

            EditNoteCommand.Delete -> {
                viewModelScope.launch {
                    _state.update { previousState ->
                        if (previousState is EditNoteState.Editing) {
                            val note = previousState.note
                            deleteNoteUseCase(note.id)
                            EditNoteState.Finished
                        } else
                            previousState // если что то пошло не так то мы возващаем предыдущее состояние без изменений

                    }
                }
            }

            is EditNoteCommand.SwitchPinnedStatus -> {

                viewModelScope.launch { switchPinnedStatusUseCase(command.noteId) }


            }
        }
    }

    @AssistedFactory
    interface Factory {
        fun create(@Assisted("noteId") noteId: Int): EditNoteViewModel
    }

}


sealed interface EditNoteCommand { // потом это (шаг 2)
    data class InputTittle(val title: String) : EditNoteCommand
    data class InputContent(val content: String) : EditNoteCommand
    data object Save : EditNoteCommand
    data object Back : EditNoteCommand
    data object Delete : EditNoteCommand

    data class SwitchPinnedStatus(val noteId: Int) : EditNoteCommand

}


sealed interface EditNoteState { //сначало создаем (шаг 1)

    data object Initial : EditNoteState

    data class Editing(
        val note: Note
    ) : EditNoteState {
        val isSaveEnabled: Boolean
            get() = note.title.isNotBlank() and note.content.isNotBlank()
    }

    data object Finished : EditNoteState

}

