package com.managerusaha.app.fragment

import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
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
import com.managerusaha.app.viewmodel.BarangFormViewModel
import com.managerusaha.app.viewmodel.BarangViewModel
import com.managerusaha.app.viewmodel.KategoriViewModel
import java.text.NumberFormat
import java.util.*

class EditBarangFragment : Fragment() {

    private val barangViewModel: BarangViewModel by viewModels()
    private val kategoriViewModel: KategoriViewModel by viewModels()
    private val formVm: BarangFormViewModel by viewModels()

    private var barangId: Int = -1
    private var currentBarang: Barang? = null

    // Views
    private lateinit var ivBarang: ImageView
    private lateinit var etNama: EditText
    private lateinit var etStok: EditText
    private lateinit var spinnerKategori: Spinner
    private lateinit var etHarga: EditText
    private lateinit var etModal: EditText
    private lateinit var etHasilScan: TextInputEditText
    private lateinit var btnSimpan: Button
    private lateinit var btnHapus: Button
    private lateinit var btnBatal: FrameLayout
    private lateinit var plusIcon: ImageView
    private lateinit var icScan: ImageView
    private lateinit var pickImageLauncher: ActivityResultLauncher<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        barangId = arguments?.getInt("barangId") ?: -1
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_edit_barang, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews(view)
        setupImagePicker()
        setupFormatters()
        setupTextWatchers()
        setupScanListener()
        setupKategoriSpinner()
        setupButtons()
        observeBarang()
    }

    private fun initViews(root: View) {
        ivBarang = root.findViewById(R.id.iv_barang)
        etNama = root.findViewById(R.id.et_nama)
        etStok = root.findViewById(R.id.et_kuantitas)
        spinnerKategori = root.findViewById(R.id.category_spinner)
        etModal = root.findViewById(R.id.et_harga_beli)
        etHarga = root.findViewById(R.id.et_harga_jual)
        etHasilScan = root.findViewById(R.id.et_hasil_scan)
        btnSimpan = root.findViewById(R.id.btn_update)
        btnHapus = root.findViewById(R.id.btn_hapus)
        btnBatal = root.findViewById(R.id.group_back)
        plusIcon = root.findViewById(R.id.plus_ico)
        icScan = root.findViewById(R.id.ic_scan)
    }

    private fun setupImagePicker() {
        pickImageLauncher = registerForActivityResult(GetContent()) { uri ->
            uri?.let {
                requireContext().contentResolver
                    .takePersistableUriPermission(it, Intent.FLAG_GRANT_READ_URI_PERMISSION)
                ivBarang.setImageURI(it)
                formVm.gambarPath = it.toString()
            }
        }
        ivBarang.setOnClickListener { pickImageLauncher.launch("image/*") }
    }

    private fun setupFormatters() {
        listOf(etModal, etHarga).forEach { field ->
            field.doAfterTextChanged { s ->
                val clean = s.toString().replace("[Rp,.\\s]".toRegex(), "")
                val v = clean.toLongOrNull() ?: 0L
                val fmt = NumberFormat.getCurrencyInstance(Locale("id", "ID"))
                    .format(v)
                    .replace("Rp", "Rp.")
                    .replace(",00", "")
                if (fmt != field.text.toString()) {
                    field.setText(fmt)
                    field.setSelection(fmt.length)
                }
            }
        }
    }

    private fun setupTextWatchers() {
        etNama.doAfterTextChanged { formVm.nama = it.toString() }
        etStok.doAfterTextChanged { formVm.stok = it.toString() }
        etHarga.doAfterTextChanged { formVm.hargaJual = it.toString() }
        etModal.doAfterTextChanged { formVm.hargaModal = it.toString() }

        spinnerKategori.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {}

            override fun onItemSelected(
                parent: AdapterView<*>?,
                _view: View?,
                pos: Int,
                _id: Long
            ) {
                formVm.kategori = parent!!.getItemAtPosition(pos).toString()
            }
        }
    }


    private fun setupScanListener() {
        parentFragmentManager.setFragmentResultListener("scan_result", viewLifecycleOwner) { _, b ->
            formVm.barcode = b.getString("rawvalue", "")
            etHasilScan.setText(formVm.barcode)
        }
        icScan.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, com.managerusaha.app.fragment.minor.CamFragment())
                .addToBackStack(null)
                .commit()
        }
    }

    private fun setupKategoriSpinner() {
        kategoriViewModel.allKategori.observe(viewLifecycleOwner) { list ->
            val opts = mutableListOf("Lainnya").apply { addAll(list.map { it.nama }) }
            val ad = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, opts)
            ad.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerKategori.adapter = ad
            restoreFormState(ad)
        }
        plusIcon.setOnClickListener {
            KategoriDialogFragment { name ->
                kategoriViewModel.insertKategori(Kategori(nama = name))
            }.show(parentFragmentManager, "dlg")
        }
    }

    private fun setupButtons() {
        btnBatal.setOnClickListener { navigateBack() }
        btnSimpan.setOnClickListener { saveUpdate() }
        btnHapus.setOnClickListener { currentBarang?.let { confirmDelete(it) } }
    }

    private fun observeBarang() {
        barangViewModel.getBarangById(barangId).observe(viewLifecycleOwner) { b ->
            if (b == null) return@observe
            currentBarang = b
            // load into formVm
            formVm.apply {
                nama = b.nama
                stok = b.stok.toString()
                hargaJual = b.harga.toString()
                hargaModal = b.modal.toString()
                kategori = b.kategori ?: ""
                barcode = b.barcode ?: ""
                gambarPath = b.gambarPath
            }
            // restore into UI
            restoreFormState(spinnerKategori.adapter as ArrayAdapter<String>)
        }
    }

    private fun restoreFormState(adapter: ArrayAdapter<String>) {
        etNama.setText(formVm.nama)
        etStok.setText(formVm.stok)
        etHarga.setText(formVm.hargaJual)
        etModal.setText(formVm.hargaModal)
        etHasilScan.setText(formVm.barcode)
        formVm.gambarPath?.let { ivBarang.setImageURI(Uri.parse(it)) }
        val idx = adapter.getPosition(formVm.kategori)
        spinnerKategori.setSelection(idx.coerceAtLeast(0))
    }

    private fun cleanNumber(input: String): Double {
        return input.replace("\\D+".toRegex(), "").toDoubleOrNull() ?: 0.0
    }

    private fun saveUpdate() {
        val nama = formVm.nama.trim()
        val stok = formVm.stok.toIntOrNull() ?: 0
        val harga = cleanNumber(etHarga.text.toString())
        val modal = cleanNumber(etModal.text.toString())
        val kategori = formVm.kategori
        val barcode = formVm.barcode
        val gambarPath = formVm.gambarPath

        if (nama.isEmpty()) {
            etNama.error = "Nama wajib"; return
        }
        if (stok < 0) {
            etStok.error = "Stok minimal 0"; return
        }
        if (harga <= 0) {
            etHarga.error = "Harga minimal Rp1"; return
        }

        currentBarang?.let {
            val upd = it.copy(
                nama = nama,
                stok = stok,
                harga = harga,
                modal = modal,
                kategori = kategori,
                barcode = barcode,
                gambarPath = gambarPath
            )
            barangViewModel.updateBarang(upd)
        }
        navigateBack()
    }

    private fun confirmDelete(b: Barang) {
        AlertDialog.Builder(requireContext())
            .setTitle("Hapus")
            .setMessage("Hapus “${b.nama}”?")
            .setPositiveButton("Ya") { _, _ ->
                barangViewModel.deleteBarang(b)
                navigateBack()
            }
            .setNegativeButton("Batal", null)
            .show()
    }

    private fun navigateBack() {
        (activity as MainActivity).replaceFragment(
            com.managerusaha.app.fragment.StokFragment(), "STOK"
        )
    }
}
