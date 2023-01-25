package com.study.apptoko.response.nota

data class ItemTransaksi(
    val harga_saat_transaksi: String,
    val id: String,
    val nama: String,
    val produk_id: String,
    val qty: String,
    val sub_total: String,
    val transaksi_id: String
)