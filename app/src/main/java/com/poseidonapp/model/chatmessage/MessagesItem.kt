package com.poseidonapp.model.chatmessage

import com.google.gson.annotations.SerializedName

 class MessagesItem(

    @SerializedName("userNoteId")
    val userNoteId: String,

    @SerializedName("addedOnTimeStamp")
    val addedOnTimeStamp: Long,

    @SerializedName("id")
    val id: String,

    @SerializedName("text")
    val text: String,

    @SerializedName("userId")
    val userId: String,

    @SerializedName("addedOn")
    val addedOn: String
)