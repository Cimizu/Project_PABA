package project.paba.app

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso

class addPaketAdapter (
    private val paketList: List<paketRestoran>,
    private val restoranId: String?,
    private val onReserveClick: (paketRestoran) -> Unit
) : RecyclerView.Adapter<addPaketAdapter.PaketViewHolder>() {

    inner class PaketViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val namaPaket: TextView = view.findViewById(R.id.namaPaket)
        val deskripsi: TextView = view.findViewById(R.id.deskripsi)
        val kapasitas: TextView = view.findViewById(R.id.kapasitas)
        val harga: TextView = view.findViewById(R.id.harga)
        val uangDp: TextView = view.findViewById(R.id.uangDp)
        val gambar: ImageView = view.findViewById(R.id.gambarPaket)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PaketViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.addpaket_recycler, parent, false)
        return PaketViewHolder(view)
    }

    override fun onBindViewHolder(holder: PaketViewHolder, position: Int) {
        val paket = paketList[position]

        holder.namaPaket.text = paket.namaPaket
        holder.deskripsi.text = paket.deskripsi
        holder.kapasitas.text = paket.kapasitas
        holder.harga.text = paket.harga
        holder.uangDp.text = paket.uangDp
        if (paket.foto.isNotEmpty()) {
            Picasso.get()
                .load(paket.foto)
                .placeholder(R.drawable.resto)
                .error(R.drawable.resto)
                .into(holder.gambar) // Ngest image dr yang link diksh
        } else {
            holder.gambar.setImageResource(R.drawable.resto)
        }



    }

    override fun getItemCount(): Int {
        return paketList.size
    }
}
