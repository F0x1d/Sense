package com.f0x1d.sense.viewmodel.base

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import com.f0x1d.sense.model.network.response.base.ErrorResponse
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import retrofit2.HttpException
import javax.inject.Inject

abstract class BaseViewModel(application: Application): AndroidViewModel(application) {

    @Inject
    lateinit var gson: Gson

    var error by mutableStateOf<String?>(null)

    protected val ctx get() = getApplication<Application>()

    protected fun CoroutineScope.onIO(
        block: suspend CoroutineScope.() -> Unit
    ) = onIO(block, errorBlock = {})

    protected fun CoroutineScope.onIO(
        block: suspend CoroutineScope.() -> Unit,
        errorBlock: suspend CoroutineScope.() -> Unit
    ) = launch(Dispatchers.IO) {
        try {
            coroutineScope {
                block.invoke(this)
            }
        } catch (e: Exception) {
            e.printStackTrace()

            handleNetworkError(e)
            errorBlock.invoke(this)
        }
    }

    private suspend fun handleNetworkError(e: Exception) {
        if (e is HttpException) {
            e.response()?.errorBody()?.apply {
                gson.fromJson(string(), ErrorResponse::class.java).error.message.let {
                    error = it
                }
            }
        } else {
            error = e.localizedMessage
        }
    }
}