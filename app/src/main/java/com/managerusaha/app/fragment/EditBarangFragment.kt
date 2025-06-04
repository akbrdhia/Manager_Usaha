package com.managerusaha.app.fragment

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import com.managerusaha.app.MainActivity
import com.managerusaha.app.R
import com.managerusaha.app.room.entity.Barang
import com.managerusaha.app.viewmodel.BarangViewModel
import com.managerusaha.app.viewmodel.KategoriViewModel
import java.text.NumberFormat
import java.util.Locale

class EditBarangFragment : Fragment() {
    private var barangId: Int = -1
    private lateinit var barangViewModel: BarangViewModel
    private val kategoriViewModel: KategoriViewModel by viewModels()
    private lateinit var pickImageLauncher: ActivityResultLauncher<String>

    // * Views
    private lateinit var etNama: EditText
    private lateinit var etKuantitas: EditText
    private lateinit var etHargaBeli: EditText
    private lateinit var etHargaJual: EditText
    private lateinit var spinnerKategori: Spinner
    private lateinit var ivBarang: ImageView
    private lateinit var groupBack: FrameLayout

    // * State
    private var currentBarang: Barang? = null
    private var kategoriNamaDariBarang: String? = null
    private var currentImageUri: Uri? = null
    private var selectedImagePath: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        barangId = arguments?.getInt("barangId") ?: -1
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_edit_barang, container, false)

    @Suppress("UnsafeExperimentalUsageError")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        barangViewModel = ViewModelProvider(this)[BarangViewModel::class.java]
        initViews(view)
        configureHargaFormatter()
        setupImagePicker()
        configureKategoriSpinner()
        configureListeners()


        // * Observe barang data by ID
        barangViewModel.getBarangById(barangId).observe(viewLifecycleOwner) { barang ->
            barang?.let {
                currentBarang = it
                populateFields(it)
            }
        }
    }

    private fun initViews(root: View) {
        etNama = root.findViewById(R.id.et_nama)
        etKuantitas = root.findViewById(R.id.et_kuantitas)
        etHargaBeli = root.findViewById(R.id.et_harga_beli)
        etHargaJual = root.findViewById(R.id.et_harga_jual)
        spinnerKategori = root.findViewById(R.id.category_spinner)
        ivBarang = root.findViewById(R.id.iv_barang)
        groupBack = root.findViewById(R.id.group_back)
    }

    private fun configureListeners() {
        // * Tombol kembali
        groupBack.setOnClickListener {
            (activity as MainActivity).replaceFragment(StokFragment(), "STOK")
        }

        // * Tombol Hapus
        view?.findViewById<Button>(R.id.btn_hapus)?.setOnClickListener {
            currentBarang?.let { showDeleteConfirmation(it) }
        }

        // * Tombol Simpan (Update)
        view?.findViewById<Button>(R.id.btn_tambah)?.setOnClickListener {
            updateBarang()
        }
    }

    private fun showDeleteConfirmation(barang: Barang) {
        AlertDialog.Builder(requireContext())
            .setTitle("Hapus Barang")
            .setMessage("Yakin ingin menghapus barang \"${barang.nama}\"?")
            .setPositiveButton("Hapus") { _, _ ->
                barangViewModel.deleteBarang(barang)
                Toast.makeText(requireContext(), "Barang dihapus", Toast.LENGTH_SHORT).show()
                (activity as MainActivity).replaceFragment(StokFragment(), "STOK")
            }
            .setNegativeButton("Batal", null)
            .show()
    }

    private fun configureHargaFormatter() {
        setupRupiahFormatter(etHargaBeli)
        setupRupiahFormatter(etHargaJual)
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
                val parsed = cleanString.toDoubleOrNull() ?: 0.0
                val formatted = formatRupiah(parsed)

                current = formatted
                editText.setText(formatted)
                editText.setSelection(formatted.length)
                editText.addTextChangedListener(this)
            }
        })
    }

    private fun formatRupiah(number: Double): String {
        val localeID = Locale("in", "ID")
        val formatter = NumberFormat.getCurrencyInstance(localeID)
        return formatter.format(number).replace(",00", "")
    }

    private fun configureKategoriSpinner() {
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
            // Pilih kategori yg sesuai data awal
            selectCurrentKategori()
        }
    }

    private fun selectCurrentKategori() {
        val namaKategori = kategoriNamaDariBarang ?: return
        val adapter = spinnerKategori.adapter as? ArrayAdapter<*>
        adapter?.let {
            for (i in 0 until it.count) {
                if (it.getItem(i).toString() == namaKategori) {
                    spinnerKategori.setSelection(i)
                    break
                }
            }
        }
    }

    private fun setupImagePicker() {
        pickImageLauncher =
            registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
                uri?.let {
                    try { // ! Grant permiss kalo blm punya
                        requireContext().contentResolver.takePersistableUriPermission(
                            it,
                            Intent.FLAG_GRANT_READ_URI_PERMISSION
                        )
                    } catch (e: SecurityException) {
                        e.printStackTrace()
                    }

                    ivBarang.setImageURI(it)
                    selectedImagePath = it.toString()
                }
            }
    }

    // ? liat nanti kalo mau pake ini
    private fun getRawDouble(input: String): Double {
        val cleanString = input.replace("[^0-9]".toRegex(), "")
        return cleanString.toDoubleOrNull() ?: 0.0
    }

    private fun populateFields(barang: Barang) {
        etNama.setText(barang.nama)
        etKuantitas.setText(barang.stok.toString())
        etHargaBeli.setText(formatRupiah(barang.modal))
        etHargaJual.setText(formatRupiah(barang.harga))

        kategoriNamaDariBarang = barang.kategori
        selectCurrentKategori()

        barang.gambarPath?.let { path ->
            try {
                ivBarang.setImageURI(Uri.parse(path))
            } catch (e: SecurityException) {
                e.printStackTrace()
                Toast.makeText(requireContext(), "Gagal menampilkan gambar, izin ditolak", Toast.LENGTH_SHORT).show()
            }
        }

    }

    private fun updateBarang() {
        val nama = etNama.text.toString().trim()
        val kuantitas = etKuantitas.text.toString().toIntOrNull() ?: 0
        val hargaBeli = etHargaBeli.text.toString()
            .replace("[^0-9]".toRegex(), "")
            .toDoubleOrNull() ?: 0.0
        val hargaJual = etHargaJual.text.toString()
            .replace("[^0-9]".toRegex(), "")
            .toDoubleOrNull() ?: 0.0
        val kategori = spinnerKategori.selectedItem.toString()
        val gambarPath = currentBarang?.gambarPath

        if (nama.isEmpty()) {
            Toast.makeText(requireContext(), "Nama tidak boleh kosong", Toast.LENGTH_SHORT).show()
            return
        }
        if (currentBarang == null) {
            Toast.makeText(requireContext(), "Barang tidak ditemukan", Toast.LENGTH_SHORT).show()
            return
        }

        val updated = currentBarang!!.copy(
            nama = nama,
            stok = kuantitas,
            modal = hargaBeli,
            harga = hargaJual,
            kategori = kategori,
            gambarPath = gambarPath
        )
        barangViewModel.updateBarang(updated)
        Toast.makeText(requireContext(), "Barang berhasil diupdate", Toast.LENGTH_SHORT).show()
        (activity as MainActivity).replaceFragment(StokFragment(), "STOK")
    }
}
