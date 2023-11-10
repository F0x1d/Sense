package com.f0x1d.sense.model.network.request

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class GenerateAudioRequestBody(
    @SerializedName("model") val model: String,
    @SerializedName("input") val input: String,
    @SerializedName("voice") val voice: String,
    @SerializedName("response_format") val responseFormat: String = "mp3",
    @SerializedName("speed") val speed: Float = 1f
)
