package com.poseidonapp.model.addchat

import com.google.gson.annotations.SerializedName

 class AddChatResponse(

    @SerializedName("data")
    val dataAddChat: DataAddChat,

    @SerializedName("message")
    val message: String,

    @SerializedName("error")
    val error: Error,

    @SerializedName("status")
    val status: Boolean
)