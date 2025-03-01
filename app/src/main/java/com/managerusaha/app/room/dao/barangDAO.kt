import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface BarangDao {
    @Insert
    suspend fun insert(barang: Barang)

    @Update
    suspend fun update(barang: Barang)

    @Delete
    suspend fun delete(barang: Barang)

    @Query("SELECT * FROM barang")
    fun getAllBarang(): LiveData<List<Barang>>

    @Query("SELECT * FROM barang WHERE kategoriId = :kategoriId")
    fun getBarangByKategori(kategoriId: Int): LiveData<List<Barang>>
}
