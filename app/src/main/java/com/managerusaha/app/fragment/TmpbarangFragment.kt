import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.managerusaha.app.viewmodel.BarangViewModel
import com.managerusaha.app.R
import com.managerusaha.app.dialog.KategoriDialogFragment
import com.managerusaha.app.room.entity.Barang
import com.managerusaha.app.room.entity.Kategori
import com.managerusaha.app.viewmodel.KategoriViewModel

class TmpbarangFragment : Fragment() {

    private val barangViewModel: BarangViewModel by viewModels()
    private val kategoriViewModel : KategoriViewModel by viewModels()

    private lateinit var ivBarang: ImageView
    private lateinit var etNama: EditText
    private lateinit var etStok: EditText
    private lateinit var spinkategory : Spinner
    private lateinit var etHarga: EditText
    private lateinit var etModal: EditText
    private lateinit var btnTambah: Button
    private lateinit var btnBatal: Button
    private lateinit var plusIco: ImageView
    private var selectedImagePath: String? = null
    private lateinit var pickImageLauncher: ActivityResultLauncher<String>
    private var namkat : String? = null

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
    }

    private fun inisialisasi(view: View) {
        ivBarang = view.findViewById(R.id.iv_barang)
        etNama = view.findViewById(R.id.et_nama)
        etStok = view.findViewById(R.id.et_kuantitas)
        spinkategory = view.findViewById(R.id.category_spinner)
        etModal = view.findViewById(R.id.et_harga_beli)
        etHarga = view.findViewById(R.id.et_harga_jual)
        btnTambah = view.findViewById(R.id.btn_tambah)
        btnBatal = view.findViewById(R.id.btn_batal)
        plusIco = view.findViewById(R.id.plus_ico)
        category_spin_refresh()
    }

    private fun category_spin_refresh(){
        kategoriViewModel.allKategori.observe(viewLifecycleOwner) { allkategory ->
            val categories = mutableListOf("all")
            categories.addAll(allkategory.map { it.nama })

            val categoryAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, categories)
            categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinkategory.adapter = categoryAdapter
        }
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

        plusIco.setOnClickListener {
            val dialog = KategoriDialogFragment { kategoriNama ->
               kategoriViewModel.insertKategori(Kategori(nama = kategoriNama))
                Toast.makeText(requireContext(), "Kategori ditambahkan: $kategoriNama", Toast.LENGTH_SHORT).show()
            }
            dialog.show(parentFragmentManager, "KategoriDialog")
        }


        ivBarang.setOnClickListener {
            pickImageLauncher.launch("image/*")
        }
    }

    private fun tambahBarang() {
        val nama = etNama.text.toString()
        val kategori = spinkategory.selectedItem.toString()
        val stokStr = etStok.text.toString().trim()
        val hargaStr = etHarga.text.toString().trim()
        val modalStr = etModal.text.toString().trim()

        if (nama.isEmpty()) {
            etNama.error = "Nama barang wajib diisi"
            return
        }
        if (kategori.isEmpty() || kategori == "Pilih Kategori") {
            Toast.makeText(requireContext(), "Kategori belum dipilih", Toast.LENGTH_SHORT).show()
            return
        }
        if (stokStr.isEmpty() || stokStr.toIntOrNull() == null || stokStr.toInt() < 0) {
            etStok.error = "Stok harus angka dan minimal 0"
            return
        }
        if (hargaStr.isEmpty() || hargaStr.toDoubleOrNull() == null || hargaStr.toDouble() < 0) {
            etHarga.error = "Harga harus angka dan minimal 0"
            return
        }
        if (modalStr.isEmpty() || modalStr.toDoubleOrNull() == null || modalStr.toDouble() < 0) {
            etModal.error = "Modal harus angka dan minimal 0"
            return
        }

        val barangbaru = Barang(
            nama = nama,
            kategori = kategori,
            stok = stokStr.toInt(),
            harga = hargaStr.toDouble(),
            modal = modalStr.toDouble(),
            gambarPath = selectedImagePath
        )

        barangViewModel.insertBarang(barangbaru)
        Toast.makeText(requireContext(), "Barang berhasil ditambahkan!", Toast.LENGTH_SHORT).show()
    }

}


