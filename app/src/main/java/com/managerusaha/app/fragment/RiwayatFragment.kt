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
import com.managerusaha.app.utills.model.RiwayatWithNama
import com.managerusaha.app.viewmodel.BarangViewModel
import com.managerusaha.app.viewmodel.riwayatViewModel

class RiwayatFragment : Fragment() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: RiwayatAdapter
    private val riwayatViewModel: riwayatViewModel by viewModels()
    private val barangViewModel: BarangViewModel by viewModels()
    private lateinit var searchInput: TextInputEditText
    private lateinit var searchWrap: TextInputLayout
    private lateinit var spinnerCategory: Spinner

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_riwayat, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        checkStatusBar()
        searchInput = view.findViewById(R.id.search_in)
        searchWrap = view.findViewById(R.id.search_wrap)
        spinnerCategory = view.findViewById(R.id.category_spinner)
        recyclerView = view.findViewById(R.id.recycler_riwayat)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        adapter = RiwayatAdapter(emptyList())
        recyclerView.adapter = adapter
        barangViewModel.allBarang.observe(viewLifecycleOwner) { barangList ->
            riwayatViewModel.allriwayat.observe(viewLifecycleOwner) { riwayatList ->
                val combined = riwayatList.mapNotNull { r ->
                    val barang = barangList.find { it.id == r.barangId }
                    barang?.let {
                        RiwayatWithNama(
                            id = r.id,
                            barangId = r.barangId,
                            namaBarang = it.nama,
                            tanggal = r.tanggal,
                            jumlah = r.jumlah,
                            tipe = r.tipe
                        )
                    }
                }
                adapter.updateData(combined)
            }
        }

        setdefault()
    }


    private fun setdefault() {
        val categories = listOf("All", "Audit", "Keluar", "Masuk")
        val categoryAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, categories)
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerCategory.adapter = categoryAdapter

        searchWrap.setStartIconOnClickListener {
            val searchText = searchInput.text.toString().trim()
            val selectedCategory = spinnerCategory.selectedItem.toString()

            Toast.makeText(requireContext(), "Cari: $searchText di $selectedCategory", Toast.LENGTH_SHORT).show()
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