package com.f0x1d.sense.store.datastore.base

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.map

abstract class BaseDataStore(
    private val context: Context,
    private val dataStoreName: String
) {
    protected val Context.dataStore by preferencesDataStore(dataStoreName)

    protected fun <T> getAsFlow(key: Preferences.Key<T>) = context.dataStore.data.map {
        it[key]
    }

    protected suspend fun <T> save(key: Preferences.Key<T>, value: T?) = context.dataStore.edit {
        if (value != null)
            it[key] = value
        else
            it.remove(key)
    }
}