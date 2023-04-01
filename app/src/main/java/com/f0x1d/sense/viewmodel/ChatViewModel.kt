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
        if (database.messagesDao().countEmptyMessages() > 0) return@onIO

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
            chatId = chatId
        )
        val responseMessageId = database.messagesDao().insert(userMessage, responseMessage).last()

        openAIRepository.generateMessages(chatWithMessages.messages.asReversed() + userMessage).mapIndexed { index, message ->
            val content = message.content?.trim()

            if (index == 0) responseMessage.copy(
                content = content,
                time = System.currentTimeMillis(),
                id = responseMessageId
            ) else message.copy(
                content = content,
                chatId = chatId
            )
        }.also {
            database.messagesDao().insert(it)
        }
    }) {
        database.messagesDao().deleteEmptyMessages()
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