package com.managerusaha.app.room.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.managerusaha.app.room.entity.Riwayat

@Dao
interface RiwayatDao {
    @Insert
    suspend fun insert(riwayat: Riwayat)

    @Query("SELECT * FROM riwayat WHERE barangId = :barangId")
    fun getRiwayatByBarang(barangId: Int): LiveData<List<Riwayat>>

    @Query("SELECT * FROM riwayat")
    fun getAllRiwayat(): LiveData<List<Riwayat>>
}
