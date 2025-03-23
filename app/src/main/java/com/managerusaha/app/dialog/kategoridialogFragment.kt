package com.managerusaha.app.dialog

import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import android.view.LayoutInflater
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import com.managerusaha.app.R

class KategoriDialogFragment(private val onKategoriAdded: (String) -> Unit) : DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val view = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_kategori, null)

        val etKategori = view.findViewById<EditText>(R.id.et_nama_kategori)
        val btnSubmit = view.findViewById<Button>(R.id.btn_tambah_kategori)
        val btnCancel = view.findViewById<Button>(R.id.btn_batal)

        val dialog = AlertDialog.Builder(requireContext())
            .setView(view)
            .setCancelable(false)
            .create()

        btnSubmit.setOnClickListener {
            val namaKategori = etKategori.text.toString().trim()
            if (namaKategori.isNotEmpty()) {
                onKategoriAdded(namaKategori)
                dismiss()
            }
        }

        btnCancel.setOnClickListener {
            dismiss()
        }

        return dialog
    }
}