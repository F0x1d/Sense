package com.f0x1d.sense.extensions

import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

suspend fun <T> MutableLiveData<T>.suspendSetValue(value: T?) = withContext(Dispatchers.Main.immediate) {
    setValue(value)
}