package com.f0x1d.sense.store.datastore

import android.content.Context
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.f0x1d.sense.store.datastore.base.BaseDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AudioDataStore @Inject constructor(
    @ApplicationContext context: Context
): BaseDataStore(context, DATA_STORE_NAME) {

    companion object {
        const val DATA_STORE_NAME = "audio_data"

        val MODEL_KEY = stringPreferencesKey("model")
        val VOICE_KEY = stringPreferencesKey("voice")
        val FORMAT_KEY = stringPreferencesKey("format")
        val SPEED_KEY = floatPreferencesKey("speed")
    }

    val model = getAsFlow(MODEL_KEY).map {
        it ?: "tts-1"
    }
    val voice = getAsFlow(VOICE_KEY).map {
        it ?: "alloy"
    }
    val format = getAsFlow(FORMAT_KEY).map {
        it ?: "mp3"
    }
    val speed = getAsFlow(SPEED_KEY).map {
        it ?: 1f
    }

    suspend fun saveModel(model: String?) = save(MODEL_KEY, model)
    suspend fun saveVoice(voice: String?) = save(VOICE_KEY, voice)
    suspend fun saveFormat(format: String?) = save(FORMAT_KEY, format)
    suspend fun saveSpeed(speed: Float?) = save(SPEED_KEY, speed)
}