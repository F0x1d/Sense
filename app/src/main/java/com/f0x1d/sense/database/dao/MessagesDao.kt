package com.f0x1d.sense.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.f0x1d.sense.database.entity.ChatMessage

@Dao
interface MessagesDao {

    @Query("SELECT COUNT(*) FROM ChatMessage WHERE generating = 1 AND chat_id = :chatId")
    suspend fun countGeneratingMessagesInChat(chatId: Long): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(vararg message: ChatMessage): List<Long>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(messages: List<ChatMessage>)

    @Query("UPDATE ChatMessage SET generating = 0 WHERE generating = 1 AND chat_id = :chatId")
    suspend fun markAllAsNotGeneratingInChat(chatId: Long)

    @Query("DELETE FROM ChatMessage WHERE content is NULL AND chat_id = :chatId")
    suspend fun deleteEmptyMessagesInChat(chatId: Long)

    @Query("UPDATE ChatMessage SET generating = 0 WHERE generating = 1")
    suspend fun markAllAsNotGenerating()

    @Query("DELETE FROM ChatMessage WHERE content is NULL")
    suspend fun deleteEmptyMessages()

    @Delete
    suspend fun delete(vararg messages: ChatMessage)
}