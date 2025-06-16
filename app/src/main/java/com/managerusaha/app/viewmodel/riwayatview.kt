package com.managerusaha.app.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.managerusaha.app.utills.getRangeBulanIni
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
    // LiveData atau StateFlow untuk keperluan UI
    private val _stokMasuk = MutableLiveData<Int>()
    val stokMasuk: LiveData<Int> = _stokMasuk

    private val _stokKeluar = MutableLiveData<Int>()
    val stokKeluar: LiveData<Int> = _stokKeluar

    private val _omset = MutableLiveData<Double>()
    val omset: LiveData<Double> = _omset

    private val _untung = MutableLiveData<Double>()
    val untung: LiveData<Double> = _untung

    fun loadStatistikBulanIni() {
        val (start, end) = getRangeBulanIni()
        viewModelScope.launch {
            _stokMasuk.value  = riwayatDao.getTotalStokMasukBulanIni(start, end) ?: 0
            _stokKeluar.value = riwayatDao.getTotalStokKeluarBulanIni(start, end) ?: 0
            _omset.value      = riwayatDao.getTotalOmsetBulanIni(start, end) ?: 0.0
            _untung.value     = riwayatDao.getTotalUntungBulanIni(start, end) ?: 0.0
        }
    }
    val allRiwayatWithBarang: LiveData<List<RiwayatWithBarang>> = riwayatDao.getRiwayatWithBarang()
}

