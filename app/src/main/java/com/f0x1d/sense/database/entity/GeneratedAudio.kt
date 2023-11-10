package com.f0x1d.sense.database.entity

import androidx.annotation.Keep
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Keep
@Entity
data class GeneratedAudio(
    @ColumnInfo(name = "input") val input: String,
    @ColumnInfo(name = "file_path") val filePath: String,
    @ColumnInfo(name = "file_mime_type") val mimeType: String,
    @ColumnInfo(name = "id") @PrimaryKey(autoGenerate = true) val id: Long = 0
)
