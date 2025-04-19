package com.managerusaha.app.repository

import com.managerusaha.app.room.AppDatabase
import com.managerusaha.app.room.dao.BarangDao
import com.managerusaha.app.room.entity.Barang
import android.app.Application
import androidx.lifecycle.LiveData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class       BarangRepository(application: Application) {
    private val barangDao: BarangDao = AppDatabase.getDatabase(application).barangDao()

    suspend fun insert(barang: Barang) = withContext(Dispatchers.IO) {
        barangDao.insert(barang)
    }

    suspend fun update(barang: Barang) = withContext(Dispatchers.IO) {
        barangDao.update(barang)
    }

    suspend fun delete(barang: Barang) = withContext(Dispatchers.IO) {
        barangDao.delete(barang)
    }

    fun getAllBarang() = barangDao.getAllBarang()

    fun getBarangByKategori(kategoriId: Int) = barangDao.getBarangByKategori(kategoriId)

    fun searchBarang(searchQuery: String) = barangDao.searchBarang(searchQuery)

    fun getBarangStokRendah(batasStok: Int) = barangDao.getBarangStokRendah(batasStok)

    fun getBarangByRangeHarga(minHarga: Double, maxHarga: Double) = 
        barangDao.getBarangByRangeHarga(minHarga, maxHarga)

    suspend fun updateStok(barangId: Int, jumlah: Int) = withContext(Dispatchers.IO) {
        barangDao.updateStok(barangId, jumlah)
    }


    fun getBarangById(barangId: Int): LiveData<Barang> {
        return barangDao.getBarangById(barangId)
    }


} 