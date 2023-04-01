package com.f0x1d.sense.ui.widget

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.f0x1d.sense.R
import com.f0x1d.sense.database.entity.ChatWithMessages

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun LazyItemScope.Chat(chatWithMessages: ChatWithMessages, delete: () -> Unit, click: () -> Unit) {
    OutlinedCard(
        modifier = Modifier
            .fillMaxWidth()
            .animateItemPlacement(),
        border = BorderStroke(2.dp, MaterialTheme.colorScheme.secondaryContainer),
        onClick = { click() }
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 20.dp)
            ) {
                Spacer(modifier = Modifier.size(20.dp))
                Text(
                    text = chatWithMessages.chat.title ?: stringResource(id = com.f0x1d.sense.R.string.nothing_there),
                    fontWeight = FontWeight.Bold,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 2
                )

                chatWithMessages.messages.apply {
                    if (isNotEmpty()) {
                        last().content.also { content ->
                            Spacer(modifier = Modifier.size(if (content != null) 10.dp else 15.dp))

                            if (content != null) {
                                Text(
                                    text = content,
                                    overflow = TextOverflow.Ellipsis,
                                    maxLines = 2
                                )
                            } else {
                                PulsatingDots(firstItemPadding = true)
                            }

                            Spacer(modifier = Modifier.size(if (content != null) 20.dp else 25.dp))
                        }
                    } else {
                        Spacer(modifier = Modifier.size(20.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.size(10.dp))

            Column(modifier = Modifier.padding(end = 5.dp)) {
                Spacer(modifier = Modifier.size(5.dp))

                IconButton(onClick = { delete() }) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_delete),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.error
                    )
                }

                Spacer(modifier = Modifier.size(5.dp))
            }
        }
    }
}