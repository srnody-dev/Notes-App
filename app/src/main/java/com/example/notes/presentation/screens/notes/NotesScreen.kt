@file:OptIn(ExperimentalFoundationApi::class, ExperimentalFoundationApi::class)

package com.example.notes.presentation.screens.notes

import android.content.Context
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.notes.R
import com.example.notes.domain.Note
import com.example.notes.presentation.screens.creation.CreateNoteViewModel
import com.example.notes.presentation.ui.theme.OtherNotesColors
import com.example.notes.presentation.ui.theme.PinnedNotesColors
import com.example.notes.presentation.utils.DateFormatter

@Composable
fun NotesScreen(
    modifier: Modifier = Modifier,
    viewModel: NotesViewModel = hiltViewModel(),
    onNoteClick: (Note) -> Unit,
    onAddNoteClick: () -> Unit
) {


    LocalContext.current.applicationContext



    val state = viewModel.state.collectAsState()

    val currentState = state.value
    Scaffold(modifier = modifier, floatingActionButton = {

        FloatingActionButton(modifier = modifier.size(80.dp),
            onClick = {
                onAddNoteClick()
            },
            content = {
                Icon(
                    painter = painterResource(R.drawable.ic_add_note),
                    contentDescription = null
                )
            },
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary,
            shape = CircleShape
        )

    }
    ) { innerPadding ->
        LazyColumn(
            contentPadding = innerPadding
        ) {
            item { Title(modifier = Modifier.padding(horizontal = 24.dp), text = "All Notes") }
            item { Spacer(modifier = Modifier.height(16.dp)) }
            item {
                SearchBar(
                    modifier = Modifier.padding(horizontal = 24.dp),
                    query = state.value.query,
                    onQueryChange = { viewModel.processCommand(NotesCommand.InputSearchQuery(it)) })
            }
            item { Spacer(modifier = Modifier.height(24.dp)) }
            if (currentState.pinnedNotes.isNotEmpty()) {
                item { Subtitle(text = "Pinned", modifier = Modifier.padding(horizontal = 24.dp)) }
            }
            item { Spacer(modifier = Modifier.height(16.dp)) }
            item {
                LazyRow(
                    modifier = modifier,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(horizontal = 24.dp)
                ) {

                    itemsIndexed(
                        items = currentState.pinnedNotes,
                        key = { index, note -> note.id }) { index, note ->
                        NoteCard(
                            //modifier = Modifier.widthIn(max = 160.dp),
                            modifier = Modifier.widthIn(max=170.dp, min = 120.dp),

                            note = note,
                            onNoteClick = { onNoteClick(note) },
                            onLongClick = {
                                viewModel.processCommand(
                                    NotesCommand.SwitchPinnedStatus(
                                        it.id
                                    )
                                )
                            },
                            backgroundColor = PinnedNotesColors[index % PinnedNotesColors.size]
                        )
                    }
                }
            }
            item { Spacer(modifier = Modifier.height(16.dp)) }
            if (currentState.otherNotes.isNotEmpty()) {
                item { Subtitle(text = "Other", modifier = Modifier.padding(horizontal = 24.dp)) }
            }
            item { Spacer(modifier = Modifier.height(16.dp)) }
            itemsIndexed(
                items = currentState.otherNotes,
                key = { index, note -> note.id }) { index, note ->
                NoteCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp),
                    note = note,
                    onNoteClick = { onNoteClick(note) },
                    onLongClick = { viewModel.processCommand(NotesCommand.SwitchPinnedStatus(it.id)) },
                    backgroundColor = OtherNotesColors[index % OtherNotesColors.size]
                )
                Spacer(modifier = Modifier.height(8.dp))


            }
            item {
                if (currentState.otherNotes.isEmpty() and currentState.pinnedNotes.isEmpty()) {
                    FirstAddNote()
                }
            }

        }
    }


}


@Composable
fun NoteCard(
    modifier: Modifier = Modifier,
    note: Note,
    backgroundColor: Color,
    onNoteClick: (Note) -> Unit,
    onLongClick: (Note) -> Unit
) {
    Column(modifier = modifier
        .clip(RoundedCornerShape(16.dp))
        .background(backgroundColor)
        .combinedClickable(
            onClick = { onNoteClick(note) },
            onLongClick = { onLongClick(note) }
        )
        .padding(16.dp)
    )
    {

        Text(
            text = note.title,
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurface,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = DateFormatter.formatDateToString(note.updatedAt),
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = note.content,
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.onSurface,
            fontWeight = FontWeight.Medium,
            maxLines = 3,
            overflow = TextOverflow.Ellipsis
        )


    }

}

@Composable
private fun FirstAddNote(modifier: Modifier = Modifier) {
    Column(modifier=modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) { Text(
        text = "Add your first note!",
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 120.dp),
        fontSize = 26.sp,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onBackground,
        textAlign = TextAlign.Center
    )
        Spacer(modifier = Modifier.height(20.dp))
        Icon(
            modifier = modifier.size(125.dp),
            painter = painterResource(R.drawable.ic_first_note),
            contentDescription = null
        )}

}

@Composable
private fun Title(modifier: Modifier = Modifier, text: String) {
    Text(
        modifier = modifier,
        fontSize = 24.sp,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onBackground,
        text = text
    )
}

@Composable
private fun Subtitle(modifier: Modifier = Modifier, text: String) {
    Text(
        modifier = modifier,
        text = text,
        fontSize = 14.sp,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )
}

@Composable
private fun SearchBar(
    modifier: Modifier = Modifier,
    query: String,
    onQueryChange: (String) -> Unit
) {
    TextField(
        modifier = modifier
            .fillMaxWidth()
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                shape = RoundedCornerShape(10.dp)
            ),
        shape = RoundedCornerShape(10.dp),
        value = query,
        onValueChange = onQueryChange,
        placeholder = {
            Text(
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                text = "Search"
            )
        },
        colors = TextFieldDefaults.colors(
            focusedContainerColor = MaterialTheme.colorScheme.surface,
            unfocusedContainerColor = MaterialTheme.colorScheme.surface,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent
        ),
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurface
            )
        }
    )
}