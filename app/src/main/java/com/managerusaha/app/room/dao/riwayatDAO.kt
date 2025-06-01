package com.managerusaha.app.room.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.managerusaha.app.room.entity.Riwayat
import com.managerusaha.app.utills.model.RiwayatWithBarang

@Dao
interface RiwayatDao {
    @Insert
    suspend fun insert(riwayat: Riwayat)

    @Query("SELECT * FROM riwayat WHERE barangId = :barangId")
    fun getRiwayatByBarang(barangId: Int): LiveData<List<Riwayat>>

    @Query("SELECT * FROM riwayat")
    fun getAllRiwayat(): LiveData<List<Riwayat>>

    @Query(
        """
    SELECT r.id AS riwayatId, b.id AS barangId, b.nama, b.stok, b.harga, b.modal, b.gambarPath,
           r.tanggal, r.jumlah, r.tipe
    FROM riwayat r
    JOIN barang b ON r.barangId = b.id
    ORDER BY r.tanggal DESC
    """
    )
    fun getRiwayatWithBarang(): LiveData<List<RiwayatWithBarang>>

}
