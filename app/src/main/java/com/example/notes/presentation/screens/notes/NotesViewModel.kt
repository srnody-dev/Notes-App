@file:Suppress("OPT_IN_USAGE")

package com.example.notes.presentation.screens.notes

import android.content.Context
import android.util.Log
import androidx.compose.runtime.remember
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.notes.data.NotesRepositoryImpl
import com.example.notes.data.TestNotesRepositoryImpl
import com.example.notes.domain.AddNoteUseCase
import com.example.notes.domain.DeleteNoteUseCase
import com.example.notes.domain.EditNoteUseCase
import com.example.notes.domain.GetAllNotesUseCase
import com.example.notes.domain.GetNoteUseCase
import com.example.notes.domain.Note
import com.example.notes.domain.SearchNotesUseCse
import com.example.notes.domain.SwitchPinnedStatusUseCase
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class NotesViewModel @Inject constructor(
    private val getAllNotesUseCase: GetAllNotesUseCase,
    private val searchNotesUseCse: SearchNotesUseCse,
    private val switchPinnedStatusUseCase: SwitchPinnedStatusUseCase
    ) :
    ViewModel() {



    private val query = MutableStateFlow("")

    private var _state = MutableStateFlow(NotesScreenState())
    val state = _state.asStateFlow()

    //private val scope = CoroutineScope(Dispatchers.IO) не нужен т.к есть спецаильная функция viewModelScope для Жизн цикла scope корутин
    // при уничтожении viewModel должны отменяется все scope coroutine


    init {

        // цепочка операторов query (StateFlow) → flatMapLatest → onEach → launchIn
        query
            .onEach { input -> _state.update { it.copy(query = input) } } // это для того чтобы стейт обновлялся,мы копируем
            // данный стейт и изменяем параметр query
            .flatMapLatest { input -> /* Каждое выдаваемое значение преобразуется в поток,
        при выдаче нового значения предыдущие потоки отменяются, значения выдаются из последнего потока.
        Активен только последний поток, выдаются его значения, предыдущие потоки отменяются.*/
                if (input.isBlank()) {
                    getAllNotesUseCase() //когда пусто получаем все заметки
                } else {
                    searchNotesUseCse(input) //Если есть текст запроса, выполняем поиск по этому запросу
                }
            }
            .onEach { notes ->
                val pinnedNotes = notes.filter { it.isPin }
                val otherNotes = notes.filter { !it.isPin }
                _state.update { it.copy(pinnedNotes = pinnedNotes, otherNotes = otherNotes) }
                /*
                Получаем список заметок (it - это List<Note>)
                Разделяем его на два списка:
                pinnedNotes - заметки с isPin = true
                otherNotes - все остальные
                Обновляем состояние ViewModel*/

            }.launchIn(viewModelScope)


    }

    fun processCommand(command: NotesCommand) {
        viewModelScope.launch {
            when (command) {
                is NotesCommand.InputSearchQuery -> {
                    query.update { command.query.trim() }
                }

                is NotesCommand.SwitchPinnedStatus -> {
                    switchPinnedStatusUseCase(command.noteId)
                }

            }
        }

    }


}

sealed interface NotesCommand {
    data class InputSearchQuery(val query: String) : NotesCommand

    data class SwitchPinnedStatus(val noteId: Int) : NotesCommand


}

data class NotesScreenState(
    val query: String = "",
    val pinnedNotes: List<Note> = listOf(),
    val otherNotes: List<Note> = listOf()
)