@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.notes.presentation.screens.editing

import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil3.compose.AsyncImage
import com.example.notes.R
import com.example.notes.domain.ContentItem
import com.example.notes.presentation.ui.theme.CustomIcons
import com.example.notes.presentation.utils.DateFormatter

@Composable
fun EditNoteScreen(
    noteId: Int,
    modifier: Modifier = Modifier,
    viewModel: EditNoteViewModel = hiltViewModel(
        creationCallback = { factory: EditNoteViewModel.Factory ->
            factory.create(noteId)
        },
    ),
    onFinished: () -> Unit
) {

    val state = viewModel.state.collectAsState()
    val currentstate = state.value

    val imagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri ->
            uri?.let {
                viewModel.proccesCommand(EditNoteCommand.AddImage(it))
            }

        }
    )


    when (currentstate) {

        is EditNoteState.Editing -> {
            Scaffold(
                modifier = modifier,

                topBar = {
                    TopAppBar(
                        title = {
                            Text(
                                text = stringResource(R.string.edit_note),
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }, colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = Color.Transparent,
                            navigationIconContentColor = MaterialTheme.colorScheme.onSurface
                        ), navigationIcon = {
                            Icon(
                                modifier = Modifier
                                    .padding(start = 16.dp, end = 8.dp)
                                    .clickable { viewModel.proccesCommand(EditNoteCommand.Back) },
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = null
                            )
                        }, actions = {
                            Icon(
                                modifier = Modifier
                                    .padding(end = 16.dp)
                                    .clickable { viewModel.proccesCommand(EditNoteCommand.DeleteNote) },
                                imageVector = Icons.Outlined.Delete,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurface
                            )



                            Icon(
                                modifier = Modifier
                                    .padding(end = 16.dp)
                                    .clickable {
                                        viewModel.proccesCommand(
                                            EditNoteCommand.SwitchPinnedStatus(
                                                noteId
                                            )
                                        )
                                    },
                                imageVector = if (currentstate.note.isPin) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurface
                            )

                            Icon(
                                modifier = Modifier
                                    .clickable { (imagePicker.launch("image/*")) }
                                    .padding(end = 24.dp),
                                imageVector = CustomIcons.AddPhoto,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurface
                            )


                        }
                    )
                }


            ) { innerPadding ->
                Column(modifier = Modifier.padding(innerPadding)) {


                    TextField(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent
                        ),
                        textStyle = TextStyle(
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,


                            ),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                        placeholder = {
                            Text(
                                text = stringResource(R.string.title),
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f)
                            )
                        },
                        value = currentstate.note.title,
                        onValueChange = { viewModel.proccesCommand(EditNoteCommand.InputTittle(it)) })


                    Text(
                        modifier = Modifier.padding(horizontal = 24.dp),
                        text = DateFormatter.formatDateToString(currentstate.note.updatedAt),
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )



                    Content(
                        modifier = Modifier
                            .weight(1f)
                            ,
                        content = currentstate.note.content,
                        onDeleteImageClick = { imageId->
                            viewModel.proccesCommand(EditNoteCommand.DeleteImage(imageId))

                        },
                        onTextChanged = { index, text ->
                            viewModel.proccesCommand(
                                EditNoteCommand.InputContent(
                                    content = text,
                                    index = index
                                )
                            )
                        },
                    )



                    Button(
                        onClick = {
                            viewModel.proccesCommand(EditNoteCommand.Save)
                        }, modifier = Modifier
                            .padding(24.dp)
                            .fillMaxWidth(),
                        enabled = currentstate.isSaveEnabled, colors = ButtonDefaults.buttonColors(
                            contentColor = MaterialTheme.colorScheme.onPrimary,
                            disabledContentColor = MaterialTheme.colorScheme.onPrimary,
                            containerColor = MaterialTheme.colorScheme.primary,
                            disabledContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                        ),
                        shape = RoundedCornerShape(10)
                    ) {
                        Text(text = stringResource(R.string.save_note))
                    }


                }


            }
        }

        EditNoteState.Finished -> {
            Log.d("EditNoteState","Finished")
            LaunchedEffect(key1 = Unit) {
                onFinished()
            }

        }

        EditNoteState.Initial -> {
            Scaffold(
                modifier = modifier,
                topBar = {
                    TopAppBar(
                        title = {
                            Text(
                                text = stringResource(R.string.edit_note_initial),
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        },
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = Color.Transparent,
                            navigationIconContentColor = MaterialTheme.colorScheme.onSurface
                        )
                    )
                }
            ) { innerPadding ->
                Column(
                    modifier = Modifier
                        .padding(innerPadding)
                        .fillMaxSize()
                ) {

                    LinearProgressIndicator(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(3.dp),
                        color = MaterialTheme.colorScheme.primary,
                        trackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
                    )


                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = stringResource(R.string.loading_initial),
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                            fontSize = 14.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun TextContent(
    modifier: Modifier = Modifier,
    text: String,
    onTextChanged: (String) -> Unit
) {
    TextField(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Color.Transparent,
            unfocusedContainerColor = Color.Transparent,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent
        ),
        textStyle = TextStyle(
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,


            ),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
        placeholder = {
            Text(
                text = stringResource(R.string.note_something_down),
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f)
            )
        },
        value = text.replaceFirstChar { it.uppercase() },
        onValueChange = onTextChanged,
    )
}

@Composable
private fun ImageContent(
    modifier: Modifier = Modifier,
    imageUrl: String,
    onDeleteImageClick: () -> Unit
) {
    Box(modifier = modifier) {


        AsyncImage(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp)),
            model = imageUrl,
            contentDescription = null,
            contentScale = ContentScale.FillWidth
        )
        Icon(
            modifier = modifier
                .align(Alignment.TopEnd)
                .padding(8.dp)
                .size(24.dp)
                .clickable { onDeleteImageClick() },
            tint = MaterialTheme.colorScheme.onSurface,
            imageVector = Icons.Default.Clear,
            contentDescription = null
        )


    }

}


@Composable
private fun Content(
    modifier: Modifier = Modifier,
    content: List<ContentItem>,
    onDeleteImageClick: (Int) -> Unit,
    onTextChanged: (Int, String) -> Unit
) {
    LazyColumn(modifier = modifier) {
        content.forEachIndexed { index, contentItem ->
            item(key = index) {
                when (contentItem) {
                    is ContentItem.Image -> {
                        ImageContent(
                            modifier = Modifier.padding(horizontal = 16.dp),
                            imageUrl = contentItem.url, onDeleteImageClick = {
                                onDeleteImageClick(index)
                            }
                        )
                    }

                    is ContentItem.Text -> {
                        TextContent(
                            modifier= Modifier,
                            text = contentItem.content, onTextChanged = { onTextChanged(index, it) }
                        )
                    }
                }
            }

        }

    }
}