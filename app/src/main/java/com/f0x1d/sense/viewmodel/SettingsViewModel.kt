package com.f0x1d.sense.viewmodel

import android.app.Application
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.viewModelScope
import com.f0x1d.sense.store.datastore.SettingsDataStore
import com.f0x1d.sense.viewmodel.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    application: Application,
    private val settingsDataStore: SettingsDataStore
): BaseViewModel(application) {

    val apiKey = mutableStateOf("")
    val model = mutableStateOf("")

    private val fillings = mutableMapOf<MutableState<String>, suspend () -> String?>()
    private val mutexes = mutableMapOf<MutableState<String>, Mutex>()
    private val actions = mutableMapOf<MutableState<String>, suspend (String) -> Unit>()

    init {
        fillings[apiKey] = { settingsDataStore.apiKey.first() }
        fillings[model] = { settingsDataStore.model.first() }

        actions[apiKey] = { settingsDataStore.saveApiKey(it) }
        actions[model] = { settingsDataStore.saveModel(it) }

        viewModelScope.launch {
            fillings.entries.map {
                async {
                    StateWithValue(it.key, it.value.invoke())
                }
            }.awaitAll().forEach {
                it.state.value = (it.value) ?: ""
            }
        }
    }

    fun updateFor(state: MutableState<String>, value: String) {
        state.value = value

        viewModelScope.launch(Dispatchers.Default) {
            mutexes.getOrPut(state) { Mutex() }.withLock {
                actions[state]?.invoke(value)
            }
        }
    }

    private data class StateWithValue(
        val state: MutableState<String>,
        val value: String?
    )
}