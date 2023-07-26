package com.f0x1d.sense.extensions

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.annotation.StringRes

fun Context.toast(@StringRes stringRes: Int, length: Int = Toast.LENGTH_SHORT) = Toast.makeText(
    this, stringRes, length
).show()

fun Context.openLink(url: String) = startActivity(
    Intent(Intent.ACTION_VIEW).setData(Uri.parse(url))
)

fun Context.copyText(text: String) = (getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager).also {
    it.setPrimaryClip(ClipData.newPlainText("Sense", text))
}