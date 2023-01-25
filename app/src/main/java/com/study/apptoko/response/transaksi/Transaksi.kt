package com.study.apptoko.response.transaksi

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Transaksi(
    val admin_id: String,
    val id: String,
    val tanggal: String,
    val total: String
):Parcelable