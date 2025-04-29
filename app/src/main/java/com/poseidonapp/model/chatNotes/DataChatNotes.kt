package com.poseidonapp.model.chatNotes

import com.google.gson.annotations.SerializedName

 class DataChatNotes(

    @SerializedName("notesList")
    val notesList: ArrayList<NotesListItem>
)