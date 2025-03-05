package com.managerusaha.app.fragment

import Barang
import BarangViewModel
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.managerusaha.app.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

class TmpbarangFragment : Fragment() {
    private lateinit var barangViewModel: BarangViewModel
    private lateinit var ivBarang: ImageView
    private lateinit var etNama: EditText
    private lateinit var etStok: EditText
    private lateinit var etHarga: EditText
    private lateinit var etModal: EditText
    private lateinit var btnTambah: Button
    private var selectedImagePath: String? = null

    private lateinit var pickImageLauncher: ActivityResultLauncher<String>


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_tmpbarang, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        barangViewModel = ViewModelProvider(this)[BarangViewModel::class.java]

        ivBarang = view.findViewById(R.id.iv_barang)
        etNama = view.findViewById(R.id.et_nama)
        etStok = view.findViewById(R.id.et_kuantitas)
        etHarga = view.findViewById(R.id.et_harga_jual)
        etModal = view.findViewById(R.id.et_harga_beli)
        btnTambah = view.findViewById(R.id.btn_tambah)

        pickImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let {
                val filePath = saveImageToInternalStorage(it)
                selectedImagePath = filePath
                ivBarang.setImageURI(it)
            }
        }


        ivBarang.setOnClickListener {
            pickImageLauncher.launch("image/*")
        }

        btnTambah.setOnClickListener {
            saveBarang()
        }
    }

    private fun saveBarang() {
        val nama = etNama.text.toString()
        val stok = etStok.text.toString().toIntOrNull() ?: 0
        val harga = etHarga.text.toString().toDoubleOrNull() ?: 0.0
        val modal = etModal.text.toString().toDoubleOrNull() ?: 0.0

        if (nama.isNotEmpty()) {
            val barang = Barang(
                nama = nama,
                kategoriId = 1,
                stok = stok,
                harga = harga,
                modal = modal,
                gambarPath = selectedImagePath
            )

            viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
                barangViewModel.insertBarang(barang)
                withContext(Dispatchers.Main) {
                    Toast.makeText(requireContext(), "Barang ditambahkan!", Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            Toast.makeText(requireContext(), "Nama barang harus diisi!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun saveImageToInternalStorage(uri: Uri): String {
        val file = File(requireContext().filesDir, "images")
        if (!file.exists()) file.mkdirs()

        val fileName = "IMG_${System.currentTimeMillis()}.jpg"
        val destFile = File(file, fileName)

        try {
            requireContext().contentResolver.openInputStream(uri).use { inputStream ->
                FileOutputStream(destFile).use { outputStream ->
                    inputStream?.copyTo(outputStream)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return destFile.absolutePath
    }
}

