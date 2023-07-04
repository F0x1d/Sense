package com.f0x1d.sense.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.f0x1d.sense.BuildConfig
import com.f0x1d.sense.R
import com.f0x1d.sense.extensions.openLink
import com.f0x1d.sense.model.Screen
import com.f0x1d.sense.ui.widget.Chat
import com.f0x1d.sense.ui.widget.ErrorAlertDialog
import com.f0x1d.sense.viewmodel.ChatsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatsScreen(navController: NavController) {
    val viewModel = hiltViewModel<ChatsViewModel>()

    val chatsWithMessages by viewModel.chatsWithMessages.collectAsStateWithLifecycle(initialValue = emptyList())

    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(rememberTopAppBarState())

    Box(modifier = Modifier.fillMaxSize()) {
        Column {
            LargeTopAppBar(
                title = { Text(text = stringResource(id = R.string.chats)) },
                actions = {
                    IconButton(onClick = { viewModel.infoDialogOpened = true }) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_info),
                            contentDescription = null
                        )
                    }
                    IconButton(onClick = { navController.navigate(Screen.Pictures.route) }) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_photo),
                            contentDescription = null
                        )
                    }
                    IconButton(onClick = { navController.navigate(Screen.Settings.route) }) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_settings),
                            contentDescription = null
                        )
                    }
                },
                scrollBehavior = scrollBehavior
            )

            LazyColumn(
                modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
                contentPadding = PaddingValues(
                    top = 16.dp,
                    start = 16.dp,
                    end = 16.dp,
                    bottom = bottomPaddingForFAB()
                ),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(
                    chatsWithMessages,
                    key = { it.chat.id }
                ) { chatWithMessages ->
                    Chat(
                        chatWithMessages = chatWithMessages,
                        delete = { viewModel.deleteChat(chatWithMessages.chat) }
                    ) {
                        navController.navigate("${Screen.Chat.route}/${chatWithMessages.chat.id}")
                    }
                }
            }
        }

        FloatingActionButton(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
                .navigationBarsPadding(),
            onClick = {
                viewModel.createChat {
                    navController.navigate("${Screen.Chat.route}/${it.id}")
                }
            }
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_add),
                contentDescription = null
            )
        }

        InfoDialog(opened = viewModel.infoDialogOpened) {
            viewModel.infoDialogOpened = false
        }
        
        ErrorAlertDialog(viewModel = viewModel)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun InfoDialog(opened: Boolean, onClose: () -> Unit) {
    if (!opened) return
    val context = LocalContext.current

    AlertDialog(
        onDismissRequest = onClose,
        title = { Text(text = stringResource(id = R.string.information)) },
        text = { Text(text = stringResource(id = R.string.version, BuildConfig.VERSION_NAME, BuildConfig.VERSION_CODE)) },
        confirmButton = {
            TextButton(onClick = { context.openLink("https://github.com/F0x1d/Sense") }) {
                Text(text = stringResource(id = R.string.github))
            }
        },
        dismissButton = {
            TextButton(onClick = { context.openLink("https://t.me/f0x1dsshit") }) {
                Text(text = stringResource(id = R.string.releases))
            }
        }
    )
}

@Composable
fun bottomPaddingForFAB() = 88.dp + WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()