package com.managerusaha.app.fragment.minor

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.managerusaha.app.MainActivity
import com.managerusaha.app.R
import com.managerusaha.app.dialog.sheetstokListDialogFragment
import com.managerusaha.app.room.AppDatabase
import com.managerusaha.app.utills.adapter.KategoriAdapter
import com.managerusaha.app.utills.model.KategoryExpand

class StokKeluarrFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var kategoriAdapter: KategoriAdapter
    private lateinit var database: AppDatabase
    private lateinit var scan: ImageView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_stok_keluarr, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        inisialisasi(view)
        database = AppDatabase.getDatabase(requireContext())
        setupRecyclerView()
        loadBarangData()
        handleItemClick()
    }

    private fun handleItemClick() {
        scan.setOnClickListener {
            val camFragment = CamFragment().apply {
                arguments = Bundle().apply {
                    putString("from", "stokkeluar")
                }
            }

            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, camFragment)
                .addToBackStack(null) // ini penting biar bisa "balik"
                .commit()
        }


    }

    private fun inisialisasi(view: View) {
        recyclerView = view.findViewById(R.id.recyclerView)
        scan = view.findViewById(R.id.ic_scan)

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

            kategoriAdapter = KategoriAdapter(kategoriList.toMutableList()) { barang ->
               navigatetopopup(barang.id)
            }
            recyclerView.adapter = kategoriAdapter
            Log.d("StokFragment", "Adapter updated with new data")
        }
    }



    private fun navigatetopopup(barangid: Int) {
        val sheet = sheetstokListDialogFragment()
        val bundle = Bundle()
        bundle.putInt("barangid", barangid)
        bundle.putString("mode", "KELUAR")
        sheet.arguments = bundle
        Log.d("StokFragment", "Navigating to popup with barangid: $barangid")
        sheet.show(parentFragmentManager, "sheet")
    }

    private fun setupRecyclerView() {
        recyclerView.layoutManager = LinearLayoutManager(context)
        kategoriAdapter = KategoriAdapter(mutableListOf()) { barang ->
           //wait
        }
        recyclerView.adapter = kategoriAdapter
        Log.d("StokFragment", "RecyclerView setup completed")
    }

}