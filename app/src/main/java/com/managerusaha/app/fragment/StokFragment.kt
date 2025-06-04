package com.managerusaha.app.fragment

import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.managerusaha.app.MainActivity
import com.managerusaha.app.R
import com.managerusaha.app.room.AppDatabase
import com.managerusaha.app.room.entity.Barang
import com.managerusaha.app.utills.adapter.KategoriAdapter
import com.managerusaha.app.utills.model.KategoryExpand
import com.managerusaha.app.viewmodel.KategoriViewModel

class StokFragment : Fragment() {
    private val kategoriViewModel: KategoriViewModel by viewModels()

    private lateinit var searchInput: TextInputEditText
    private lateinit var searchWrap: TextInputLayout
    private lateinit var spinnerCategory: Spinner
    private lateinit var spinnerFilter: Spinner
    private lateinit var button: Button
    private lateinit var recyclerView: RecyclerView
    private lateinit var kategoriAdapter: KategoriAdapter
    private lateinit var database: AppDatabase

    // Simpan semua barang untuk nanti difilter
    private var allBarangList: List<Barang> = emptyList()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_stok, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        checkStatusBar()
        inisialisasi(view)
        setupRecyclerView()
        setupSpinners()
        loadBarangData()

        button.setOnClickListener {
            (activity as MainActivity).replaceFragment(TmpbarangFragment(), "AddBarang")
        }
    }

    private fun inisialisasi(view: View) {
        searchInput = view.findViewById(R.id.search_in)
        searchWrap = view.findViewById(R.id.search_wrap)
        spinnerCategory = view.findViewById(R.id.category_spinner)
        spinnerFilter = view.findViewById(R.id.filter_spinner)
        recyclerView = view.findViewById(R.id.recyclerView)
        button = view.findViewById(R.id.button2)
        database = AppDatabase.getDatabase(requireContext())
    }

    private fun setupRecyclerView() {
        recyclerView.layoutManager = LinearLayoutManager(context)
        // Inisialisasi adapter dengan daftar kosong
        kategoriAdapter = KategoriAdapter(mutableListOf()) { barang ->
            navigateToEditBarang(barang.id)
        }
        recyclerView.adapter = kategoriAdapter
        Log.d("StokFragment", "RecyclerView setup completed")
    }

    private fun setupSpinners() {
        // Spinner filter
        val filterOptions = listOf("Banyak Stok", "Sedikit Stok", "Mahal Harga")
        val filterAdapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            filterOptions
        )
        filterAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerFilter.adapter = filterAdapter

        // Spinner kategori: data di‐observe kemudian di‐set
        kategoriViewModel.allKategori.observe(viewLifecycleOwner) { allKategori ->
            val categories = mutableListOf("all")
            categories.addAll(allKategori.map { it.nama })
            val categoryAdapter = ArrayAdapter(
                requireContext(),
                android.R.layout.simple_spinner_item,
                categories
            )
            categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerCategory.adapter = categoryAdapter
        }

        // Listener ikon search: saat diklik, ambil nilai dan filter
        searchWrap.setStartIconOnClickListener {
            val searchText = searchInput.text.toString().trim()
            val selectedCategory = spinnerCategory.selectedItem.toString()
            val selectedFilter = spinnerFilter.selectedItem.toString()
            filterAndDisplayBarang(searchText, selectedCategory, selectedFilter)
        }
    }

    private fun loadBarangData() {
        Log.d("StokFragment", "Starting to load barang data")
        database.barangDao().getAllBarang().observe(viewLifecycleOwner) { barangList ->
            Log.d("StokFragment", "Received barang list with size: ${barangList.size}")
            // Simpan dahulu semua barang
            allBarangList = barangList

            // Siapkan tampilan awal tanpa filter
            val kategoriMap = barangList.groupBy { it.kategori ?: "Tanpa Kategori" }
            val kategoriList = kategoriMap.map { (key, value) ->
                Log.d("StokFragment", "Category: $key, Items: ${value.size}")
                KategoryExpand(key, value)
            }

            kategoriAdapter.updateData(kategoriList.toMutableList())
            Log.d("StokFragment", "Adapter updated with new data")
        }
    }

    private fun filterAndDisplayBarang(
        searchText: String,
        selectedCategory: String,
        selectedFilter: String
    ) {
        var filteredList = allBarangList

        // Filter berdasarkan nama
        if (searchText.isNotEmpty()) {
            filteredList = filteredList.filter {
                it.nama.contains(searchText, ignoreCase = true)
            }
        }

        // Filter berdasarkan kategori
        if (selectedCategory.lowercase() != "all") {
            filteredList = filteredList.filter { it.kategori == selectedCategory }
        }


        filteredList = when (selectedFilter) {
            "Banyak Stok" -> filteredList.sortedByDescending { it.stok }
            "Sedikit Stok" -> filteredList.sortedBy { it.stok }
            "Mahal Harga" -> filteredList.sortedByDescending { it.harga }
            else -> filteredList
        }

        val grouped = filteredList.groupBy { it.kategori ?: "Tanpa Kategori" }
        val result = grouped.map { (kategori, listBarang) ->
            KategoryExpand(kategori, listBarang)
        }
        kategoriAdapter.updateData(result.toMutableList())
    }

    private fun navigateToEditBarang(barangId: Int) {
        val fragment = EditBarangFragment()
        val bundle = Bundle().apply {
            putInt("barangId", barangId)
        }
        fragment.arguments = bundle
        (activity as MainActivity).replaceFragment(fragment, "EditBarang")
    }

    private fun checkStatusBar() {
        val window = requireActivity().window
        window.statusBarColor = ContextCompat.getColor(requireContext(), R.color.white)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }
    }
}
