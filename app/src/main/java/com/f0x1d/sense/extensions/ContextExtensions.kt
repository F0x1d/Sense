package com.f0x1d.sense.extensions

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri

fun Context.openLink(url: String) = startActivity(
    Intent(Intent.ACTION_VIEW).setData(Uri.parse(url))
)

fun Context.copyText(text: String) = (getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager).also {
    it.setPrimaryClip(ClipData.newPlainText("Sense", text))
}