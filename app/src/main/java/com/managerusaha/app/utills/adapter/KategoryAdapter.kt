package com.managerusaha.app.utills.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.managerusaha.app.R
import com.managerusaha.app.room.entity.Barang
import com.managerusaha.app.utills.model.KategoryExpand
import java.text.NumberFormat
import java.util.Locale

class KategoriAdapter(
    private val kategoriList: MutableList<KategoryExpand>,
    private val onBarangClick: (Barang) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val VIEW_TYPE_KATEGORI = 0
    private val VIEW_TYPE_BARANG = 1

    override fun getItemViewType(position: Int): Int {
        val item = getItem(position)
        return if (item is KategoryExpand) VIEW_TYPE_KATEGORI else VIEW_TYPE_BARANG
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        Log.d("KategoriAdapter", "Creating ViewHolder for type: $viewType")
        return if (viewType == VIEW_TYPE_KATEGORI) {
            val view = inflater.inflate(R.layout.item_kategori, parent, false)
            KategoriViewHolder(view)
        } else {
            val view = inflater.inflate(R.layout.item_barang, parent, false)
            BarangViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = getItem(position)
        Log.d("KategoriAdapter", "Binding position: $position, item: $item, holder type: ${holder.javaClass.simpleName}")

        if (holder is KategoriViewHolder && item is KategoryExpand) {
            holder.bind(item)
            Log.d("KategoriAdapter", "Bound kategori: ${item.nama}")
        } else if (holder is BarangViewHolder && item is Barang) {
            holder.bind(item)
            Log.d("KategoriAdapter", "Bound barang: ${item.nama}")
        }
    }

    override fun getItemCount(): Int {
        val count = kategoriList.sumOf { if (it.isExpanded) it.barangList.size + 1 else 1 }
        Log.d("KategoriAdapter", "Item count: $count")
        return count
    }

    private fun getItem(position: Int): Any {
        var count = 0
        for (kategori in kategoriList) {
            if (count == position) {
                Log.d("KategoriAdapter", "Getting kategori at position $position: ${kategori.nama}")
                return kategori
            }
            count++
            if (kategori.isExpanded) {
                if (position < count + kategori.barangList.size) {
                    val barang = kategori.barangList[position - count]
                    Log.d("KategoriAdapter", "Getting barang at position $position: ${barang.nama}")
                    return barang
                }
                count += kategori.barangList.size
            }
        }
        throw IllegalStateException("Invalid position")
    }

    inner class KategoriViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val kategoriText: TextView = view.findViewById(R.id.tvKategori)
        private val iconExpand: ImageView = view.findViewById(R.id.ivExpand)

        fun bind(kategori: KategoryExpand) {
            Log.d("KategoriViewHolder", "Binding kategori: ${kategori.nama}")
            kategoriText.text = kategori.nama
            iconExpand.setImageResource(if (kategori.isExpanded) R.drawable.ic_expand_less else R.drawable.ic_expand)

            itemView.setOnClickListener {
                Log.d("KategoriViewHolder", "Kategori clicked: ${kategori.nama}")
                kategori.isExpanded = !kategori.isExpanded
                notifyDataSetChanged()
            }
        }
    }

    inner class BarangViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            private val barangText: TextView = view.findViewById(R.id.tvBarang)
            private val stoktext: TextView = view.findViewById(R.id.tv_stok)
            private val hargatext: TextView = view.findViewById(R.id.tvHarga)
            private val ivbarang: ImageView = view.findViewById(R.id.iv_gambar)

            fun bind(barang: Barang) {
                Log.d("BarangViewHolder", "Binding barang: ${barang.nama}")
                if (barang.gambarPath != null){
                    ivbarang.setImageURI(android.net.Uri.parse(barang.gambarPath))
                }
                barangText.text = barang.nama
                stoktext.text = barang.stok.toString()
                val formatRupiah = NumberFormat.getCurrencyInstance(Locale("id", "ID"))
                formatRupiah.maximumFractionDigits = 0
                hargatext.text = formatRupiah.format(barang.harga).replace("Rp", "Rp ")



                itemView.setOnClickListener {
                    Log.d("BarangViewHolder", "Barang clicked: ${barang.nama}")
                    onBarangClick(barang)
                }
            }
        }
}
