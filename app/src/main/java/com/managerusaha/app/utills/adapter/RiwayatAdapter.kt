import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.managerusaha.app.R
import com.managerusaha.app.room.entity.Riwayat
import com.managerusaha.app.utills.model.RiwayatWithNama
import java.text.SimpleDateFormat
import java.util.*

class RiwayatAdapter(private var data: List<RiwayatWithNama>) :
    RecyclerView.Adapter<RiwayatAdapter.ViewHolder>() {

    private val formatter = SimpleDateFormat("dd MMM yyyy HH:mm", Locale.getDefault())

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvBarang: TextView = view.findViewById(R.id.tv_barangg)
        val tvJumlah: TextView = view.findViewById(R.id.tv_stokk)
        val tvTanggal: TextView = view.findViewById(R.id.tv_tanggall)
        val imgLine: ImageView = view.findViewById(R.id.img_line)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_riwayat, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = data[position]

        holder.tvBarang.text = item.namaBarang
        holder.tvJumlah.text = "Jumlah: ${item.jumlah}"
        holder.tvTanggal.text = formatter.format(Date(item.tanggal))

        if (item.tipe == "KELUAR") {
            holder.imgLine.setImageResource(R.drawable.redline)
        } else {
            holder.imgLine.setImageResource(R.drawable.greenline)
        }
    }

    override fun getItemCount(): Int = data.size

    fun updateData(newData: List<RiwayatWithNama>) {
        data = newData
        notifyDataSetChanged()
    }
}
