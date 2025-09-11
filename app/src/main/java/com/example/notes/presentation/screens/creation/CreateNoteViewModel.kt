package com.example.notes.presentation.screens.creation

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.notes.domain.AddNoteUseCase
import com.example.notes.domain.ContentItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CreateNoteViewModel @Inject constructor(private val addNoteUseCase: AddNoteUseCase) :
    ViewModel() {


    private val _state = MutableStateFlow<CreateNoteState>(CreateNoteState.Creation())
    val state = _state.asStateFlow()

    fun proccesCommand(command: CreateNoteCommand) { //обратока всех комманд

        when (command) {
            is CreateNoteCommand.Back -> {
                _state.update { CreateNoteState.Finished }
            }


            is CreateNoteCommand.InputContent -> {

                _state.update { previousState ->
                    if (previousState is CreateNoteState.Creation) {
                        val newContent = previousState.content
                            .mapIndexed { index, contentItem ->
                                if (index == command.index && contentItem is ContentItem.Text) {
                                    contentItem.copy(content = command.content)
                                } else {
                                    contentItem
                                }
                            }
                        previousState.copy(
                            content = newContent
                        )
                    } else {
                        previousState
                    }
                }
            }


            is CreateNoteCommand.InputTittle -> {
                _state.update { previousState ->
                    if (previousState is CreateNoteState.Creation) {
                        previousState.copy(
                            title = command.title,
                        )

                    } else previousState
                }
            }
            CreateNoteCommand.Save -> {
                viewModelScope.launch {
                    _state.update { previousState ->
                        if (previousState is CreateNoteState.Creation) {
                            val title = previousState.title

                            val filteredContent = previousState.content.filterNot { item ->
                                item is ContentItem.Text && item.content.isBlank()
                            }

                            val finalContent = if (filteredContent.isEmpty() ||
                                filteredContent.last() !is ContentItem.Text) {
                                filteredContent + ContentItem.Text("")
                            } else {
                                filteredContent
                            }

                            addNoteUseCase(title, finalContent)
                            CreateNoteState.Finished
                        } else {
                            previousState
                        }
                    }
                }
            }

            is CreateNoteCommand.AddImage -> {
                /*
                _state.update { previousState ->
                    if (previousState is CreateNoteState.Creation) {
                        val newItems=previousState.content.toMutableList()
                        val lastItem=newItems.last()

                        if (lastItem is ContentItem.Text && lastItem.content.isBlank()){
                            newItems.removeAt(newItems.lastIndex)
                        }
                        newItems.add(ContentItem.Image(command.uri.toString()))
                        newItems.add(ContentItem.Text(""))
                        previousState.copy(content = newItems)

                    }
                    else previousState
                }*/

                //тоже самое что и сверху только в функциональном стиле


                _state.update { previousState ->
                    if (previousState is CreateNoteState.Creation) {
                        previousState.content.toMutableList().apply {
                            val lastItem = last()
                            if (lastItem is ContentItem.Text && lastItem.content.isBlank()) {
                                removeAt(lastIndex)
                            }
                            add(ContentItem.Image(command.uri.toString()))

                            add(ContentItem.Text(""))
                        }.let {
                            previousState.copy(content = it)
                        }
                    } else {
                        previousState
                    }
                }
            }

            is CreateNoteCommand.DeleteImage -> {
                _state.update { previousState ->
                    if (previousState is CreateNoteState.Creation) {

                        previousState.content.toMutableList().apply {
                            removeAt(command.imageId)

                        }.let {
                            previousState.copy(content = it)
                        }
                    } else previousState
                }
            }
        }


    }
}


sealed interface CreateNoteCommand { // потом команды (шаг 2)
    data class InputTittle(val title: String) : CreateNoteCommand
    data class InputContent(val content: String, val index: Int) : CreateNoteCommand
    data object Save : CreateNoteCommand
    data object Back : CreateNoteCommand

    data class AddImage(val uri: Uri) : CreateNoteCommand
    data class DeleteImage(val imageId: Int) : CreateNoteCommand
}

sealed interface CreateNoteState { //сначало создаем cостояния (шаг 1)
    data class Creation(
        val title: String = "",
        val content: List<ContentItem> = listOf(ContentItem.Text("")),
    ) : CreateNoteState {
        val isSaveEnabled: Boolean
            get() {
                return when {
                    title.isBlank() -> false
                    content.isEmpty() -> false
                    else -> {
                        content.any {
                            it !is ContentItem.Text || it.content.isNotBlank()
                        }
                    }
                }
            }
    }


    data object Finished : CreateNoteState
}