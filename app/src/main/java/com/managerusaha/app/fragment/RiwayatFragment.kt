package com.managerusaha.app.fragment

import RiwayatAdapter
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.managerusaha.app.R
import com.managerusaha.app.utills.model.RiwayatWithBarang
import com.managerusaha.app.viewmodel.riwayatViewModel

class RiwayatFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var riwayatAdapter: RiwayatAdapter
    private val riwayatViewModel: riwayatViewModel by viewModels()

    private lateinit var searchInput: TextInputEditText
    private lateinit var searchWrap: TextInputLayout
    private lateinit var spinnerCategory: Spinner

    private var originalData: List<RiwayatWithBarang> = listOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_riwayat, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        checkStatusBar()

        // Inisialisasi View
        recyclerView = view.findViewById(R.id.recycler_riwayat)
        searchInput = view.findViewById(R.id.search_in)
        searchWrap = view.findViewById(R.id.search_wrap)
        spinnerCategory = view.findViewById(R.id.category_spinner)

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        riwayatAdapter = RiwayatAdapter(emptyList())
        recyclerView.adapter = riwayatAdapter

        // Observasi data
        riwayatViewModel.allRiwayatWithBarang.observe(viewLifecycleOwner) { list ->
            originalData = list
            riwayatAdapter.updateData(list)
        }

        setupFilterUI()
    }

    private fun setupFilterUI() {
        val categories = listOf("All", "Keluar", "Masuk")
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, categories)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerCategory.adapter = adapter

        searchWrap.setStartIconOnClickListener {
            val keyword = searchInput.text.toString().trim().lowercase()
            val selectedCategory = spinnerCategory.selectedItem.toString()

            val filtered = originalData.filter { riwayat ->
                val matchNama = riwayat.nama.lowercase().contains(keyword)
                val matchKategori = when (selectedCategory) {
                    "All" -> true
                    else -> riwayat.tipe.equals(selectedCategory, ignoreCase = true)
                }
                matchNama && matchKategori
            }


            riwayatAdapter.updateData(filtered)

            Toast.makeText(
                requireContext(),
                "Menampilkan ${filtered.size} hasil untuk \"$keyword\" di kategori \"$selectedCategory\"",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun checkStatusBar() {
        val window = requireActivity().window
        window.statusBarColor = ContextCompat.getColor(requireContext(), R.color.white)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }
    }
}
