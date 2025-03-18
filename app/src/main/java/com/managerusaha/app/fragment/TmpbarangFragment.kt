
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.activity.result.ActivityResultLauncher
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.managerusaha.app.viewmodel.BarangViewModel
import com.managerusaha.app.R
import com.managerusaha.app.room.entity.Barang

class TmpbarangFragment : Fragment(){

    private val barangViewModel: BarangViewModel by viewModels()



    private lateinit var ivBarang: ImageView
    private lateinit var etNama: EditText
    private lateinit var etStok: EditText
    private lateinit var etHarga: EditText
    private lateinit var etModal: EditText
    private lateinit var btnTambah: Button
    private lateinit var plusIco: ImageView
    private var selectedImagePath: String? = null
    private lateinit var pickImageLauncher: ActivityResultLauncher<String>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_tmpbarang, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Observasi semua barang
        barangViewModel.allBarang.observe(viewLifecycleOwner) { barangList ->
            // Update UI dengan data barang
        }

        // Observasi barang berdasarkan kategori
        barangViewModel.getBarangByKategori(1).observe(viewLifecycleOwner) { barangList ->
            // Update UI dengan data barang dari kategori tertentu
        }

        // Menambah barang baru
        val barangBaru = Barang(
            nama = "Nama Barang",
            kategoriId = 1,
            stok = 10,
            harga = 100000.0,
            modal = 80000.0
        )
        barangViewModel.insertBarang(barangBaru)

        // Update stok
        barangViewModel.updateStok(1, 5) // Menambah 5 stok ke barang dengan ID 1
    }
 }

