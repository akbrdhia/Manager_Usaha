package com.managerusaha.app.utills.adapter

import android.graphics.ImageDecoder
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.managerusaha.app.R
import com.managerusaha.app.room.entity.Barang
import com.managerusaha.app.utills.model.KategoryExpand
import java.text.NumberFormat
import java.util.Locale


class KategoriAdapter(
    private var kategoriList: MutableList<KategoryExpand>,
    private val onBarangClick: (Barang) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    sealed class ListItem {
        data class Category(val nama: String, val isExpanded: Boolean) : ListItem()
        data class ItemBarang(val barang: Barang) : ListItem()
    }

    private val VIEW_TYPE_KATEGORI = 0
    private val VIEW_TYPE_BARANG = 1

    // Daftar flattened yang di‐rebuild setiap kali kategoriList berubah atau expand/collapse:
    private var flattenedList: MutableList<ListItem> = mutableListOf()

    init {
        buildFlattenedList()
    }

    /** Membangun ulang flattenedList sesuai state kategoriList saat ini */
    private fun buildFlattenedList() {
        flattenedList.clear()
        for (kategori in kategoriList) {
            // Tambah header kategori
            flattenedList.add(ListItem.Category(kategori.nama, kategori.isExpanded))
            if (kategori.isExpanded) {
                // Jika expanded, tambahkan setiap barang di bawahnya
                for (barang in kategori.barangList) {
                    flattenedList.add(ListItem.ItemBarang(barang))
                }
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (flattenedList[position]) {
            is ListItem.Category -> VIEW_TYPE_KATEGORI
            is ListItem.ItemBarang -> VIEW_TYPE_BARANG
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return if (viewType == VIEW_TYPE_KATEGORI) {
            val view = inflater.inflate(R.layout.item_kategori, parent, false)
            KategoriViewHolder(view)
        } else {
            val view = inflater.inflate(R.layout.item_barang, parent, false)
            BarangViewHolder(view)
        }
    }

    @RequiresApi(Build.VERSION_CODES.P)
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val item = flattenedList[position]) {
            is ListItem.Category -> {
                (holder as KategoriViewHolder).bind(item, position)
            }
            is ListItem.ItemBarang -> {
                (holder as BarangViewHolder).bind(item.barang)
            }
        }
    }

    override fun getItemCount(): Int = flattenedList.size

    /**
     * Memperbarui seluruh kategoriList dengan list baru, lalu rebuild flattenedList
     * dan notifikasi agar UI ter‐refresh sekali.
     */
    fun updateData(newKategoriList: List<KategoryExpand>) {
        kategoriList.clear()
        kategoriList.addAll(newKategoriList)
        buildFlattenedList()
        notifyDataSetChanged()
    }

    inner class KategoriViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val kategoriText: TextView = view.findViewById(R.id.tvKategori)
        private val iconExpand: ImageView = view.findViewById(R.id.ivExpand)

        fun bind(item: ListItem.Category, position: Int) {
            kategoriText.text = item.nama
            iconExpand.setImageResource(
                if (item.isExpanded) R.drawable.ic_expand_less else R.drawable.ic_expand
            )

            itemView.setOnClickListener {
                // Toggle isExpanded pada KategoryExpand yang sesuai nama-nya
                val idx = kategoriList.indexOfFirst { it.nama == item.nama }
                if (idx != -1) {
                    val kategoriObj = kategoriList[idx]
                    kategoriObj.isExpanded = !kategoriObj.isExpanded
                    buildFlattenedList()
                    notifyDataSetChanged()
                }
            }
        }
    }

    inner class BarangViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val barangText: TextView = view.findViewById(R.id.tvBarang)
        private val stokText: TextView = view.findViewById(R.id.tv_stok)
        private val hargaText: TextView = view.findViewById(R.id.tvHarga)
        private val ivBarang: ImageView = view.findViewById(R.id.iv_gambar)

        @RequiresApi(Build.VERSION_CODES.P)
        fun bind(barang: Barang) {
            barangText.text = barang.nama
            stokText.text = barang.stok.toString()
            val formatRupiah = NumberFormat.getCurrencyInstance(Locale("id", "ID"))
            formatRupiah.maximumFractionDigits = 0
            hargaText.text = formatRupiah.format(barang.harga).replace("Rp", "Rp ")

            if (!barang.gambarPath.isNullOrEmpty()) {
                try {
                    val source = ImageDecoder.createSource(
                        ivBarang.context.contentResolver,
                        android.net.Uri.parse(barang.gambarPath)
                    )
                    ivBarang.setImageBitmap(ImageDecoder.decodeBitmap(source))
                } catch (e: Exception) {
                    e.printStackTrace()
                    ivBarang.setImageResource(R.drawable.grey_round) // fallback kalau error
                }
            } else {
                // ← PENTING: reset placeholder jika tidak ada gambar
                ivBarang.setImageResource(R.drawable.grey_round)
            }

            itemView.setOnClickListener {
                onBarangClick(barang)
            }
        }
    }

}
