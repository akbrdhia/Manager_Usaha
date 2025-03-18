package com.managerusaha.app.room.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "barang")
data class Barang(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val nama: String,
    val kategoriId: Int,
    val stok: Int,
    val harga: Double,
    val modal: Double,
    val gambarPath: String? = null
)
