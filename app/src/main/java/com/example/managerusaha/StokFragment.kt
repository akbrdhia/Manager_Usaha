package com.example.managerusaha

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

class StokFragment : Fragment() {

    private lateinit var searchInput: TextInputEditText
    private lateinit var searchWrap: TextInputLayout
    private lateinit var spinner: Spinner

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
        spinner = view.findViewById(R.id.category_spinner)
        setdefault()

    }
    private fun checkStatusBar() {
        val window = requireActivity().window
        window.statusBarColor = ContextCompat.getColor(requireContext(), R.color.white)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }
    }

    fun setdefault(){
        val categories = listOf("Semua", "Makanan", "Minuman", "Other")
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, categories)
        spinner.adapter = adapter

        searchWrap.setStartIconOnClickListener {
            val searchText = searchInput.text.toString().trim()
            val selectedCategory = spinner.selectedItem.toString()

            Toast.makeText(requireContext(), "Cari: $searchText di $selectedCategory", Toast.LENGTH_SHORT).show()
        }
    }
}