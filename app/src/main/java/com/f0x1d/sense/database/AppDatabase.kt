package com.f0x1d.sense.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import com.f0x1d.sense.database.dao.ChatsDao
import com.f0x1d.sense.database.dao.GeneratedImagesDao
import com.f0x1d.sense.database.dao.MessagesDao
import com.f0x1d.sense.database.entity.Chat
import com.f0x1d.sense.database.entity.ChatMessage
import com.f0x1d.sense.database.entity.GeneratedImage

@Database(entities = [Chat::class, ChatMessage::class, GeneratedImage::class], version = 3)
abstract class AppDatabase: RoomDatabase() {

    companion object {
        val MIGRATION_1_2 = Migration(1, 2) { database ->
            database.execSQL("ALTER TABLE ChatMessage ADD COLUMN generating INTEGER NOT NULL DEFAULT 0")
        }
        val MIGRATION_2_3 = Migration(2, 3) { database ->
            database.execSQL("CREATE TABLE GeneratedImage(id INTEGER PRIMARY KEY ASC AUTOINCREMENT NOT NULL, prompt TEXT, url TEXT)")
        }
    }
    abstract fun chatsDao(): ChatsDao
    abstract fun messagesDao(): MessagesDao
    abstract fun imagesDao(): GeneratedImagesDao
}