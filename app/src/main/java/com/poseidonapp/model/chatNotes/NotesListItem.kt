package com.poseidonapp.model.chatNotes

import com.google.gson.annotations.SerializedName

 class NotesListItem(

    @SerializedName("note")
    val note: String,

    @SerializedName("createdAt")
    val createdAt: String,

    @SerializedName("subject")
    val subject: String,

    @SerializedName("id")
    val id: String,

    @SerializedName("userId")
    val userId: String,

    @SerializedName("updatedAt")
    val updatedAt: String
)