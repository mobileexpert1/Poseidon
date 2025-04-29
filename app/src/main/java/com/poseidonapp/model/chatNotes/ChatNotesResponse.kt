package com.poseidonapp.model.chatNotes

import com.google.gson.annotations.SerializedName

 class ChatNotesResponse(

    @SerializedName("data")
    val dataChatNotes: DataChatNotes,

    @SerializedName("message")
    val message: String,

    @SerializedName("error")
    val error: Error,

    @SerializedName("status")
    val status: Boolean
)