import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import project.paba.app.R
import project.paba.app.paketRestoran

class adapterPaket(
    private val paketList: List<paketRestoran>,
    private val onReserveClick: (paketRestoran) -> Unit // Menggunakan lambda untuk event handler
) : RecyclerView.Adapter<adapterPaket.PaketViewHolder>() {

    inner class PaketViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val namaPaket: TextView = view.findViewById(R.id.namaPaket)
        val deskripsi: TextView = view.findViewById(R.id.deskripsi)
        val kapasitas: TextView = view.findViewById(R.id.kapasitas)
        val harga: TextView = view.findViewById(R.id.harga)
        val uangDp: TextView = view.findViewById(R.id.uangDp)
        val pesanButton: Button = view.findViewById(R.id.btnPesan)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PaketViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.paket_recycler, parent, false)
        return PaketViewHolder(view)
    }

    override fun onBindViewHolder(holder: PaketViewHolder, position: Int) {
        val paket = paketList[position]

        // Set text data
        holder.namaPaket.text = paket.namaPaket
        holder.deskripsi.text = paket.deskripsi
        holder.kapasitas.text = paket.kapasitas
        holder.harga.text = paket.harga
        holder.uangDp.text = paket.uangDp

        // Handle button click event
        holder.pesanButton.setOnClickListener {
            onReserveClick(paket) // Panggil lambda event handler
        }
    }

    override fun getItemCount(): Int {
        return paketList.size
    }
}
