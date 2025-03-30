package com.managerusaha.app.room.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "riwayat")
data class Riwayat(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val barangId: Int,
    val tanggal: Long,
    val jumlah: Int,
    val tipe: String //enum (masuk/keluar)
)
