package com.f0x1d.sense.database.entity

import androidx.annotation.Keep
import androidx.room.*
import com.f0x1d.sense.di.GsonSkip

@Keep
@Entity
data class Chat(
    @ColumnInfo(name = "title") val title: String? = null,
    @ColumnInfo(name = "created_time") val createdTime: Long = System.currentTimeMillis(),
    @ColumnInfo(name = "id") @PrimaryKey(autoGenerate = true) val id: Long = 0
)

@Keep
@Entity(
    foreignKeys = [
        ForeignKey(
            entity = Chat::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("chat_id"),
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class ChatMessage(
    @ColumnInfo(name = "content") val content: String? = null,
    @ColumnInfo(name = "generating") @GsonSkip val generating: Boolean = false,
    @ColumnInfo(name = "time") @GsonSkip val time: Long = System.currentTimeMillis(),
    @ColumnInfo(name = "role") val role: String? = null,
    @ColumnInfo(name = "chat_id", index = true) @GsonSkip val chatId: Long? = 0,
    @ColumnInfo(name = "message_id") @PrimaryKey(autoGenerate = true) @GsonSkip val id: Long = 0
) {
    val fromUser get() = role == "user"
    val fromChatGPT get() = role == "assistant"

    val parsedRole get() = if (fromUser)
        com.f0x1d.sense.R.string.you
    else
        com.f0x1d.sense.R.string.chatgpt
}

data class ChatWithMessages(
    @Embedded val chat: Chat,
    @Relation(
        parentColumn = "id",
        entityColumn = "chat_id"
    ) val messages: List<ChatMessage> = emptyList(),
)