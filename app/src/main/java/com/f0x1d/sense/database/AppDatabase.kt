package com.f0x1d.sense.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.f0x1d.sense.database.dao.ChatsDao
import com.f0x1d.sense.database.dao.MessagesDao
import com.f0x1d.sense.database.entity.Chat
import com.f0x1d.sense.database.entity.ChatMessage

@Database(entities = [Chat::class, ChatMessage::class], version = 1)
abstract class AppDatabase: RoomDatabase() {
    abstract fun chatsDao(): ChatsDao
    abstract fun messagesDao(): MessagesDao
}