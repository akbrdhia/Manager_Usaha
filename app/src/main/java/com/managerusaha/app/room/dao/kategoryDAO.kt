package com.managerusaha.app.room.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.managerusaha.app.room.entity.Kategori

@Dao
interface KategoriDao {
    @Insert
    suspend fun insert(kategori: Kategori)

    @Query("SELECT * FROM kategori")
    fun getAllKategori(): LiveData<List<Kategori>>
}
