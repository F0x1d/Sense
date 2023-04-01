package com.f0x1d.sense.model.network.response.base

import com.google.gson.annotations.SerializedName

data class ErrorResponse(
    @SerializedName("error") val error: OpenAIError
)

data class OpenAIError(
    @SerializedName("message") val message: String
)
