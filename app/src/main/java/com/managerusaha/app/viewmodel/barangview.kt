package com.managerusaha.app.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.managerusaha.app.repository.BarangRepository
import com.managerusaha.app.room.entity.Barang
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class BarangViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: BarangRepository = BarangRepository(application)

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

    fun updateStok(barangId: Int, jumlah: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateStok(barangId, jumlah)
        }
    }

    fun getBarangById(barangId: Int): LiveData<Barang> {
        return repository.getBarangById(barangId)
    }

}

