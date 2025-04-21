package com.managerusaha.app.utills.model

data class RiwayatDisplay(
    val id: Int,
    val namaBarang: String,
    val tanggal: Long,
    val jumlah: Int,
    val tipe: String,
    val harga: Double
)