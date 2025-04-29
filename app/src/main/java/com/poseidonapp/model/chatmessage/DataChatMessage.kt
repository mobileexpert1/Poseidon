package com.poseidonapp.model.chatmessage

import com.google.gson.annotations.SerializedName

 class DataChatMessage(

    @SerializedName("messages")
    val messages: ArrayList<MessagesItem>
)