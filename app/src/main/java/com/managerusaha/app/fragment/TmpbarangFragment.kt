import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.*
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.managerusaha.app.MainActivity
import com.managerusaha.app.viewmodel.BarangViewModel
import com.managerusaha.app.R
import com.managerusaha.app.dialog.KategoriDialogFragment
import com.managerusaha.app.fragment.StokFragment
import com.managerusaha.app.room.entity.Barang
import com.managerusaha.app.room.entity.Kategori
import com.managerusaha.app.viewmodel.KategoriViewModel
import java.text.NumberFormat
import java.util.Locale

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
    private lateinit var GroubBack : FrameLayout
    private lateinit var icback : ImageView
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
        setupfomat()

    }

    private fun setupfomat() {
        etModal.addTextChangedListener(object : TextWatcher {
            private var current = ""

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                if (s.toString() != current) {
                    etModal.removeTextChangedListener(this)

                    val cleanString = s.toString().replace("[Rp,.]".toRegex(), "")
                    val parsed = if (cleanString.isNotEmpty()) cleanString.toLong() else 0
                    val formatted = formatRupiah(parsed)

                    current = formatted
                    etModal.setText(formatted)
                    etModal.setSelection(formatted.length)

                    etModal.addTextChangedListener(this)
                }
            }
        })
        etHarga.addTextChangedListener(object : TextWatcher {
            private var current = ""

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                if (s.toString() != current) {
                    etHarga.removeTextChangedListener(this)

                    val cleanString = s.toString().replace("[Rp,.]".toRegex(), "")
                    val parsed = if (cleanString.isNotEmpty()) cleanString.toLong() else 0
                    val formatted = formatRupiah(parsed)

                    current = formatted
                    etHarga.setText(formatted)
                    etHarga.setSelection(formatted.length)

                    etHarga.addTextChangedListener(this)
                }
            }
        })
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
        GroubBack = view.findViewById(R.id.group_back)
        icback = view.findViewById(R.id.iv_back)
        category_spin_refresh()
    }

    private fun category_spin_refresh(){
        kategoriViewModel.allKategori.observe(viewLifecycleOwner) { allkategory ->
            val categories = mutableListOf("Lainnya")
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

        GroubBack.setOnClickListener{
            (activity as MainActivity).replaceFragment(StokFragment(), "STOK")
        }

        icback.setOnClickListener{
            (activity as MainActivity).replaceFragment(StokFragment(), "STOK")
        }
    }

    private fun formatRupiah(amount: Long): String {
        val formatter = NumberFormat.getCurrencyInstance(Locale("id", "ID"))
        return formatter.format(amount).replace("Rp", "Rp.").replace(",00", "")
    }

    private fun getRawDouble(input: String): Double {
        val cleanString = input.replace("[^0-9]".toRegex(), "")
        return cleanString.toDoubleOrNull() ?: 0.0
    }


    private fun tambahBarang() {
        val nama = etNama.text.toString()
        val kategori = spinkategory.selectedItem.toString()
        val stokStr = etStok.text.toString().trim()
        val hargaStr = getRawDouble(etHarga.text.toString())
        val modalStr = getRawDouble(etModal.text.toString())

        if (nama.isEmpty()) {
            etNama.error = "Nama barang wajib diisi"
            return
        }
        if (kategori.isEmpty() || kategori == "Pilih Kategori") {
            Toast.makeText(requireContext(), "Kategori belum dipilih", Toast.LENGTH_SHORT).show()
            return
        }
        if (stokStr.isEmpty() || stokStr.toIntOrNull() == null || stokStr.toInt() < 0) {
            etStok.error = "Stok minimal 0"
            return
        }
        if (hargaStr <= 0) {
            etHarga.error = "Harga minimal Rp. 0"
            return
        }
        if (modalStr < 0) {
            etModal.error = "Modal minimal Rp. 0"
            return
        }

        val barangbaru = Barang(
            nama = nama,
            kategori = kategori,
            stok = stokStr.toInt(),
            harga = hargaStr,
            modal = modalStr,
            gambarPath = selectedImagePath
        )

        barangViewModel.insertBarang(barangbaru)
        Toast.makeText(requireContext(), "Barang berhasil ditambahkan!", Toast.LENGTH_SHORT).show()
    }

}


