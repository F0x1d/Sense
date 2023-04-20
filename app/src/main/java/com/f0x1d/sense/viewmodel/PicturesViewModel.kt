package com.f0x1d.sense.viewmodel

import android.app.Application
import android.app.DownloadManager
import android.net.Uri
import android.os.Environment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.f0x1d.sense.R
import com.f0x1d.sense.extensions.downloadManager
import com.f0x1d.sense.extensions.suspendSetValue
import com.f0x1d.sense.extensions.toast
import com.f0x1d.sense.repository.network.OpenAIRepository
import com.f0x1d.sense.viewmodel.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class PicturesViewModel @Inject constructor(
    application: Application,
    private val openAIRepository: OpenAIRepository
): BaseViewModel(application) {

    val query = MutableLiveData("")

    val loading = MutableLiveData<Boolean>()
    val pictureUrl = MutableLiveData<String?>()

    fun generate() = viewModelScope.onIO {
        query.value!!.trim().also { query ->
            if (query.isEmpty()) return@onIO

            loading.suspendSetValue(true)
            val imageUrl = openAIRepository.generateImage(query)
            loading.suspendSetValue(false)

            pictureUrl.suspendSetValue(imageUrl)
        }
    }

    fun download() {
        val request = DownloadManager.Request(Uri.parse(pictureUrl.value!!))
        request.setTitle(ctx.getString(R.string.picture))
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_PICTURES, "Sense/image-${System.currentTimeMillis()}.png")

        ctx.downloadManager.enqueue(request)
        ctx.toast(R.string.download_started)
    }

    fun updateQuery(query: String) {
        this.query.value = query
    }
}