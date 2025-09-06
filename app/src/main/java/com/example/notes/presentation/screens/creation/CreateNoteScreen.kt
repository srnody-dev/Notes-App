@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.notes.presentation.screens.creation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.notes.presentation.utils.DateFormatter

@Composable
fun CreateNoteScreen(
    modifier: Modifier = Modifier,
    viewModel: CreateNoteViewModel = hiltViewModel(),
    onFinished: () -> Unit
) {

    val state = viewModel.state.collectAsState()
    val currentstate = state.value

    when (currentstate) {
        is CreateNoteState.Creation -> {
            Scaffold(
                modifier = modifier,

                topBar = {
                    TopAppBar(title = {
                        Text(
                            text = "Create Note",
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
                    }
                    )
                }


            ) { innerPadding ->
                Column(modifier = Modifier.padding(innerPadding)) {


                    TextField(modifier = Modifier
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
                                text = "Title",
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

                    TextField(modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp).weight(1f),
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
                                text = "Note something down",
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f)
                            )
                        },
                        value = currentstate.content.replaceFirstChar { it.uppercase() },
                        onValueChange = { viewModel.proccesCommand(CreateNoteCommand.InputContent(it)) })

                    Button(
                        onClick = {
                            viewModel.proccesCommand(CreateNoteCommand.Save)
                        }, modifier = Modifier
                            .padding(24.dp)
                            .fillMaxWidth(),
                        enabled = currentstate.isSaveEnabled, colors = ButtonDefaults.buttonColors(
                            contentColor = MaterialTheme.colorScheme.onPrimary,
                            disabledContentColor =MaterialTheme.colorScheme.onPrimary ,
                            containerColor = MaterialTheme.colorScheme.primary,
                            disabledContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                        ),
                        shape = RoundedCornerShape(10)
                    ) {
                        Text(text = "Save Note")
                    }


                }


            }
        }

        CreateNoteState.Finished -> {
            LaunchedEffect(key1 = Unit)   {
                onFinished()
            }

        }
    }
}