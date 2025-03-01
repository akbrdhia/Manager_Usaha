import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface RiwayatDao {
    @Insert
    suspend fun insert(riwayat: Riwayat)

    @Query("SELECT * FROM riwayat WHERE barangId = :barangId")
    fun getRiwayatByBarang(barangId: Int): LiveData<List<Riwayat>>

    @Query("SELECT * FROM barang")
    fun getAllRiwayat(): LiveData<List<Riwayat>>
}
