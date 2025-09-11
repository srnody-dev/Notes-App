@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.notes.presentation.screens.creation

import com.example.notes.R
import androidx.compose.ui.res.stringResource
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil3.compose.AsyncImage
import com.example.notes.domain.ContentItem
import com.example.notes.presentation.ui.theme.CustomIcons
import com.example.notes.presentation.utils.DateFormatter

@Composable
fun CreateNoteScreen(
    modifier: Modifier = Modifier,
    viewModel: CreateNoteViewModel = hiltViewModel(),
    onFinished: () -> Unit
) {

    val state = viewModel.state.collectAsState()
    val currentstate = state.value

    val imagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri ->
            uri?.let {
                viewModel.proccesCommand(CreateNoteCommand.AddImage(it))
            }

        }
    )

    when (currentstate) {
        is CreateNoteState.Creation -> {
            Scaffold(
                modifier = modifier,

                topBar = {
                    TopAppBar(
                        title = {
                            Text(
                                text = stringResource(R.string.create_note),
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
                                    .clickable { viewModel.proccesCommand(CreateNoteCommand.Back) },
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = null
                            )
                        }, actions = {
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
                        value = currentstate.title.replaceFirstChar { it.uppercase() },
                        onValueChange = { viewModel.proccesCommand(CreateNoteCommand.InputTittle(it)) })

                    Text(
                        modifier = Modifier.padding(horizontal = 24.dp),
                        text = DateFormatter.formanCurrentDay(),
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Content(
                        modifier = Modifier
                            .weight(1f),
                        content = currentstate.content,
                        onDeleteImageClick = { imageId->
                            viewModel.proccesCommand( CreateNoteCommand.DeleteImage(imageId))

                        },
                        onTextChanged = {index,text ->
                            viewModel.proccesCommand(
                                CreateNoteCommand.InputContent(
                                    content = text, index =index
                                )
                            )
                        }
                    )

                    Button(
                        onClick = {
                            viewModel.proccesCommand(CreateNoteCommand.Save)
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

        CreateNoteState.Finished -> {
            LaunchedEffect(key1 = Unit) {
                onFinished()
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
            .fillMaxWidth(),
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
    onTextChanged: (Int,String) -> Unit
) {
    LazyColumn(modifier = modifier) {
        content.forEachIndexed{ index, contentItem ->
            item(key = index) {
                when (contentItem) {
                    is ContentItem.Image -> {

                                ImageContent(
                                    modifier = Modifier.padding(horizontal = 24.dp),
                                    imageUrl =contentItem.url , onDeleteImageClick = {
                                        onDeleteImageClick(index)
                                    }
                                )
                    }

                    is ContentItem.Text -> {
                        TextContent(
                            modifier = Modifier,
                            text = contentItem.content,
                            onTextChanged = {
                                onTextChanged(index,it)
                            }
                        )
                    }
                }
            }

        }

    }
}