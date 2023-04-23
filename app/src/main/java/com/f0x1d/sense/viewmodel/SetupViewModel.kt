package com.f0x1d.sense.viewmodel

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import com.f0x1d.sense.store.datastore.SettingsDataStore
import com.f0x1d.sense.viewmodel.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SetupViewModel @Inject constructor(
    application: Application,
    private val settingsDataStore: SettingsDataStore
): BaseViewModel(application) {

    var apiKey by mutableStateOf("")

    fun saveApiKey() = viewModelScope.launch {
        settingsDataStore.saveApiKey(apiKey)
    }
}