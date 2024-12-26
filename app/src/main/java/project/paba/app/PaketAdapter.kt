package project.paba.app

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class PaketAdapter(private val paketList: List<paketRestoran>) : RecyclerView.Adapter<PaketAdapter.PaketViewHolder>() {

    class PaketViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvPaketName: TextView = itemView.findViewById(R.id.tv_packetName)
        val tvPacketDesc: TextView = itemView.findViewById(R.id.tv_packetDesc)
        val tvPaketPrice: TextView = itemView.findViewById(R.id.tv_packetPrice)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PaketViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.paket_recycler, parent, false)
        return PaketViewHolder(view)
    }

    override fun onBindViewHolder(holder: PaketViewHolder, position: Int) {
        val paket = paketList[position]
        holder.tvPaketName.text = paket.namapaket
        holder.tvPacketDesc.text = paket.deskripsi
        holder.tvPaketPrice.text = paket.harga.toString()
    }

    override fun getItemCount(): Int {
        return paketList.size
    }
}