package com.f0x1d.sense.model.network.response

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class GenerateImageResponse(
    @SerializedName("data") val data: List<ImageGeneration>
)

@Keep
data class ImageGeneration(
    @SerializedName("url") val url: String
)