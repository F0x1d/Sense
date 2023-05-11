package com.f0x1d.sense.store.datastore

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.f0x1d.sense.store.datastore.base.BaseDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SettingsDataStore @Inject constructor(@ApplicationContext context: Context): BaseDataStore(context, DATA_STORE_NAME) {

    companion object {
        const val DATA_STORE_NAME = "settings_data"

        const val THEME = "theme"
        const val AMOLED = "amoled"

        const val API_KEY = "api_key"
        const val MODEL = "model"

        val THEME_KEY = intPreferencesKey(THEME)
        val AMOLED_KEY = booleanPreferencesKey(AMOLED)

        val API_KEY_KEY = stringPreferencesKey(API_KEY)
        val MODEL_KEY = stringPreferencesKey(MODEL)
    }

    val theme = getAsFlow(THEME_KEY).map {
        it ?: 0
    }
    val amoled = getAsFlow(AMOLED_KEY).map {
        it ?: false
    }

    val apiKey = getAsFlow(API_KEY_KEY)
    val model = getAsFlow(MODEL_KEY).map {
        it ?: "gpt-3.5-turbo"
    }

    suspend fun saveTheme(theme: Int) = save(THEME_KEY, theme)
    suspend fun saveAmoled(amoled: Boolean) = save(AMOLED_KEY, amoled)

    suspend fun saveApiKey(apiKey: String?) = save(API_KEY_KEY, apiKey)
    suspend fun saveModel(model: String?) = save(MODEL_KEY, model)
}