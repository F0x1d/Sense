package com.f0x1d.sense.viewmodel

import android.app.Application
import android.media.MediaPlayer
import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.content.FileProvider
import androidx.lifecycle.viewModelScope
import com.f0x1d.sense.BuildConfig
import com.f0x1d.sense.database.AppDatabase
import com.f0x1d.sense.database.entity.GeneratedAudio
import com.f0x1d.sense.repository.network.OpenAIRepository
import com.f0x1d.sense.store.datastore.AudioDataStore
import com.f0x1d.sense.viewmodel.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject

@HiltViewModel
class AudioViewModel @Inject constructor(
    private val openAIRepository: OpenAIRepository,
    private val audioDataStore: AudioDataStore,
    private val database: AppDatabase,
    application: Application
): BaseViewModel(application) {

    val generatedAudios = database.audiosDao().getAll()
        .map { it.asReversed() }
        .distinctUntilChanged()
        .flowOn(Dispatchers.IO)

    val models = listOf(
        "tts-1",
        "tts-1-hd"
    )
    val voices = listOf(
        "alloy",
        "echo",
        "fable",
        "onyx",
        "nova",
        "shimmer"
    )
    val mimeTypes = mapOf(
        "mp3" to "audio/mpeg",
        "opus" to "audio/opus",
        "aac" to "audio/aac",
        "flac" to "audio/flac"
    )
    val formats = mimeTypes.keys.toList()

    var input by mutableStateOf("")
    var model by mutableStateOf("")
    var voice by mutableStateOf("")
    var format by mutableStateOf("")
    var speed by mutableStateOf("")

    var loading by mutableStateOf(false)

    private val audiosDir = File(ctx.filesDir.absolutePath + "/audios").apply {
        if (!exists()) mkdirs()
    }

    init {
        viewModelScope.launch {
            model = audioDataStore.model.first()
            voice = audioDataStore.voice.first()
            format = audioDataStore.format.first()
            speed = audioDataStore.speed.first().toString()
        }
    }

    fun generate() = viewModelScope.onIO({
        audioDataStore.saveModel(model)
        audioDataStore.saveVoice(voice)
        audioDataStore.saveFormat(format)

        val fixedSpeed = speed
            .replace(",", ".")
            .toFloatOrNull()
            ?.coerceIn(0.25f, 4f) ?: 1f

        speed = fixedSpeed.toString()

        audioDataStore.saveSpeed(fixedSpeed)

        loading = true

        openAIRepository.generateAudio(
            model,
            input,
            voice,
            format,
            fixedSpeed
        )?.byteStream()?.use { downloadStream ->
            val audioFile = File(audiosDir, "${System.currentTimeMillis()}")

            FileOutputStream(audioFile).use {
                downloadStream.copyTo(it)
            }

            GeneratedAudio(
                input = input,
                filePath = audioFile.absolutePath,
                mimeType = mimeTypes[format] ?: ""
            ).also {
                database.audiosDao().insert(it)
            }

            loading = false
        }
    }, {
        loading = false
    })

    fun play(audio: GeneratedAudio) {
        val file = File(audio.filePath)

        MediaPlayer.create(
            ctx,
            FileProvider.getUriForFile(ctx, "${BuildConfig.APPLICATION_ID}.provider", file)
        ).apply {
            start()

            setOnCompletionListener {
                it.release()
            }
        }
    }

    fun exportTo(uri: Uri?, audio: GeneratedAudio) = viewModelScope.onIO {
        ctx.contentResolver.openOutputStream(uri ?: return@onIO)?.use { outputStream ->
            File(audio.filePath).inputStream().use { inputStream ->
                inputStream.copyTo(outputStream)
            }
        }
    }

    fun delete(audio: GeneratedAudio) = viewModelScope.onIO {
        File(audio.filePath).delete()
        database.audiosDao().delete(audio)
    }
}