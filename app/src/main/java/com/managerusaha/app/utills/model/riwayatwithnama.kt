package com.managerusaha.app.utills.model

data class RiwayatWithNama(
    val id: Int,
    val barangId: Int,
    val namaBarang: String,
    val tanggal: Long,
    val jumlah: Int,
    val tipe: String
)