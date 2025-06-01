import android.annotation.SuppressLint
import android.graphics.BitmapFactory
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.managerusaha.app.R
import com.managerusaha.app.utills.model.RiwayatWithBarang
import java.text.SimpleDateFormat
import java.util.*

class RiwayatAdapter(private var riwayatList: List<RiwayatWithBarang>) :
    RecyclerView.Adapter<RiwayatAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val ivGambar: ImageView = view.findViewById(R.id.iv_gambarr)
        val tvBarang: TextView = view.findViewById(R.id.tv_barangg)
        val tvStok: TextView = view.findViewById(R.id.tv_stokk)
        val tvTanggal: TextView = view.findViewById(R.id.tv_tanggall)
        val tvHarga: TextView = view.findViewById(R.id.tv_hargaa)
        val imgLine: ImageView = view.findViewById(R.id.img_line)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_riwayat, parent, false)
        return ViewHolder(v)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = riwayatList[position]

        holder.tvBarang.text = item.nama
        holder.tvStok.text = "${item.tipe.uppercase()}: ${item.jumlah}"
        holder.tvTanggal.text = convertLongToDate(item.tanggal)

        val totalUang = when (item.tipe.lowercase()) {
            "masuk"  -> item.harga * item.jumlah
            "keluar" -> item.modal * item.jumlah
            else     -> 0.0
        }
        holder.tvHarga.text = "Rp %.0f".format(totalUang)

        if (!item.gambarPath.isNullOrEmpty()) {
            try {
                val uri = Uri.parse(item.gambarPath)

                val inputStream = holder.ivGambar.context.contentResolver.openInputStream(uri)
                inputStream?.use { stream ->
                    val bitmap = BitmapFactory.decodeStream(stream)
                    holder.ivGambar.setImageBitmap(bitmap)
                }
            } catch (e: SecurityException) {
                e.printStackTrace()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        val lineDrawable = if (item.tipe.lowercase() == "keluar") {
            R.drawable.redline
        } else {
            R.drawable.greenline
        }
        holder.imgLine.setImageResource(lineDrawable)
    }


    override fun getItemCount(): Int = riwayatList.size

    fun updateData(newData: List<RiwayatWithBarang>) {
        riwayatList = newData
        notifyDataSetChanged()
    }

    private fun convertLongToDate(time: Long): String {
        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        return sdf.format(Date(time))
    }
}

