package com.f0x1d.sense.viewmodel

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.f0x1d.sense.store.datastore.SettingsDataStore
import com.f0x1d.sense.viewmodel.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
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

    val apiKey = MutableLiveData("")
    val model = MutableLiveData("")

    private val fillings = mutableMapOf<MutableLiveData<String>, suspend () -> String?>()
    private val mutexes = mutableMapOf<MutableLiveData<String>, Mutex>()
    private val actions = mutableMapOf<MutableLiveData<String>, suspend (String) -> Unit>()

    init {
        fillings[apiKey] = { settingsDataStore.apiKey.first() }
        fillings[model] = { settingsDataStore.model.first() }

        actions[apiKey] = { settingsDataStore.saveApiKey(it) }
        actions[model] = { settingsDataStore.saveModel(it) }

        viewModelScope.launch {
            fillings.entries.map {
                async {
                    LiveDataWithValue(it.key, it.value.invoke())
                }
            }.awaitAll().forEach {
                it.liveData.value = it.value
            }
        }
    }

    fun updateFor(liveData: MutableLiveData<String>, value: String) {
        liveData.value = value

        viewModelScope.launch {
            mutexes.getOrPut(liveData) { Mutex() }.withLock {
                actions[liveData]?.invoke(value)
            }
        }
    }

    private data class LiveDataWithValue(
        val liveData: MutableLiveData<String>,
        val value: String?
    )
}