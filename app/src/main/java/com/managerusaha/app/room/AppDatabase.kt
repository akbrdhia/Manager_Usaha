package com.managerusaha.app.room

import android.content.Context
import android.util.Log
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.managerusaha.app.room.dao.BarangDao
import com.managerusaha.app.room.dao.KategoriDao
import com.managerusaha.app.room.dao.RiwayatDao
import com.managerusaha.app.room.entity.Barang
import com.managerusaha.app.room.entity.Kategori
import com.managerusaha.app.room.entity.Riwayat

@Database(entities = [Barang::class, Kategori::class, Riwayat::class], version = 2, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun barangDao(): BarangDao
    abstract fun kategoriDao(): KategoriDao
    abstract fun riwayatDao(): RiwayatDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            Log.d("DB_CHECK", "getDatabase() dipanggil")
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "managerusaha_db"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                Log.d("DB_CHECK", "Database berhasil dibuat: ${instance.openHelper.databaseName}")
                instance
            }
        }
    }
}


