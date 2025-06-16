package com.managerusaha.app.viewmodel

import androidx.lifecycle.ViewModel

class BarangFormViewModel : ViewModel() {
    var nama: String = ""
    var stok: String = ""
    var hargaJual: String = ""
    var hargaModal: String = ""
    var kategori: String = ""
    var barcode: String = ""
    var gambarPath: String? = null
}
