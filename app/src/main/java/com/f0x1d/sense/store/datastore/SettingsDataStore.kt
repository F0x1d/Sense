package com.f0x1d.sense.store.datastore

import android.content.Context
import androidx.datastore.preferences.core.stringPreferencesKey
import com.f0x1d.sense.SenseApplication
import com.f0x1d.sense.store.datastore.base.BaseDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SettingsDataStore @Inject constructor(@ApplicationContext context: Context): BaseDataStore(context, DATA_STORE_NAME) {

    companion object {
        const val DATA_STORE_NAME = "settings_data"

        val ENDPOINT_KEY = stringPreferencesKey("endpoint")
        val API_KEY_KEY = stringPreferencesKey("api_key")
        val MODEL_KEY = stringPreferencesKey("model")
    }

    val endpoint = getAsFlow(ENDPOINT_KEY).map {
        it ?: SenseApplication.API_ENDPOINT
    }
    val apiKey = getAsFlow(API_KEY_KEY)
    val model = getAsFlow(MODEL_KEY).map {
        it ?: "gpt-3.5-turbo"
    }

    suspend fun saveEndpoint(endpoint: String?) = save(ENDPOINT_KEY, endpoint)
    suspend fun saveApiKey(apiKey: String?) = save(API_KEY_KEY, apiKey)
    suspend fun saveModel(model: String?) = save(MODEL_KEY, model)
}