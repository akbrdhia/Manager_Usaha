import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.managerusaha.app.viewmodel.BarangViewModel
import com.managerusaha.app.R
import com.managerusaha.app.room.entity.Barang

class TmpbarangFragment : Fragment() {

    private val barangViewModel: BarangViewModel by viewModels()


    private lateinit var ivBarang: ImageView
    private lateinit var etNama: EditText
    private lateinit var etStok: EditText
    private lateinit var etHarga: EditText
    private lateinit var etModal: EditText
    private lateinit var btnTambah: Button
    private lateinit var btnBatal: Button
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
        inisialisasi(view)
        setupImagePicker()
        setupClickListeners()

        barangViewModel.allBarang.observe(viewLifecycleOwner) { barangList ->
            val p = barangList.joinToString { it.nama }
            Toast.makeText(requireContext(), p, Toast.LENGTH_SHORT).show()
        }


        // Observasi barang berdasarkan kategori
        barangViewModel.getBarangByKategori(1).observe(viewLifecycleOwner) { barangList ->
        }


    }

    private fun inisialisasi(view: View) {
        ivBarang = view.findViewById(R.id.iv_barang)
        etNama = view.findViewById(R.id.et_nama)
        etStok = view.findViewById(R.id.et_kuantitas)
        etModal = view.findViewById(R.id.et_harga_beli)
        etHarga = view.findViewById(R.id.et_harga_jual)
        btnTambah = view.findViewById(R.id.btn_tambah)
        btnBatal = view.findViewById(R.id.btn_batal)
        plusIco = view.findViewById(R.id.plus_ico)
    }

    private fun setupImagePicker() {
        pickImageLauncher =
            registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
                if (uri != null) {
                    ivBarang.setImageURI(uri)
                    selectedImagePath = uri.toString()
                }
            }
    }

    private fun setupClickListeners() {
        btnTambah.setOnClickListener {
            tambahBarang()
        }

        ivBarang.setOnClickListener {
            pickImageLauncher.launch("image/*")
        }
    }

    private fun tambahBarang() {
        val barangbaru = Barang(
            nama = etNama.toString(),
            kategoriId = 1,
            stok = 10,
            harga = 100000.0,
            modal = 80000.0
        )
    }
}


