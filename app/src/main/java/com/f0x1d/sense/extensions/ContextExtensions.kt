package com.f0x1d.sense.extensions

import android.app.DownloadManager
import android.content.Context
import android.widget.Toast
import androidx.annotation.StringRes

val Context.downloadManager get() = getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager

fun Context.toast(@StringRes stringRes: Int, length: Int = Toast.LENGTH_SHORT) = Toast.makeText(
    this, stringRes, length
)