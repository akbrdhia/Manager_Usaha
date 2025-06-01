package com.managerusaha.app.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.managerusaha.app.room.AppDatabase
import com.managerusaha.app.room.entity.Riwayat
import com.managerusaha.app.utills.model.RiwayatWithBarang
import kotlinx.coroutines.launch

class riwayatViewModel(application: Application) : AndroidViewModel(application) {
    private val riwayatDao = AppDatabase.getDatabase(application).riwayatDao()

    val allriwayat: LiveData<List<Riwayat>> = riwayatDao.getAllRiwayat()

    fun getRiwayatByBarang(barangId: Int): LiveData<List<Riwayat>> {
        return riwayatDao.getRiwayatByBarang(barangId)
    }

    fun insertRiwayat(riwayat: Riwayat) {
        viewModelScope.launch {
            riwayatDao.insert(riwayat)
        }
    }

    val allRiwayatWithBarang: LiveData<List<RiwayatWithBarang>> = riwayatDao.getRiwayatWithBarang()
}

