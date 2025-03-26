package com.managerusaha.app.fragment

import TmpbarangFragment
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
import com.managerusaha.app.utills.adapter.KategoriAdapter
import com.managerusaha.app.utills.model.KategoryExpand
import com.managerusaha.app.viewmodel.KategoriViewModel

class StokFragment : Fragment() {
    private val kategoriViewModel : KategoriViewModel by viewModels()

    private lateinit var searchInput: TextInputEditText
    private lateinit var searchWrap: TextInputLayout
    private lateinit var spinnerCategory: Spinner
    private lateinit var spinnerFilter: Spinner
    private  lateinit var button: Button
    private lateinit var recyclerView: RecyclerView
    private lateinit var kategoriAdapter: KategoriAdapter
    private lateinit var database: AppDatabase


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_stok, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        checkStatusBar()
        
        // Inisialisasi views
        searchInput = view.findViewById(R.id.search_in)
        searchWrap = view.findViewById(R.id.search_wrap)
        spinnerCategory = view.findViewById(R.id.category_spinner)
        spinnerFilter = view.findViewById(R.id.filter_spinner)
        recyclerView = view.findViewById(R.id.recyclerView)
        button = view.findViewById(R.id.button2)

        // Inisialisasi database
        database = AppDatabase.getDatabase(requireContext())

        // Setup RecyclerView
        setupRecyclerView()
        
        // Load data
        loadBarangData()
        
        // Setup button click
        button.setOnClickListener {
            (activity as MainActivity).replaceFragment(TmpbarangFragment(), " ")
        }
        
        setDefault()
    }

    private fun setupRecyclerView() {
        recyclerView.layoutManager = LinearLayoutManager(context)
        // Inisialisasi adapter dengan list kosong terlebih dahulu
        kategoriAdapter = KategoriAdapter(mutableListOf()) { barang ->
            navigateToEditBarang(barang.id)
        }
        recyclerView.adapter = kategoriAdapter
        Log.d("StokFragment", "RecyclerView setup completed")
    }

    private fun loadBarangData() {
        Log.d("StokFragment", "Starting to load barang data")
        database.barangDao().getAllBarang().observe(viewLifecycleOwner) { barangList ->
            Log.d("StokFragment", "Received barang list with size: ${barangList.size}")

            if (barangList.isEmpty()) {
                Log.d("StokFragment", "Barang list is empty")
                return@observe
            }

            val kategoriMap = barangList.groupBy { it.kategori ?: "Tanpa Kategori" }
            Log.d("StokFragment", "Grouped into ${kategoriMap.size} categories")

            val kategoriList = kategoriMap.map { (key, value) ->
                Log.d("StokFragment", "Category: $key, Items: ${value.size}")
                KategoryExpand(key, value)
            }

            // Update adapter dengan data baru
            kategoriAdapter = KategoriAdapter(kategoriList.toMutableList()) { barang ->
                navigateToEditBarang(barang.id)
            }
            recyclerView.adapter = kategoriAdapter
            Log.d("StokFragment", "Adapter updated with new data")
        }
    }

    private fun navigateToEditBarang(barangId: Int) {
        val fragment = EditBarangFragment()
        val bundle = Bundle()
        bundle.putInt("barangId", barangId)
        fragment.arguments = bundle

        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .addToBackStack(null)
            .commit()
    }

    private fun checkStatusBar() {
        val window = requireActivity().window
        window.statusBarColor = ContextCompat.getColor(requireContext(), R.color.white)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }
    }

    private fun setDefault() {
            setcategory()
            setfilter()
        searchWrap.setStartIconOnClickListener {
            val searchText = searchInput.text.toString().trim()
            val selectedCategory = spinnerCategory.selectedItem.toString()
            val selectedFilter = spinnerFilter.selectedItem.toString()

            Toast.makeText(requireContext(), "Cari: $searchText di $selectedCategory Dengan Format $selectedFilter", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setfilter() {
        val filterOptions = listOf("Banyak Stok", "Sedikit Stok", "Mahal Harga")
        val filterAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, filterOptions)
        filterAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerFilter.adapter = filterAdapter
    }

    private fun setcategory() {
        kategoriViewModel.allKategori.observe(viewLifecycleOwner) { allkategory ->
            val categories = mutableListOf("all")
            categories.addAll(allkategory.map { it.nama })

            val categoryAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, categories)
            categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerCategory.adapter = categoryAdapter
        }
    }
}
