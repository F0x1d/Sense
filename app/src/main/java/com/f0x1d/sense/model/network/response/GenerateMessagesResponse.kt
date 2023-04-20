package com.f0x1d.sense.model.network.response

import androidx.annotation.Keep
import com.f0x1d.sense.database.entity.ChatMessage
import com.google.gson.annotations.SerializedName

@Keep
data class GenerateMessagesResponse(
    @SerializedName("choices") val choices: List<MessageChoice>
)

@Keep
data class MessageChoice(
    @SerializedName("message") val message: ChatMessage
)