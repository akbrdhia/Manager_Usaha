package com.managerusaha.app.utills.model

import com.managerusaha.app.room.entity.Barang

data class KategoryExpand(
    val nama: String,
    val barangList: List<Barang>,
    var isExpanded: Boolean = false
)