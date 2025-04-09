package com.managerusaha.app.room.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.managerusaha.app.room.entity.Barang

@Dao
interface BarangDao {
    @Insert
    suspend fun insert(barang: Barang)

    @Update
    suspend fun update(barang: Barang)

    @Delete
    suspend fun delete(barang: Barang)

    @Query("SELECT * FROM barang ORDER BY id ASC")
    fun getAllBarang(): LiveData<List<Barang>>

    @Query("SELECT * FROM barang WHERE kategori = :kategori")
    fun getBarangByKategori(kategori: Int): LiveData<List<Barang>>

    @Query("SELECT * FROM barang WHERE nama LIKE '%' || :searchQuery || '%'")
    fun searchBarang(searchQuery: String): LiveData<List<Barang>>

    @Query("SELECT * FROM barang WHERE stok <= :batasStok")
    fun getBarangStokRendah(batasStok: Int): LiveData<List<Barang>>

    @Query("SELECT * FROM barang WHERE harga BETWEEN :minHarga AND :maxHarga")
    fun getBarangByRangeHarga(minHarga: Double, maxHarga: Double): LiveData<List<Barang>>

    @Query("UPDATE barang SET stok = stok + :jumlah WHERE id = :barangId")
    suspend fun updateStok(barangId: Int, jumlah: Int)

    @Query("SELECT * FROM barang WHERE id = :barangId")
    fun getBarangById(barangId: Int): LiveData<Barang>

}
