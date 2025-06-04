package com.managerusaha.app.fragment

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.managerusaha.app.MainActivity
import com.managerusaha.app.R
import com.managerusaha.app.dialog.KategoriDialogFragment
import com.managerusaha.app.room.entity.Barang
import com.managerusaha.app.room.entity.Kategori
import com.managerusaha.app.viewmodel.BarangViewModel
import com.managerusaha.app.viewmodel.KategoriViewModel
import java.text.NumberFormat
import java.util.Locale

class TmpbarangFragment : Fragment() {

    // ViewModels
    private val barangViewModel: BarangViewModel by viewModels()
    private val kategoriViewModel: KategoriViewModel by viewModels()

    // Views
    private lateinit var ivBarang: ImageView
    private lateinit var etNama: EditText
    private lateinit var etStok: EditText
    private lateinit var spinnerKategori: Spinner
    private lateinit var etHarga: EditText
    private lateinit var etModal: EditText
    private lateinit var btnTambah: Button
    private lateinit var btnBatal: Button
    private lateinit var plusIcon: ImageView
    private lateinit var groupBack: FrameLayout
    private lateinit var ivBack: ImageView

    // Image picker launcher
    private lateinit var pickImageLauncher: ActivityResultLauncher<String>
    private var selectedImagePath: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_tmpbarang, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews(view)
        setupImagePicker()
        setupFormatters()
        setupSpinnerData()
        setupClickListeners()
    }

    // Inisialisasi semua view dari layout
    private fun initViews(root: View) {
        ivBarang = root.findViewById(R.id.iv_barang)
        etNama = root.findViewById(R.id.et_nama)
        etStok = root.findViewById(R.id.et_kuantitas)
        spinnerKategori = root.findViewById(R.id.category_spinner)
        etModal = root.findViewById(R.id.et_harga_beli)
        etHarga = root.findViewById(R.id.et_harga_jual)
        btnTambah = root.findViewById(R.id.btn_tambah)
        btnBatal = root.findViewById(R.id.btn_batal)
        plusIcon = root.findViewById(R.id.plus_ico)
        groupBack = root.findViewById(R.id.group_back)
        ivBack = root.findViewById(R.id.iv_back)
    }

    // Siapkan ActivityResult untuk pemilihan gambar
    private fun setupImagePicker() {
        pickImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let {
                // Ambil persistable permission (wajib)
                requireContext().contentResolver.takePersistableUriPermission(
                    it,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION
                )

                // Tampilkan gambar dan simpan path
                ivBarang.setImageURI(it)
                selectedImagePath = it.toString()
            }
        }
    }


    // Format input harga dan modal menjadi Rupiah secara realtime
    private fun setupFormatters() {
        setupRupiahFormatter(etModal)
        setupRupiahFormatter(etHarga)
    }

    private fun setupRupiahFormatter(editText: EditText) {
        editText.addTextChangedListener(object : TextWatcher {
            private var current = ""
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) { }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) { }
            override fun afterTextChanged(s: Editable?) {
                val input = s.toString()
                if (input == current) return
                editText.removeTextChangedListener(this)

                val cleanString = input.replace("[Rp,.\\s]".toRegex(), "")
                val parsed = if (cleanString.isNotEmpty()) cleanString.toLong() else 0L
                val formatted = formatRupiah(parsed)

                current = formatted
                editText.setText(formatted)
                editText.setSelection(formatted.length)
                editText.addTextChangedListener(this)
            }
        })
    }

    // Mengonversi angka ke format Rupiah
    private fun formatRupiah(amount: Long): String {
        val localeID = Locale("id", "ID")
        val formatter = NumberFormat.getCurrencyInstance(localeID)
        return formatter.format(amount).replace("Rp", "Rp.").replace(",00", "")
    }

    // Mengambil data kategori dan set ke Spinner
    private fun setupSpinnerData() {
        kategoriViewModel.allKategori.observe(viewLifecycleOwner) { daftarKategori ->
            val options = mutableListOf("Lainnya")
            options.addAll(daftarKategori.map { it.nama })

            val adapter = ArrayAdapter(
                requireContext(),
                android.R.layout.simple_spinner_item,
                options
            ).also {
                it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            }
            spinnerKategori.adapter = adapter
        }
    }

    // Pasang semua click listener
    private fun setupClickListeners() {
        // Pilih gambar
        ivBarang.setOnClickListener {
            pickImageLauncher.launch("image/*")
        }

        // Tambah kategori baru lewat dialog
        plusIcon.setOnClickListener {
            val dialog = KategoriDialogFragment { namaBaru ->
                kategoriViewModel.insertKategori(Kategori(nama = namaBaru))
                Toast.makeText(
                    requireContext(),
                    "Kategori ditambahkan: $namaBaru",
                    Toast.LENGTH_SHORT
                ).show()
            }
            dialog.show(parentFragmentManager, "KategoriDialog")
        }

        // Tombol Batal atau kembali
        btnBatal.setOnClickListener { navigateBack() }
        groupBack.setOnClickListener { navigateBack() }
        ivBack.setOnClickListener { navigateBack() }

        // Tombol Tambah simpan barang
        btnTambah.setOnClickListener { tambahBarang() }
    }

    // Navigasi kembali ke StokFragment
    private fun navigateBack() {
        (activity as MainActivity).replaceFragment(StokFragment(), "STOK")
    }

    // Validasi input dan simpan barang baru
    private fun tambahBarang() {
        val nama = etNama.text.toString().trim()
        val kategori = spinnerKategori.selectedItem.toString()
        val stokStr = etStok.text.toString().trim()
        val harga = getRawValue(etHarga.text.toString())
        val modal = getRawValue(etModal.text.toString())

        // Validasi
        if (nama.isEmpty()) {
            etNama.error = "Nama barang wajib diisi"
            return
        }
        if (kategori.isEmpty() || kategori == "Lainnya") {
            Toast.makeText(requireContext(), "Kategori belum dipilih", Toast.LENGTH_SHORT).show()
            return
        }
        val stok = stokStr.toIntOrNull()
        if (stok == null || stok < 0) {
            etStok.error = "Stok minimal 0"
            return
        }
        if (harga <= 0) {
            etHarga.error = "Harga minimal Rp. 0"
            return
        }
        if (modal < 0) {
            etModal.error = "Modal minimal Rp. 0"
            return
        }

        val barangBaru = Barang(
            nama = nama,
            kategori = kategori,
            stok = stok,
            harga = harga,
            modal = modal,
            gambarPath = selectedImagePath
        )

        barangViewModel.insertBarang(barangBaru)
        Toast.makeText(requireContext(), "Barang berhasil ditambahkan!", Toast.LENGTH_SHORT).show()
        navigateBack()
    }

    // Mengubah string "Rp.1.000" → angka murni 1000.0
    private fun getRawValue(input: String): Double {
        val cleanString = input.replace("[^0-9]".toRegex(), "")
        return cleanString.toDoubleOrNull() ?: 0.0
    }
}
