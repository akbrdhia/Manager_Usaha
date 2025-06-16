package com.managerusaha.app.fragment

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts.GetContent
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.android.material.textfield.TextInputEditText
import com.managerusaha.app.MainActivity
import com.managerusaha.app.R
import com.managerusaha.app.dialog.KategoriDialogFragment
import com.managerusaha.app.room.entity.Barang
import com.managerusaha.app.room.entity.Kategori
import com.managerusaha.app.viewmodel.BarangViewModel
import com.managerusaha.app.viewmodel.BarangFormViewModel
import com.managerusaha.app.viewmodel.KategoriViewModel
import java.text.NumberFormat
import java.util.*

/**
 * Fragment untuk menambah/edit barang dengan stateful form
 */
class TmpbarangFragment : Fragment() {

    // ViewModels: satu untuk operasi CRUD, satu untuk daftar kategori, dan satu untuk menyimpan state form
    private val barangViewModel: BarangViewModel by viewModels()
    private val kategoriViewModel: KategoriViewModel by viewModels()
    private val formVm: BarangFormViewModel by viewModels()

    // Views
    private lateinit var ivBarang: ImageView
    private lateinit var etNama: EditText
    private lateinit var etStok: EditText
    private lateinit var spinnerKategori: Spinner
    private lateinit var etHarga: EditText
    private lateinit var etModal: EditText
    private lateinit var etHasilScan: TextInputEditText
    private lateinit var btnTambah: Button
    private lateinit var btnBatal: Button
    private lateinit var plusIcon: ImageView
    private lateinit var icScan: ImageView
    private lateinit var groupBack: FrameLayout
    private lateinit var ivBack: ImageView

    // Image picker launcher
    private lateinit var pickImageLauncher: ActivityResultLauncher<String>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View =  inflater.inflate(R.layout.fragment_tmpbarang, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews(view)
        setupImagePicker()
        setupFormatters()
        setupTextWatchers()
        setupScanListener()
        setupClickListeners()
        setupSpinnerData()
    }

    /**
     * Inisialisasi semua view dari layout
     */
    private fun initViews(root: View) {
        ivBarang        = root.findViewById(R.id.iv_barang)
        etNama          = root.findViewById(R.id.et_nama)
        etStok          = root.findViewById(R.id.et_kuantitas)
        spinnerKategori = root.findViewById(R.id.category_spinner)
        etModal         = root.findViewById(R.id.et_harga_beli)
        etHarga         = root.findViewById(R.id.et_harga_jual)
        etHasilScan     = root.findViewById(R.id.et_hasil_scan)
        btnTambah       = root.findViewById(R.id.btn_tambah)
        btnBatal        = root.findViewById(R.id.btn_batal)
        plusIcon        = root.findViewById(R.id.plus_ico)
        icScan          = root.findViewById(R.id.ic_scan)
        groupBack       = root.findViewById(R.id.group_back)
        ivBack          = root.findViewById(R.id.iv_back)
    }

    /**
     * Setup pemilih gambar dengan ActivityResultContracts.GetContent
     */
    private fun setupImagePicker() {
        pickImageLauncher = registerForActivityResult(GetContent()) { uri: Uri? ->
            uri?.let {
                requireContext().contentResolver.takePersistableUriPermission(
                    it, Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
                ivBarang.setImageURI(it)
                formVm.gambarPath = it.toString()
            }
        }
    }

    /**
     * Setup format input harga dan modal menjadi Rupiah secara realtime
     */
    private fun setupFormatters() {
        setupRupiahFormatter(etModal)
        setupRupiahFormatter(etHarga)
    }

    private fun setupRupiahFormatter(editText: EditText) {
        editText.doAfterTextChanged { s ->
            val input = s.toString().replace("[Rp,.\\s]".toRegex(), "")
            val value = input.toLongOrNull() ?: 0L
            val formatted = NumberFormat.getCurrencyInstance(Locale("id", "ID"))
                .format(value)
                .replace("Rp", "Rp.")
                .replace(",00", "")
            if (formatted != editText.text.toString()) {
                editText.setText(formatted)
                editText.setSelection(formatted.length)
            }
        }
    }

    /**
     * Setup dan observe data kategori untuk Spinner, lalu restore state form
     */
    private fun setupSpinnerData() {
        kategoriViewModel.allKategori.observe(viewLifecycleOwner) { list ->
            val opts = mutableListOf("Lainnya")
            opts.addAll(list.map { it.nama })
            val adapter = ArrayAdapter(
                requireContext(),
                android.R.layout.simple_spinner_item,
                opts
            ).also { it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item) }
            spinnerKategori.adapter = adapter

            // Restore state setelah adapter terpasang
            restoreFormState(adapter)
        }
    }

    /**
     * Simpan setiap perubahan text ke ViewModel agar state bertahan
     */
    private fun setupTextWatchers() {
        etNama.doAfterTextChanged    { formVm.nama       = it.toString() }
        etStok.doAfterTextChanged    { formVm.stok       = it.toString() }
        etHarga.doAfterTextChanged   { formVm.hargaJual  = it.toString() }
        etModal.doAfterTextChanged   { formVm.hargaModal = it.toString() }
        spinnerKategori.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {}
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, pos: Int, id: Long) {
                formVm.kategori = parent!!.getItemAtPosition(pos).toString()
            }
        }
    }
    private fun setupScanListener() {
        parentFragmentManager.setFragmentResultListener(
            "scan_result", viewLifecycleOwner
        ) { _, bundle ->
            formVm.barcode = bundle.getString("rawvalue", "")
            etHasilScan.setText(formVm.barcode)
        }
        icScan.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, com.managerusaha.app.fragment.minor.CamFragment())
                .addToBackStack(null)
                .commit()
        }
    }

    private fun setupClickListeners() {
        ivBarang.setOnClickListener { pickImageLauncher.launch("image/*") }
        plusIcon.setOnClickListener {
            KategoriDialogFragment { name ->
                kategoriViewModel.insertKategori(Kategori(nama = name))
            }.show(parentFragmentManager, "dlg")
        }
        btnBatal.setOnClickListener { navigateBack() }
        groupBack.setOnClickListener { navigateBack() }
        ivBack.setOnClickListener { navigateBack() }
        btnTambah.setOnClickListener { tambahBarang() }
    }

    /**
     * Restore seluruh state form, termasuk spinner dan image
     */
    private fun restoreFormState(adapter: ArrayAdapter<String>) {
        // Spinner selection
        val idx = adapter.getPosition(formVm.kategori)
        spinnerKategori.setSelection(if (idx >= 0) idx else 0)

        // Fields
        etNama.setText(formVm.nama)
        etStok.setText(formVm.stok)
        etHarga.setText(formVm.hargaJual)
        etModal.setText(formVm.hargaModal)
        etHasilScan.setText(formVm.barcode)
        formVm.gambarPath?.let { ivBarang.setImageURI(Uri.parse(it)) }
    }

    private fun navigateBack() {
        (activity as MainActivity).replaceFragment(StokFragment(), "STOK")
    }

    private fun cleanNumber(input: String): Double {
        // Hapus semua yang bukan digit
        val digitsOnly = input.replace("\\D".toRegex(), "")
        return digitsOnly.toDoubleOrNull() ?: 0.0
    }

    private fun tambahBarang() {
        val nama = etNama.text.toString().trim()
        val stok = etStok.text.toString().toIntOrNull() ?: 0

        // BACA LANGSUNG DARI EditText yang sudah diformat
        val harga = cleanNumber(etHarga.text.toString())
        val modal = cleanNumber(etModal.text.toString())

        Log.d("Tmpbarang", ">>> harga=$harga, modal=$modal")

        // Validasi
        if (nama.isEmpty()) {
            etNama.error = "Nama wajib diisi"
            return
        }
        if (stok < 0) {
            etStok.error = "Stok minimal 0"
            return
        }
        if (harga <= 0) {
            etHarga.error = "Harga minimal Rp 1"
            return
        }
        if (modal < 0) {
            etModal.error = "Modal minimal Rp 0"
            return
        }

        val barang = Barang(
            nama = nama,
            kategori = formVm.kategori,
            stok = stok,
            harga = harga,
            modal = modal,
            barcode = formVm.barcode,
            gambarPath = formVm.gambarPath
        )
        barangViewModel.insertBarang(barang)
        navigateBack()
    }
}
