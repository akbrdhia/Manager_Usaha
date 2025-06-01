package com.managerusaha.app.utills.model

data class RiwayatWithBarang(
    val riwayatId: Int,
    val barangId: Int,
    val nama: String,
    val stok: Int,
    val harga: Double,
    val modal: Double,
    val gambarPath: String?,
    val tanggal: Long,
    val jumlah: Int,
    val tipe: String
)
