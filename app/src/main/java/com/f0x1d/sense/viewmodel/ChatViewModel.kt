package com.f0x1d.sense.viewmodel

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import com.f0x1d.sense.SenseApplication
import com.f0x1d.sense.database.AppDatabase
import com.f0x1d.sense.database.entity.ChatMessage
import com.f0x1d.sense.database.entity.ChatWithMessages
import com.f0x1d.sense.di.viewmodel.ChatId
import com.f0x1d.sense.repository.network.OpenAIRepository
import com.f0x1d.sense.viewmodel.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    @ChatId val chatId: Long,
    private val database: AppDatabase,
    private val openAIRepository: OpenAIRepository,
    application: Application
): BaseViewModel(application) {

    val chatWithMessages = database.chatsDao().getById(chatId)
        .map { it.copy(messages = it.messages.asReversed()) }
        .flowOn(Dispatchers.IO)

    var text by mutableStateOf("")
    var addingMyMessage by mutableStateOf(false)

    fun send(chatWithMessages: ChatWithMessages) = SenseApplication.applicationScope.onIO({
        if (database.messagesDao().countGeneratingMessagesInChat(chatId) > 0) return@onIO

        val messageText = text.trim()
        if (messageText.isEmpty()) return@onIO

        val userMessage = ChatMessage(
            content = messageText,
            role = "user",
            chatId = chatId
        )

        withContext(Dispatchers.Main) {
            addingMyMessage = true
            text = ""
        }

        chatWithMessages.chat.also { chat ->
            if (chat.title == null) {
                database.chatsDao().insert(chat.copy(title = userMessage.content))
            }
        }

        val responseMessage = ChatMessage(
            role = "assistant",
            generating = true,
            chatId = chatId
        )
        val responseMessageId = database.messagesDao().insert(userMessage, responseMessage).last()

        val responseMessages = mutableMapOf(0 to responseMessage.copy(id = responseMessageId))
        openAIRepository.generateMessagesStream(chatWithMessages.messages.asReversed() + userMessage).collect { (index, content) ->
            responseMessages.getOrPut(index) {
                responseMessage.copy(id = 0)
            }.copy(
                content = content,
                time = System.currentTimeMillis()
            ).also { message ->
                database.messagesDao().insert(message).first().also { newId ->
                    if (message.id == 0L)
                        responseMessages[index] = message.copy(id = newId)
                    else
                        responseMessages[index] = message
                }
            }
        }

        database.messagesDao().insert(
            responseMessages.values.map {
                it.copy(
                    generating = false,
                    content = it.content?.trim()
                )
            }
        )
    }, errorBlock = {
        database.messagesDao().apply {
            markAllAsNotGeneratingInChat(chatId)
            deleteEmptyMessagesInChat(chatId)
        }
    })

    fun delete(message: ChatMessage) = viewModelScope.onIO {
        database.messagesDao().delete(message)
    }
}