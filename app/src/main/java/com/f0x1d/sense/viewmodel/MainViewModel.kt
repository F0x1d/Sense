package com.f0x1d.sense.viewmodel

import android.app.Application
import com.f0x1d.sense.store.datastore.SettingsDataStore
import com.f0x1d.sense.viewmodel.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    application: Application,
    private val settingsDataStore: SettingsDataStore
): BaseViewModel(application) {
    val shouldOpenSetup = settingsDataStore.apiKey
        .map { it == null }
        .distinctUntilChanged()
        .flowOn(Dispatchers.IO)
}