package com.poseidonapp.model.chatmessage

import com.google.gson.annotations.SerializedName

 class ChatMessageResponse(

    @SerializedName("message")
    val message: String,

    @SerializedName("error")
    val error: Error,

    @SerializedName("data")
    val dataChatMessage: DataChatMessage,

    @SerializedName("status")
    val status: Boolean
)