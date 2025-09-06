package com.example.notes.presentation.screens.creation

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.notes.data.NotesRepositoryImpl
import com.example.notes.domain.AddNoteUseCase
import com.example.notes.domain.NotesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CreateNoteViewModel @Inject constructor(private val addNoteUseCase: AddNoteUseCase) : ViewModel() {


    private val _state = MutableStateFlow<CreateNoteState>(CreateNoteState.Creation())
    val state = _state.asStateFlow()

    fun proccesCommand(command: CreateNoteCommand) { //обратока всех комманд

        when (command) {
            is CreateNoteCommand.Back -> {
                _state.update { CreateNoteState.Finished }
            }

            is CreateNoteCommand.InputContent -> {
                _state.update { previousState ->
                    if (previousState is CreateNoteState.Creation) { // если текущий стейт является стейтом создании заметки
                        previousState.copy(
                            content = command.content, // то мы берем этот стейт и обновляем у него контент
                            isSaveEnabled = previousState.title.isNotBlank() and command.content.isNotBlank() //устанавливаем корректное состояние кнопки
                            // если title и content не пустые то кнопку можно сделать активной
                        )

                    } else CreateNoteState.Creation(content = command.content) // если предыдущее состояние не было Creation, то мы
                    // создаем новый Creation и устанавливаем его в state, внутри меняя только content
                }
            }

            is CreateNoteCommand.InputTittle -> {
                _state.update { previousState ->
                    if (previousState is CreateNoteState.Creation) {
                        previousState.copy(
                            title = command.title,
                            isSaveEnabled = command.title.isNotBlank() and previousState.content.isNotBlank()
                        )

                    } else CreateNoteState.Creation(title = command.title)
                }
            }

            CreateNoteCommand.Save -> {
                viewModelScope.launch {
                    _state.update { previousState ->
                        if (previousState is CreateNoteState.Creation) { //если мы находимся в состоянии Creation
                            val title =
                                previousState.title // то мы сохраняем заметку и устанавливаем стейт Finished
                            val content = previousState.content
                            addNoteUseCase(title, content)
                            CreateNoteState.Finished
                        } else
                            previousState // если что то пошло не так то мы возващаем предыдущее состояние без изменений

                    }
                }


            }
        }
    }
}


sealed interface CreateNoteCommand { // потом это (шаг 2)
    data class InputTittle(val title: String) : CreateNoteCommand
    data class InputContent(val content: String) : CreateNoteCommand
    data object Save : CreateNoteCommand
    data object Back : CreateNoteCommand
}

sealed interface CreateNoteState { //сначало создаем (шаг 1)
    data class Creation(
        val title: String = "",
        val content: String = "",
        val isSaveEnabled: Boolean = false
    ) : CreateNoteState

    data object Finished : CreateNoteState
}