package com.managerusaha.app.fragment

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
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.managerusaha.app.R

class StokFragment : Fragment() {

    private lateinit var searchInput: TextInputEditText
    private lateinit var searchWrap: TextInputLayout
    private lateinit var spinnerCategory: Spinner
    private lateinit var spinnerFilter: Spinner

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_stok, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        checkStatusBar()
        searchInput = view.findViewById(R.id.search_in)
        searchWrap = view.findViewById(R.id.search_wrap)
        spinnerCategory = view.findViewById(R.id.category_spinner)
        spinnerFilter = view.findViewById(R.id.filter_spinner)
        setDefault()
    }

    private fun checkStatusBar() {
        val window = requireActivity().window
        window.statusBarColor = ContextCompat.getColor(requireContext(), R.color.white)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }
    }

    private fun setDefault() {
        setcategory();setfiltter()
        searchWrap.setStartIconOnClickListener {
            val searchText = searchInput.text.toString().trim()
            val selectedCategory = spinnerCategory.selectedItem.toString()
            val selectedFilter = spinnerFilter.selectedItem.toString()

            Toast.makeText(requireContext(), "Cari: $searchText di $selectedCategory Dengan Format $selectedFilter", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setfiltter() {
        val filterOptions = listOf("Banyak Stok", "Sedikit Stok", "Mahal Harga")
        val filterAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, filterOptions)
        filterAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerFilter.adapter = filterAdapter
    }

    private fun setcategory() {
        val categories = listOf("Semua", "Makanan", "Minuman", "Other")
        val categoryAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, categories)
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerCategory.adapter = categoryAdapter
    }
}
