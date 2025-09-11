package com.example.notes.presentation.screens.editing

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.notes.domain.ContentItem
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

            is EditNoteCommand.InputContent -> {
                _state.update { previousState ->
                    if (previousState is EditNoteState.Editing) {
                        val newContent = previousState.note.content.mapIndexed { index, item ->
                            if (index == command.index && item is ContentItem.Text) {
                                item.copy(content = command.content)
                            } else {
                                item
                            }
                        }
                        val newNote = previousState.note.copy(content = newContent)

                        previousState.copy(note = newNote)
                    } else previousState // возвращем предыдушее состояние если не в режиме редактирования
                }
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

                        val filteredContent = note.content.filterNot { item ->
                            item is ContentItem.Text && item.content.isBlank()
                        }

                        val finalContent = if (filteredContent.isEmpty() ||
                            filteredContent.last() !is ContentItem.Text
                        ) {
                            filteredContent + ContentItem.Text("")
                        } else {
                            filteredContent
                        }

                        editNoteUseCase(note.copy(content = finalContent))
                        EditNoteState.Finished
                    } else
                        previousState // если что то пошло не так то мы возващаем предыдущее состояние без изменений

                }
            }

            EditNoteCommand.DeleteNote -> {
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

            is EditNoteCommand.AddImage -> {
                _state.update { previousState ->
                    if (previousState is EditNoteState.Editing) {
                        val oldNote = previousState.note
                        oldNote.content.toMutableList().apply {
                            val lasItem = last()

                            if (lasItem is ContentItem.Text && lasItem.content.isBlank()) {
                                removeAt(lastIndex)
                            }
                            add(ContentItem.Image(command.uri.toString()))
                            add(ContentItem.Text(""))

                        }.let {
                            val newNote = oldNote.copy(content = it)
                            previousState.copy(note = newNote)
                        }
                    } else {
                        previousState
                    }
                }
            }

            is EditNoteCommand.DeleteImage -> {
                _state.update { previousState ->
                    if (previousState is EditNoteState.Editing) {
                        val newContent = previousState.note.content.toMutableList()
                        newContent.removeAt(index = command.imageId)
                        val newNote = previousState.note.copy(content = newContent)
                        previousState.copy(note = newNote)
                    } else previousState
                }
            }
        }

    }

    @AssistedFactory
    interface Factory {
        fun create(@Assisted("noteId") noteId: Int): EditNoteViewModel
    }

}


sealed interface EditNoteCommand {
    data class InputTittle(val title: String) : EditNoteCommand
    data class InputContent(val content: String, val index: Int) : EditNoteCommand
    data object Save : EditNoteCommand
    data object Back : EditNoteCommand
    data object DeleteNote : EditNoteCommand

    data class SwitchPinnedStatus(val noteId: Int) : EditNoteCommand

    data class AddImage(val uri: Uri) : EditNoteCommand

    data class DeleteImage(val imageId: Int) : EditNoteCommand

}


sealed interface EditNoteState {

    data object Initial : EditNoteState

    data class Editing(
        val note: Note
    ) : EditNoteState {
        val isSaveEnabled: Boolean
            get() {
                return when {
                    note.title.isBlank() -> false
                    note.content.isEmpty() -> false
                    else -> {
                        note.content.any {
                            it !is ContentItem.Text || it.content.isNotBlank()
                        }
                    }
                }
            }
    }

    data object Finished : EditNoteState

}


