package com.f0x1d.sense.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.f0x1d.sense.database.entity.GeneratedImage
import kotlinx.coroutines.flow.Flow

@Dao
interface GeneratedImagesDao {

    @Query("SELECT * FROM GeneratedImage")
    fun getAll(): Flow<List<GeneratedImage>>

    @Insert
    suspend fun insert(image: GeneratedImage)
}