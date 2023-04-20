package com.f0x1d.sense.viewmodel.base

import android.app.Application
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import com.f0x1d.sense.model.network.response.base.ErrorResponse
import com.google.gson.Gson
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.*
import retrofit2.HttpException

abstract class BaseViewModel(application: Application): AndroidViewModel(application) {

    protected val ctx get() = getApplication<Application>()

    private val gson get() = EntryPointAccessors.fromApplication(ctx, BaseViewModelEntryPoint::class.java).gson()

    protected fun CoroutineScope.onIO(
        block: suspend CoroutineScope.() -> Unit
    ) = onIO(block) {}

    protected fun CoroutineScope.onIO(
        block: suspend CoroutineScope.() -> Unit,
        error: suspend CoroutineScope.() -> Unit
    ) = launch(Dispatchers.IO) {
        try {
            coroutineScope {
                block.invoke(this)
            }
        } catch (e: Exception) {
            e.printStackTrace()

            handleNetworkError(e)
            error.invoke(this)
        }
    }

    private suspend fun handleNetworkError(e: Exception) {
        if (e is HttpException) {
            e.response()?.errorBody()?.apply {
                gson.fromJson(string(), ErrorResponse::class.java).error.message.let { error ->
                    withContext(Dispatchers.Main) {
                        Toast.makeText(ctx, error, Toast.LENGTH_LONG).show()
                    }
                }
            }?.close()
        } else {
            withContext(Dispatchers.Main) {
                Toast.makeText(ctx, "Error: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
            }
        }
    }
}

@EntryPoint
@InstallIn(SingletonComponent::class)
interface BaseViewModelEntryPoint {
    fun gson(): Gson
}