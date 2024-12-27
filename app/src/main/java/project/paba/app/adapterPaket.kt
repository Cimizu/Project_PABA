package project.paba.app

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore

// Adapter untuk RecyclerView
class adapterPaket(
    private val paketList: List<paketRestoran>
) : RecyclerView.Adapter<adapterPaket.PaketViewHolder>() {

    private lateinit var db: FirebaseFirestore

    // ViewHolder untuk item paket
    inner class PaketViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val namaPaket: TextView = itemView.findViewById(R.id.namaPaket)
        val deskripsi: TextView = itemView.findViewById(R.id.dekskripsi)
        val harga: TextView = itemView.findViewById(R.id.harga)
        val kapasitas: TextView = itemView.findViewById(R.id.kapasitas)
        val uangDp: TextView = itemView.findViewById(R.id.uangDp)

        init {
            itemView.setOnClickListener {
                val paket = paketList[adapterPosition]
                Toast.makeText(itemView.context, "Paket ${paket.namaPaket} dipilih", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PaketViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.paket_recycler, parent, false)
        return PaketViewHolder(view)
    }

    override fun onBindViewHolder(holder: PaketViewHolder, position: Int) {
        val paket = paketList[position]
        holder.namaPaket.text = paket.namaPaket
        holder.deskripsi.text = paket.deskripsi
        holder.harga.text = paket.harga
        holder.kapasitas.text = paket.kapasitas.toString()
        holder.uangDp.text = paket.uangDp
    }

    override fun getItemCount(): Int = paketList.size
}
