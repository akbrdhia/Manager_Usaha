import androidx.room.*

@Dao
interface KategoriDao {
    @Insert
    suspend fun insert(kategori: Kategori)

    @Query("SELECT * FROM kategori")
    fun getAllKategori(): List<Kategori>
}
