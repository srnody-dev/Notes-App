package com.example.notes.presentation

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.notes.presentation.navigation.NavGraph
import com.example.notes.presentation.ui.theme.NotesTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val content:Context=this.applicationContext
        enableEdgeToEdge()
        setContent {
            NotesTheme {
                NavGraph()
            }
        }
    }
}

