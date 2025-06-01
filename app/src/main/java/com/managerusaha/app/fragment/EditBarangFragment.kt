package com.managerusaha.app.fragment

import TmpbarangFragment
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
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
    private lateinit var viewModel: BarangViewModel
    private val kategoriViewModel: KategoriViewModel by viewModels()

    private lateinit var etNama: EditText
    private lateinit var etKuantitas: EditText
    private lateinit var etHargaBeli: EditText
    private lateinit var etHargaJual: EditText
    private lateinit var categorySpinner: Spinner
    private lateinit var ivBarang: ImageView
    private lateinit var groub_back : FrameLayout
    private var kategoriNamaDariBarang: String? = null
    private var currentBarang: Barang? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        barangId = arguments?.getInt("barangId") ?: -1
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_edit_barang, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this)[BarangViewModel::class.java]
        inisalisasi(view)
        setupFormatHarga()
        setupKategoriSpinner()
        setuplistener()

        viewModel.getBarangById(barangId).observe(viewLifecycleOwner) { barang ->
            barang?.let {
                currentBarang = it
                etNama.setText(it.nama)
                etKuantitas.setText(it.stok.toString())
                etHargaBeli.setText(formatRupiah(it.modal))
                etHargaJual.setText(formatRupiah(it.harga))
                kategoriNamaDariBarang = it.kategori
                it.gambarPath?.let { path ->
                    ivBarang.setImageURI(Uri.parse(path))
                }
                updateSelectedSpinnerKategori()
            }
        }
    }

    private fun inisalisasi(view: View) {
        etNama = view.findViewById(R.id.et_nama)
        etKuantitas = view.findViewById(R.id.et_kuantitas)
        etHargaBeli = view.findViewById(R.id.et_harga_beli)
        etHargaJual = view.findViewById(R.id.et_harga_jual)
        categorySpinner = view.findViewById(R.id.category_spinner)
        ivBarang = view.findViewById(R.id.iv_barang)
        groub_back = view.findViewById(R.id.group_back)
    }

    private fun setuplistener() {
        val btnTambah = view?.findViewById<Button>(R.id.btn_tambah)
        val btnHapus = view?.findViewById<Button>(R.id.btn_hapus)
        groub_back.setOnClickListener {
            (activity as MainActivity).replaceFragment(StokFragment(), "Stok")
        }
        btnHapus?.setOnClickListener {
            currentBarang?.let { barang ->
                showDeleteConfirmation(barang)
            }
        }

        btnTambah?.setOnClickListener {
            UpdateBarang()
        }
    }

    private fun showDeleteConfirmation(barang: Barang) {
        AlertDialog.Builder(requireContext())
            .setTitle("Hapus Barang")
            .setMessage("Yakin ingin menghapus barang \"${barang.nama}\"?")
            .setPositiveButton("Hapus") { _, _ ->
                viewModel.deleteBarang(barang)
                Toast.makeText(requireContext(), "Barang dihapus", Toast.LENGTH_SHORT).show()
                (activity as MainActivity).replaceFragment(StokFragment(), "BarangList")
            }
            .setNegativeButton("Batal", null)
            .show()
    }

    private fun setupFormatHarga() {
        setupRupiahFormatter(etHargaBeli)
        setupRupiahFormatter(etHargaJual)
    }

    private fun updateSelectedSpinnerKategori() {
        val targetKategori = kategoriNamaDariBarang ?: return
        val adapter = categorySpinner.adapter ?: return

        for (i in 0 until adapter.count) {
            if (adapter.getItem(i).toString() == targetKategori) {
                categorySpinner.setSelection(i)
                break
            }
        }
    }

    private fun setupRupiahFormatter(editText: EditText) {
        editText.addTextChangedListener(object : TextWatcher {
            private var current = ""

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                if (s.toString() != current) {
                    editText.removeTextChangedListener(this)

                    val cleanString = s.toString().replace("[Rp,.\\s]".toRegex(), "")
                    val parsed = cleanString.toDoubleOrNull() ?: 0.0
                    val formatted = formatRupiah(parsed)

                    current = formatted
                    editText.setText(formatted)
                    editText.setSelection(formatted.length)

                    editText.addTextChangedListener(this)
                }
            }
        })
    }

    private fun formatRupiah(number: Double): String {
        val localeID = Locale("in", "ID")
        val format = NumberFormat.getCurrencyInstance(localeID)
        return format.format(number).replace(",00", "")
    }

    private fun setupKategoriSpinner() {
        kategoriViewModel.allKategori.observe(viewLifecycleOwner) { allKategori ->
            val categories = mutableListOf("Lainnya")
            categories.addAll(allKategori.map { it.nama })

            val categoryAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, categories)
            categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            categorySpinner.adapter = categoryAdapter

            updateSelectedSpinnerKategori()
        }
    }

    private fun getRawDouble(input: String): Double {
        val cleanString = input.replace("[^0-9]".toRegex(), "")
        return cleanString.toDoubleOrNull() ?: 0.0
    }

    private fun UpdateBarang() {
        val nama = etNama.text.toString().trim()
        val kuantitas = etKuantitas.text.toString().toIntOrNull() ?: 0
        val hargaBeli = getRawDouble(etHargaBeli.text.toString())
        val hargaJual = getRawDouble(etHargaJual.text.toString())
        val kategori = categorySpinner.selectedItem.toString()
        val gambarPath = currentBarang?.gambarPath

        if (nama.isEmpty()) {
            Toast.makeText(requireContext(), "Nama tidak boleh kosong", Toast.LENGTH_SHORT).show()
            return
        }

        if (currentBarang == null) {
            Toast.makeText(requireContext(), "Barang tidak ditemukan", Toast.LENGTH_SHORT).show()
            return
        }

        val updatedBarang = currentBarang!!.copy(
            nama = nama,
            stok = kuantitas,
            modal = hargaBeli,
            harga = hargaJual,
            kategori = kategori,
            gambarPath = gambarPath
        )

        viewModel.updateBarang(updatedBarang)
        Toast.makeText(requireContext(), "Barang berhasil diupdate", Toast.LENGTH_SHORT)

        (activity as MainActivity).replaceFragment(StokFragment(), "STOK")
    }
}