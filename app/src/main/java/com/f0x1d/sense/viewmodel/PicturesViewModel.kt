package com.f0x1d.sense.viewmodel

import android.app.Application
import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import coil.ImageLoader
import coil.annotation.ExperimentalCoilApi
import com.f0x1d.sense.database.AppDatabase
import com.f0x1d.sense.database.entity.GeneratedImage
import com.f0x1d.sense.repository.network.OpenAIRepository
import com.f0x1d.sense.viewmodel.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class PicturesViewModel @Inject constructor(
    application: Application,
    private val openAIRepository: OpenAIRepository,
    private val database: AppDatabase,
    val imageLoader: ImageLoader
): BaseViewModel(application) {

    var query by mutableStateOf("")
    var loading by mutableStateOf(false)

    val generatedImages = database.imagesDao().getAll().map {
        it.asReversed()
    }
        .flowOn(Dispatchers.IO)
        .distinctUntilChanged()

    fun generate() = viewModelScope.onIO({
        query.trim().also { query ->
            if (query.isEmpty()) return@onIO

            withContext(Dispatchers.Main) {
                loading = true
            }
            val imageUrl = openAIRepository.generateImage(query)

            withContext(Dispatchers.Main) {
                loading = false
            }
            database.imagesDao().insert(GeneratedImage(query, imageUrl))
        }
    }, errorBlock = { loading = false })

    @OptIn(ExperimentalCoilApi::class)
    fun save(url: String?, uri: Uri?) = viewModelScope.onIO {
        imageLoader.diskCache?.get(url ?: return@onIO)?.use { snapshot ->
            snapshot.data.toFile().inputStream().use { inputStream ->
                ctx.contentResolver.openOutputStream(uri ?: return@onIO)?.use { outputStream ->
                    inputStream.copyTo(outputStream)
                }
            }
        }
    }

    fun delete(image: GeneratedImage) = viewModelScope.onIO {
        database.imagesDao().delete(image)
    }
}