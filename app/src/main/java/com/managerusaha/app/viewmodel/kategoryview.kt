package com.managerusaha.app.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.managerusaha.app.room.AppDatabase
import com.managerusaha.app.room.entity.Kategori
import kotlinx.coroutines.launch

class KategoriViewModel(application: Application) : AndroidViewModel(application) {
    private val kategoriDao = AppDatabase.getDatabase(application).kategoriDao()

    val allKategori: LiveData<List<Kategori>> = kategoriDao.getAllKategori()

    fun insertKategori(kategori: Kategori) {
        viewModelScope.launch {
            kategoriDao.insert(kategori)
        }
    }
}
