package com.managerusaha.app.dialog

import android.app.Dialog
import android.os.Bundle
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.bottomsheet.BottomSheetDialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.viewModels
import com.managerusaha.app.R
import com.managerusaha.app.viewmodel.BarangViewModel

class sheetstokListDialogFragment : BottomSheetDialogFragment() {
    private val barangViewModel: BarangViewModel by viewModels()

    private lateinit var btnCancel: Button
    private lateinit var btnSimpan: Button
    private lateinit var ic_plus: ImageView
    private lateinit var ic_minus: ImageView
    private lateinit var l_jumlah: TextView
    private lateinit var l_nambar: TextView
    private lateinit var l_judul: TextView

    private var barangid: Int? = null
    private var mode: String? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return BottomSheetDialog(requireContext(), R.style.BottomSheetDialog_Rounded)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.sheetstok_layout, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        inisialisasi(view)
        barangid = arguments?.getInt("barangid")
        mode = arguments?.getString("mode")
        modecheck()
        btnSimpan.setOnClickListener {
            barangid?.let { id ->
                if (mode == "KELUAR") {
                    barangViewModel.kurangStok(
                        barangId = id,
                        jumlah = l_jumlah.text.toString().toInt(),
                        tanggal = System.currentTimeMillis()
                    )
                    Toast.makeText(requireContext(), "Stok Telah Di Kurangi", Toast.LENGTH_SHORT).show()
                } else {
                    barangViewModel.tambahStok(
                        barangId = id,
                        jumlah = l_jumlah.text.toString().toInt(),
                        tanggal = System.currentTimeMillis()
                    )
                    Toast.makeText(requireContext(), "Stok Telah Di Tambahkan", Toast.LENGTH_SHORT).show()
                };dismiss()
            }
        }

        btnCancel.setOnClickListener { dismiss() }

        // ➕
        ic_plus.setOnClickListener {
            val jumlah = l_jumlah.text.toString().toIntOrNull() ?: 0
            l_jumlah.text = (jumlah + 1).toString()
        }

        // ➖
        ic_minus.setOnClickListener {
            val jumlah = l_jumlah.text.toString().toIntOrNull() ?: 1
            if (jumlah > 1) l_jumlah.text = (jumlah - 1).toString()
        }
    }

    private fun modecheck() {
        l_judul.text = if (mode == "KELUAR") "Stok Keluar" else "Stok Masuk"
        barangViewModel.getBarangById(barangid ?: 0).observe(viewLifecycleOwner) { barang ->
            l_nambar.text = barang.nama
        }
    }

    private fun inisialisasi(view: View) {
        btnCancel = view.findViewById(R.id.btn_cancel)
        btnSimpan = view.findViewById(R.id.btn_simpan)
        ic_plus = view.findViewById(R.id.ic_plus)
        ic_minus = view.findViewById(R.id.ic_minus)
        l_jumlah = view.findViewById(R.id.l_jumlah)
        l_nambar = view.findViewById(R.id.l_nambar)
        l_judul = view.findViewById(R.id.l_judul)
        l_jumlah.text = "1"
    }
}

