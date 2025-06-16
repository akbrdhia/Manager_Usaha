package com.managerusaha.app.fragment.minor

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.managerusaha.app.MainActivity
import com.managerusaha.app.R
import com.managerusaha.app.dialog.sheetstokListDialogFragment
import com.managerusaha.app.room.entity.Barang
import com.managerusaha.app.utills.adapter.KategoriAdapter
import com.managerusaha.app.utills.model.KategoryExpand
import com.managerusaha.app.viewmodel.BarangViewModel

class StokKeluarrFragment : Fragment() {

    private val vm: BarangViewModel by viewModels()

    private lateinit var rv: RecyclerView
    private lateinit var adapter: KategoriAdapter
    private lateinit var searchInput: TextInputEditText
    private lateinit var searchWrap: TextInputLayout
    private lateinit var spinnerCategory: Spinner
    private lateinit var scanBtn: ImageView

    // full list all Barang
    private var fullList: List<Barang> = emptyList()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_stok_keluarr, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews(view)         // ⚠️ ini harus sebelum setup apa pun
        setupRecycler()
        setupSearch()           // pakai searchInput & searchWrap
        setupScan()
        observeData()           // di dalamnya akan memanggil applyFilter()
    }

    private fun initViews(v: View) {
        rv              = v.findViewById(R.id.recyclerView)
        searchInput     = v.findViewById(R.id.search_in)
        searchWrap      = v.findViewById(R.id.search_wrap)
        spinnerCategory = v.findViewById(R.id.spinner_category)
        scanBtn         = v.findViewById(R.id.ic_scan)

        // set statusbar putih + ikon gelap
        requireActivity().window.apply {
            statusBarColor = ContextCompat.getColor(context, R.color.white)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            }
        }
    }

    private fun setupRecycler() {
        adapter = KategoriAdapter(mutableListOf()) { barang ->
            showPopup(barang.id)
        }
        rv.layoutManager = LinearLayoutManager(requireContext())
        rv.adapter = adapter
    }

    private fun observeData() {
        vm.allBarang.observe(viewLifecycleOwner) { list ->
            fullList = list
            setupCategorySpinner() // isi spinner kategori
            applyFilter()          // tampilkan semua dulu
        }
    }

    private fun setupCategorySpinner() {
        val cats = listOf("Semua") +
                fullList.map { it.kategori ?: "Tanpa Kategori" }.distinct()
        spinnerCategory.adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            cats
        ).also { it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item) }
    }

    private fun setupSearch() {
        // ketika ikon search (di dalam TextInputLayout) diklik
        searchWrap.setStartIconOnClickListener {
            applyFilter()
        }
    }

    private fun setupScan() {
        scanBtn.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container,
                    CamFragment().apply {
                        arguments = Bundle().apply { putString("from", "stokkeluar") }
                    })
                .addToBackStack(null)
                .commit()
        }
        parentFragmentManager.setFragmentResultListener(
            "scan_result", viewLifecycleOwner
        ) { _, bundle ->
            bundle.getString("rawvalue")?.let { code ->
                vm.findByBarcode(code) { found ->
                    if (found != null) showPopup(found.id)
                    else Toast.makeText(
                        requireContext(),
                        "Barang barcode=$code tidak ditemukan",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun applyFilter() {
        val q = searchInput.text.toString().trim().lowercase()
        val selCat = spinnerCategory.selectedItem as String

        var filtered = fullList
        if (q.isNotEmpty()) {
            filtered = filtered.filter {
                it.nama.lowercase().contains(q)
            }
        }
        if (selCat != "Semua") {
            filtered = filtered.filter { it.kategori == selCat }
        }

        // rebuild grouped adapter
        val grp = filtered.groupBy { it.kategori ?: "Tanpa Kategori" }
        val out = grp.map { (cat, items) -> KategoryExpand(cat, items) }
        adapter.updateData(out)
    }

    private fun showPopup(id: Int) {
        sheetstokListDialogFragment().apply {
            arguments = Bundle().apply {
                putInt("barangid", id)
                putString("mode", "KELUAR")
            }
        }.show(parentFragmentManager, "sheet")
    }
}
