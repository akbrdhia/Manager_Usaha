package com.managerusaha.app.fragment.minor

import android.app.Dialog
import android.os.Bundle
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.bottomsheet.BottomSheetDialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import com.managerusaha.app.R

class sheetstokListDialogFragment : BottomSheetDialogFragment() {

    private lateinit var btnCancel: Button
    private lateinit var btnSimpan: Button
    private lateinit var ic_plus: ImageView
    private lateinit var ic_minus: ImageView
    private lateinit var l_jumlah : TextView

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
        btnSimpan.setOnClickListener {

        }
        btnCancel.setOnClickListener {
            dismiss()
        }
        ic_plus.setOnClickListener {
            var jumlah = l_jumlah.text.toString().toInt()
            jumlah++
            l_jumlah.text = jumlah.toString()
        }
        ic_minus.setOnClickListener {
            var jumlah = l_jumlah.text.toString().toInt()
            if (jumlah > 1) {
                jumlah--
                l_jumlah.text = jumlah.toString()
            }
        }
    }

    private fun inisialisasi(view: View) {
        btnCancel = view.findViewById(R.id.btn_cancel)
        btnSimpan = view.findViewById(R.id.btn_simpan)
        ic_plus = view.findViewById(R.id.ic_plus)
        ic_minus = view.findViewById(R.id.ic_minus)
        l_jumlah = view.findViewById(R.id.l_jumlah)
        l_jumlah.text = "1"
    }
}
