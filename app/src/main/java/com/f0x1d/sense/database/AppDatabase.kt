package com.f0x1d.sense.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import com.f0x1d.sense.database.dao.ChatsDao
import com.f0x1d.sense.database.dao.MessagesDao
import com.f0x1d.sense.database.entity.Chat
import com.f0x1d.sense.database.entity.ChatMessage

@Database(entities = [Chat::class, ChatMessage::class], version = 2)
abstract class AppDatabase: RoomDatabase() {

    companion object {
        val MIGRATION_1_2 = Migration(1, 2) { database ->
            database.execSQL("ALTER TABLE ChatMessage ADD COLUMN generating INTEGER NOT NULL DEFAULT 0")
        }
    }
    abstract fun chatsDao(): ChatsDao
    abstract fun messagesDao(): MessagesDao
}