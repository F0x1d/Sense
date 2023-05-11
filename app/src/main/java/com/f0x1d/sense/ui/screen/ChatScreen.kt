package com.f0x1d.sense.ui.screen

import android.app.Activity
import android.content.Context
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.f0x1d.sense.R
import com.f0x1d.sense.database.entity.ChatMessage
import com.f0x1d.sense.extensions.copyText
import com.f0x1d.sense.ui.activity.ViewModelFactoryProvider
import com.f0x1d.sense.ui.widget.Message
import com.f0x1d.sense.ui.widget.MessageAction
import com.f0x1d.sense.ui.widget.NavigationBackIcon
import com.f0x1d.sense.ui.widget.TypingMessage
import com.f0x1d.sense.viewmodel.ChatViewModel
import dagger.hilt.android.EntryPointAccessors
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)
@Composable
fun ChatScreen(navController: NavController, chatId: Long) {
    val viewModel = chatViewModel(chatId = chatId)

    val chatWithMessages by viewModel.chatWithMessages.collectAsStateWithLifecycle(initialValue = null)

    val lazyListState = rememberLazyListState()

    val scope = rememberCoroutineScope()

    val scrollDownFabVisible by remember {
        derivedStateOf {
            lazyListState.canScrollBackward && !viewModel.addingMyMessage && (
                    lazyListState.firstVisibleItemIndex > 0 || lazyListState.firstVisibleItemScrollOffset > 50
            )
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(
            title = {
                Text(
                    text = chatWithMessages?.chat?.title ?: "",
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1
                )
            },
            navigationIcon = { NavigationBackIcon(navController = navController) }
        )

        AnimatedVisibility(visible = lazyListState.canScrollForward) {
            Divider()
        }

        Box(modifier = Modifier.weight(1f)) {
            LazyColumn(
                contentPadding = PaddingValues(
                    start = 0.dp,
                    top = 10.dp,
                    end = 0.dp,
                    bottom = 10.dp
                ),
                reverseLayout = true,
                verticalArrangement = Arrangement.spacedBy(10.dp),
                state = lazyListState
            ) {
                val items = chatWithMessages?.messages ?: emptyList()

                itemsIndexed(
                    items,
                    key = { _, it -> it.id }
                ) { index, message ->
                    val needTitle = items.getOrNull(index + 1)?.role != message.role

                    if (message.content == null) {
                        TypingMessage(needTitle = needTitle)
                    } else {
                        Message(
                            message = message,
                            needTitle = needTitle,
                            actions = if (message.generating) emptyList() else generateMessageActions(
                                context = LocalContext.current,
                                message = message,
                                viewModel = viewModel
                            )
                        )
                    }
                }
            }

            androidx.compose.animation.AnimatedVisibility(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(10.dp),
                visible = scrollDownFabVisible,
                enter = scaleIn(),
                exit = scaleOut()
            ) {
                SmallFloatingActionButton(
                    onClick = {
                        scope.launch {
                            lazyListState.animateScrollToItem(0)
                        }
                    }
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_arrow_down),
                        contentDescription = null
                    )
                }
            }
        }

        if (chatWithMessages?.messages != null) {
            LaunchedEffect(chatWithMessages!!.messages.size) {
                chatWithMessages?.messages?.also { messages ->
                    lazyListState.animateScrollToItem(0)
                }
                viewModel.addingMyMessage = false
            }
        }

        AnimatedVisibility(visible = lazyListState.canScrollBackward) {
            Divider()
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                modifier = Modifier
                    .padding(
                        top = 5.dp,
                        start = 10.dp,
                        end = 10.dp,
                        bottom = 10.dp
                    )
                    .weight(1f),
                value = viewModel.text,
                onValueChange = { viewModel.text = it },
                label = { Text(text = stringResource(R.string.message)) },
                shape = RoundedCornerShape(20.dp)
            )

            SmallFloatingActionButton(
                modifier = Modifier.padding(end = 10.dp),
                onClick = {
                    viewModel.send(chatWithMessages ?: return@SmallFloatingActionButton)
                },
                elevation = FloatingActionButtonDefaults.elevation(0.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_arrow_upward),
                    contentDescription = null
                )
            }
        }
    }
}

@Composable
private fun generateMessageActions(
    context: Context,
    message: ChatMessage,
    viewModel: ChatViewModel
) = remember {
    listOf(
        MessageAction(
            title = android.R.string.copy,
            icon = R.drawable.ic_copy,
            onClick = { context.copyText(message.content ?: "") }
        ),
        MessageAction(
            title = R.string.delete,
            icon = R.drawable.ic_delete_message,
            tint = Color.Red,
            onClick = { viewModel.delete(message) }
        )
    )
}

@Composable
fun chatViewModel(chatId: Long): ChatViewModel {
    val factory = EntryPointAccessors.fromActivity(
        LocalContext.current as Activity,
        ViewModelFactoryProvider::class.java
    ).chatViewModelFactory()

    return viewModel(factory = ChatViewModel.provideFactory(factory, chatId))
}