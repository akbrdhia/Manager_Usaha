package com.managerusaha.app.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.managerusaha.app.repository.BarangRepository
import com.managerusaha.app.room.AppDatabase
import com.managerusaha.app.room.entity.Barang
import com.managerusaha.app.room.entity.Riwayat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Date

class BarangViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: BarangRepository = BarangRepository(application)
    private val riwayatDao = AppDatabase.getDatabase(application).riwayatDao()

    val allBarang: LiveData<List<Barang>> = repository.getAllBarang()

    fun getBarangByKategori(kategoriId: Int): LiveData<List<Barang>> {
        return repository.getBarangByKategori(kategoriId)
    }

    fun searchBarang(searchQuery: String): LiveData<List<Barang>> {
        return repository.searchBarang(searchQuery)
    }

    fun getBarangStokRendah(batasStok: Int): LiveData<List<Barang>> {
        return repository.getBarangStokRendah(batasStok)
    }

    fun getBarangByRangeHarga(minHarga: Double, maxHarga: Double): LiveData<List<Barang>> {
        return repository.getBarangByRangeHarga(minHarga, maxHarga)
    }

    fun insertBarang(barang: Barang) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.insert(barang)
        }
    }

    fun updateBarang(barang: Barang) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.update(barang)
        }
    }

    fun deleteBarang(barang: Barang) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.delete(barang)
        }
    }

    fun kurangStok(barangId: Int, jumlah: Int, tanggal: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.kurangStok(barangId, jumlah)
            val riwayat = Riwayat(
                barangId = barangId,
                jumlah = jumlah,
                tanggal = tanggal,
                tipe = "KELUAR"
            )
            riwayatDao.insert(riwayat)
        }
    }


    fun tambahStok(barangId: Int, jumlah: Int, tanggal: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.tambahStok(barangId, jumlah)
            val riwayat = Riwayat(
                barangId = barangId,
                jumlah = jumlah,
                tanggal = tanggal,
                tipe = "MASUK"
            )
            riwayatDao.insert(riwayat)
        }
    }

    fun getBarangById(barangId: Int): LiveData<Barang> {
        return repository.getBarangById(barangId)
    }
    /** lookup barcode via callback */
    fun findByBarcode(code: String, onResult: (Barang?) -> Unit) {
        viewModelScope.launch {
            // pindah ke IO, lalu kembali ke Main thread
            val b = withContext(Dispatchers.IO) {
                repository.findByBarcode(code)
            }
            onResult(b)
        }
    }

}

