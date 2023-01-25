package com.study.apptoko.response.itemTransaksi

data class ItemTransaksiResponsePost(
    val `data`: Data,
    val message: String,
    val success: Boolean
)