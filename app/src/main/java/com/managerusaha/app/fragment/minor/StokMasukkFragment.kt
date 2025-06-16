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
import com.managerusaha.app.R
import com.managerusaha.app.dialog.sheetstokListDialogFragment
import com.managerusaha.app.room.entity.Barang
import com.managerusaha.app.utills.adapter.KategoriAdapter
import com.managerusaha.app.utills.model.KategoryExpand
import com.managerusaha.app.viewmodel.BarangViewModel

class StokMasukkFragment : Fragment() {

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
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View =
        inflater.inflate(R.layout.fragment_stok_masukk, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews(view)
        setupRecycler()
        setupSearch()
        setupScan()
        observeData()
    }

    private fun initViews(v: View) {
        rv              = v.findViewById(R.id.recyclerView)
        searchInput     = v.findViewById(R.id.search_in)
        searchWrap      = v.findViewById(R.id.search_wrap)
        spinnerCategory = v.findViewById(R.id.category_spinner)
        scanBtn         = v.findViewById(R.id.ic_scan)

        // Putih status bar + icon gelap
        requireActivity().window.apply {
            statusBarColor = ContextCompat.getColor(context, R.color.white)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }
    }

    private fun setupRecycler() {
        adapter = KategoriAdapter(mutableListOf()) { barang ->
            showPopup(barang.id)
        }
        rv.layoutManager = LinearLayoutManager(requireContext())
        rv.adapter = adapter
        Log.d("StokMasukFrag", "RecyclerView siap")
    }

    private fun observeData() {
        vm.allBarang.observe(viewLifecycleOwner) { list ->
            fullList = list
            setupCategorySpinner()
            applyFilter()  // tampilkan semua di awal
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
        // icon search di TextInputLayout
        searchWrap.setStartIconOnClickListener {
            applyFilter()
        }
    }

    private fun setupScan() {
        // navigasi ke CamFragment
        scanBtn.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container,
                    CamFragment().apply {
                        arguments = Bundle().apply { putString("from", "stokmasuk") }
                    })
                .addToBackStack(null)
                .commit()
        }
        // terima hasil scan
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
        val q      = searchInput.text.toString().trim().lowercase()
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

        // rebuild adapter
        val grouped = filtered.groupBy { it.kategori ?: "Tanpa Kategori" }
        val out = grouped.map { (cat, items) -> KategoryExpand(cat, items) }
        adapter.updateData(out)
        Log.d("StokMasukFrag", "Filter => q='$q', cat='$selCat'")
    }

    private fun showPopup(id: Int) {
        sheetstokListDialogFragment().apply {
            arguments = Bundle().apply {
                putInt("barangid", id)
                putString("mode", "MASUK")
            }
        }.show(parentFragmentManager, "sheet")
    }
}
