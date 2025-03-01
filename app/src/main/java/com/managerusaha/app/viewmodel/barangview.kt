import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class BarangViewModel(application: Application) : AndroidViewModel(application) {
    private val barangDao = AppDatabase.getDatabase(application).barangDao()

    val allBarang: LiveData<List<Barang>> = barangDao.getAllBarang()

    fun insertBarang(barang: Barang) {
        viewModelScope.launch {
            barangDao.insert(barang)
        }
    }
}
