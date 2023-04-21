package com.f0x1d.sense.extensions

import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.annotation.StringRes

val Context.downloadManager get() = getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager

fun Context.toast(@StringRes stringRes: Int, length: Int = Toast.LENGTH_SHORT) = Toast.makeText(
    this, stringRes, length
).show()

fun Context.openLink(url: String) = startActivity(
    Intent(Intent.ACTION_VIEW).setData(Uri.parse(url))
)