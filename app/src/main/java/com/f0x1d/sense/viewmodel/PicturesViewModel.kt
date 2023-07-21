package com.f0x1d.sense.viewmodel

import android.app.Application
import android.app.DownloadManager
import android.net.Uri
import android.os.Environment
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import coil.ImageLoader
import com.f0x1d.sense.R
import com.f0x1d.sense.database.AppDatabase
import com.f0x1d.sense.database.entity.GeneratedImage
import com.f0x1d.sense.extensions.downloadManager
import com.f0x1d.sense.extensions.toast
import com.f0x1d.sense.repository.network.OpenAIRepository
import com.f0x1d.sense.viewmodel.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
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
    }.distinctUntilChanged()

    fun generate() = viewModelScope.onIO({
        query.trim().also { query ->
            if (query.isEmpty()) return@onIO

            loading = true
            val imageUrl = openAIRepository.generateImage(query)
            loading = false

            database.imagesDao().insert(GeneratedImage(query, imageUrl))
        }
    }) { loading = false }

    fun download(url: String?) {
        val request = DownloadManager.Request(Uri.parse(url))
        request.setTitle(ctx.getString(R.string.picture))
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_PICTURES, "Sense/image-${System.currentTimeMillis()}.png")

        ctx.downloadManager.enqueue(request)
        ctx.toast(R.string.download_started)
    }

    fun delete(image: GeneratedImage) = viewModelScope.onIO {
        database.imagesDao().delete(image)
    }
}