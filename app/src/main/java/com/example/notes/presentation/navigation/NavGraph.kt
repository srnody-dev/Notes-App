package com.example.notes.presentation.navigation

import android.os.Bundle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.notes.presentation.screens.creation.CreateNoteScreen
import com.example.notes.presentation.screens.editing.EditNoteScreen
import com.example.notes.presentation.screens.notes.NotesScreen


@Composable
fun NavGraph() {

    val navContoller = rememberNavController()


    NavHost( // отвечает за переключение между экранами
        navController = navContoller,// хранит все состояние навигации
        startDestination = Screen.Notes.route //стартовое назначение

    ) {
        composable(Screen.Notes.route) {
            NotesScreen(
                onNoteClick = {
                    navContoller.navigate(Screen.EditNote.createRoute(it.id))
                },
                onAddNoteClick = {
                    navContoller.navigate(Screen.CreateNote.route)
                }
            )
        }

        composable(Screen.CreateNote.route) {
            CreateNoteScreen(onFinished = {
                navContoller.popBackStack()
            }
            )
        }

        composable(Screen.EditNote.route) {

            val noteId=Screen.EditNote.getNoteId(it.arguments)

            EditNoteScreen(onFinished = {
                navContoller.popBackStack()
            },
                noteId = noteId
            )
        }

    }


}


sealed class Screen(val route: String) {
    data object Notes : Screen("notes")
    data object EditNote : Screen("edit_note/{note_id}"){
        fun createRoute(noteId: Int):String{
            return "edit_note/$noteId"
        }

        fun getNoteId(arguments:Bundle?):Int{
            return arguments?.getString("note_id")?.toInt() ?:0
        }

    }
    data object CreateNote : Screen("create_note"){
    }
}


