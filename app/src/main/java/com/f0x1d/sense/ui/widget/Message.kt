package com.f0x1d.sense.ui.widget

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.f0x1d.sense.database.entity.ChatMessage

@Composable
fun Message(
    message: ChatMessage,
    needTitle: Boolean = true,
    actions: List<MessageAction> = emptyList()
) {
    BaseMessage(
        message = message,
        needTitle = needTitle,
        actions = actions
    ) { modifier, expanded ->
        val textSelectionsColors = if (message.fromChatGPT)
            LocalTextSelectionColors.current
        else TextSelectionColors(
            handleColor = MaterialTheme.colorScheme.primary,
            backgroundColor = Color.DarkGray.copy(alpha = 0.4f)
        )

        CompositionLocalProvider(LocalTextSelectionColors provides textSelectionsColors) {
            if (!expanded) {
                SelectionContainer { // maybe google will make internal SelectionContainer with access to selection public
                    MessageText(
                        modifier = modifier,
                        message = message
                    )
                }
            } else {
                MessageText(
                    modifier = modifier,
                    message = message
                )
            }
        }
    }
}

@Composable
private fun MessageText(modifier: Modifier, message: ChatMessage) {
    Text(
        modifier = modifier.messageBubble(message),
        text = message.content ?: "",
        color = if (message.fromUser) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSecondaryContainer
    )
}

@Composable
fun TypingMessage(needTitle: Boolean = true) {
    val message = ChatMessage(role = "assistant")

    BaseMessage(
        message = message,
        needTitle = needTitle
    ) { modifier, expanded ->
        PulsatingDots(
            modifier = Modifier
                .messageBubble(message)
                .padding(5.dp)
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun BaseMessage(
    message: ChatMessage,
    needTitle: Boolean = true,
    actions: List<MessageAction> = emptyList(),
    content: @Composable ColumnScope.(Modifier, Boolean) -> Unit
) {
    var expanded by rememberSaveable { mutableStateOf(false) }

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

            content(
                Modifier.clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) { expanded = !expanded },
                expanded
            )

            if (actions.isNotEmpty()) {
                AnimatedVisibility(
                    visible = expanded,
                    enter = expandVertically(),
                    exit = shrinkVertically()
                ) {
                    FlowRow(horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                        actions.forEach { ActionChip(messageAction = it) { expanded = false } }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.size(if (message.fromChatGPT) 20.dp else 5.dp))
    }
}

@Composable
private fun ActionChip(messageAction: MessageAction, onDismiss: () -> Unit) {
    val tint = messageAction.tint ?: MaterialTheme.colorScheme.primary

    AssistChip(
        onClick = {
            messageAction.onClick()
            onDismiss()
        },
        label = { Text(text = stringResource(id = messageAction.title)) },
        leadingIcon = {
            Icon(
                modifier = Modifier.size(20.dp),
                painter = painterResource(id = messageAction.icon),
                contentDescription = null
            )
        },
        colors = AssistChipDefaults.assistChipColors(
            labelColor = tint,
            leadingIconContentColor = tint
        ),
        border = AssistChipDefaults.assistChipBorder(borderColor = tint)
    )
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

data class MessageAction(
    val title: Int,
    val icon: Int,
    val tint: Color? = null,
    val onClick: () -> Unit
)