package com.f0x1d.sense.viewmodel

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import com.f0x1d.sense.database.AppDatabase
import com.f0x1d.sense.database.entity.Chat
import com.f0x1d.sense.viewmodel.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class ChatsViewModel @Inject constructor(
    application: Application,
    private val database: AppDatabase
): BaseViewModel(application) {

    var infoDialogOpened by mutableStateOf(false)

    val chatsWithMessages = database.chatsDao().getAll().map {
        it.sortedByDescending { chatWithMessages ->
            chatWithMessages.messages.lastOrNull()?.time ?: chatWithMessages.chat.createdTime
        }
    }.flowOn(Dispatchers.IO)

    fun createChat(onCreated: (Chat) -> Unit) = viewModelScope.onIO {
        val chat = Chat().run {
            copy(
                id = database.chatsDao().insert(this)
            )
        }

        withContext(Dispatchers.Main) {
            onCreated(chat)
        }
    }

    fun deleteChat(chat: Chat) = viewModelScope.onIO {
        database.chatsDao().delete(chat)
    }
}