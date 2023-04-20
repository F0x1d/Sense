package com.f0x1d.sense.ui.widget

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.f0x1d.sense.database.entity.ChatMessage

@Composable
fun LazyItemScope.Message(message: ChatMessage, needTitle: Boolean = true) {
    BaseMessage(
        message = message,
        needTitle = needTitle
    ) {
        val textSelectionsColors = if (message.fromChatGPT)
            LocalTextSelectionColors.current
        else TextSelectionColors(
            handleColor = MaterialTheme.colorScheme.primary,
            backgroundColor = Color.DarkGray.copy(alpha = 0.4f)
        )

        CompositionLocalProvider(LocalTextSelectionColors provides textSelectionsColors) {
            SelectionContainer {
                Text(
                    modifier = Modifier.messageBubble(message),
                    text = message.content ?: "",
                    color = if (message.fromUser) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSecondaryContainer
                )
            }
        }
    }
}

@Composable
fun LazyItemScope.TypingMessage(needTitle: Boolean = true) {
    val message = ChatMessage(role = "assistant")

    BaseMessage(
        message = message,
        needTitle = needTitle
    ) {
        PulsatingDots(
            modifier = Modifier
                .messageBubble(message)
                .padding(5.dp)
        )
    }
}

@Composable
private fun LazyItemScope.BaseMessage(message: ChatMessage, needTitle: Boolean = true, content: @Composable ColumnScope.() -> Unit) {
    Row(modifier = Modifier.fillMaxWidth()) {
        Spacer(modifier = Modifier.size(if (message.fromUser) 20.dp else 5.dp))

        Column(
            modifier = Modifier.weight(1f),
            horizontalAlignment = if (message.fromUser) Alignment.End else Alignment.Start
        ) {
            if (needTitle) {
                Text(
                    modifier = Modifier.padding(horizontal = 10.dp),
                    text = stringResource(id = message.parsedRole),
                    style = MaterialTheme.typography.labelSmall
                )

                Spacer(modifier = Modifier.size(3.dp))
            }

            content()
        }

        Spacer(modifier = Modifier.size(if (message.fromChatGPT) 20.dp else 5.dp))
    }
}

fun Modifier.messageBubble(message: ChatMessage, picture: Boolean = false) = composed {
    background(
        color = if (message.fromUser) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondaryContainer,
        shape = messageCornerShape(message)
    ).padding(
        horizontal = if (picture) 0.dp else 10.dp,
        vertical = if (picture) 0.dp else 7.dp
    )
}

private fun messageCornerShape(message: ChatMessage) = RoundedCornerShape(
    20.dp,
    20.dp,
    if (message.fromUser) 5.dp else 20.dp,
    if (message.fromChatGPT) 5.dp else 20.dp
)