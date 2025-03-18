package com.managerusaha.app.dialog

import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import android.view.LayoutInflater
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import com.managerusaha.app.R

class KategoriDialogFragment : DialogFragment() {

    interface KategoriDialogListener {
        fun onKategoriAdded(nama: String)
    }

    private var listener: KategoriDialogListener? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(requireContext())
        val inflater = requireActivity().layoutInflater
        val view = inflater.inflate(R.layout.dialog_kategori, null)

        val etNamaKategori: EditText = view.findViewById(R.id.et_nama_kategori)
        val btnTambah: Button = view.findViewById(R.id.btn_tambah_kategori)

        builder.setView(view)
        val dialog = builder.create()

        btnTambah.setOnClickListener {
            val nama = etNamaKategori.text.toString().trim()
            if (nama.isNotEmpty()) {
                listener?.onKategoriAdded(nama)
                dialog.dismiss()
            } else {
                etNamaKategori.error = "Nama kategori harus diisi!"
            }
        }

        return dialog
    }

    fun setListener(listener: KategoriDialogListener) {
        this.listener = listener
    }

    companion object {
        fun newInstance(): KategoriDialogFragment {
            return KategoriDialogFragment()
        }
    }
}
