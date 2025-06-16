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

    /** Total stok masuk bulan ini */
    @Query("""
      SELECT SUM(jumlah) FROM riwayat
      WHERE tipe = 'MASUK' AND tanggal BETWEEN :start AND :end
    """)
    suspend fun getTotalStokMasukBulanIni(start: Long, end: Long): Int?

    /** Total stok keluar bulan ini */
    @Query("""
      SELECT SUM(jumlah) FROM riwayat
      WHERE tipe = 'KELUAR' AND tanggal BETWEEN :start AND :end
    """)
    suspend fun getTotalStokKeluarBulanIni(start: Long, end: Long): Int?

    /** Total omset (penjualan) bulan ini: jumlah × harga jual */
    @Query("""
      SELECT SUM(r.jumlah * b.harga) FROM riwayat r
      JOIN barang b ON r.barangId = b.id
      WHERE r.tipe = 'KELUAR' AND r.tanggal BETWEEN :start AND :end
    """)
    suspend fun getTotalOmsetBulanIni(start: Long, end: Long): Double?

    /** Total untung bulan ini: jumlah × (harga jual – modal) */
    @Query("""
      SELECT SUM(r.jumlah * (b.harga - b.modal)) FROM riwayat r
      JOIN barang b ON r.barangId = b.id
      WHERE r.tipe = 'KELUAR' AND r.tanggal BETWEEN :start AND :end
    """)
    suspend fun getTotalUntungBulanIni(start: Long, end: Long): Double?
}
