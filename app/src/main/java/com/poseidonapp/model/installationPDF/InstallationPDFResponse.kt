package com.poseidonapp.model.installationPDF

import com.google.gson.annotations.SerializedName

 class InstallationPDFResponse(

    @SerializedName("data")
    val dataInstallationPDF: DataInstallationPDF,

    @SerializedName("message")
    val message: String,

    @SerializedName("error")
    val error: Error,

    @SerializedName("status")
    val status: Boolean
)