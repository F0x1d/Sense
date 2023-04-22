package com.f0x1d.sense.viewmodel

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.f0x1d.sense.OpenAIApplication
import com.f0x1d.sense.database.AppDatabase
import com.f0x1d.sense.database.entity.ChatMessage
import com.f0x1d.sense.database.entity.ChatWithMessages
import com.f0x1d.sense.extensions.suspendSetValue
import com.f0x1d.sense.repository.network.OpenAIRepository
import com.f0x1d.sense.viewmodel.base.BaseViewModel
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.map

class ChatViewModel @AssistedInject constructor(
    application: Application,
    private val database: AppDatabase,
    private val openAIRepository: OpenAIRepository,
    @Assisted private val chatId: Long
): BaseViewModel(application) {

    companion object {
        fun provideFactory(assistedFactory: ChatViewModelAssistedFactory, chatId: Long) = viewModelFactory {
            initializer {
                assistedFactory.create(chatId)
            }
        }
    }

    val chatWithMessages = database.chatsDao().getById(chatId).map {
        it.copy(messages = it.messages.asReversed())
    }

    val text = MutableLiveData("")
    val addingMyMessage = MutableLiveData(false)

    fun send(chatWithMessages: ChatWithMessages) = OpenAIApplication.applicationScope.onIO({
        if (database.messagesDao().countGeneratingMessagesInChat(chatId) > 0) return@onIO

        val messageText = text.value?.trim()
        if (messageText?.isEmpty() == true) return@onIO

        addingMyMessage.suspendSetValue(true)

        val userMessage = ChatMessage(
            content = messageText,
            role = "user",
            chatId = chatId
        )
        text.postValue("")

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
            responseMessages.values.map { it.copy(generating = false) }
        )
    }) {
        database.messagesDao().deleteGeneratingMessages()
    }

    fun addedMyMessage() {
        addingMyMessage.value = false
    }

    fun updateText(text: String) {
        this.text.value = text
    }
}

@AssistedFactory
interface ChatViewModelAssistedFactory {
    fun create(@Assisted chatId: Long): ChatViewModel
}