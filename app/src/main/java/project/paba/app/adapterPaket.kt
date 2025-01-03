package project.paba.app

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso

class adapterPaket(
    private val paketList: List<paketRestoran>,
    private val restoranId: String?,
    private val onReserveClick: (paketRestoran) -> Unit
) : RecyclerView.Adapter<adapterPaket.PaketViewHolder>() {

    private val db = FirebaseFirestore.getInstance()

    inner class PaketViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val namaPaket: TextView = view.findViewById(R.id.namaPaket)
        val deskripsi: TextView = view.findViewById(R.id.deskripsi)
        val kapasitas: TextView = view.findViewById(R.id.kapasitas)
        val harga: TextView = view.findViewById(R.id.harga)
        val uangDp: TextView = view.findViewById(R.id.uangDp)
        val pesanButton: Button = view.findViewById(R.id.btnPesan)
        val gambarPaket: ImageView = view.findViewById(R.id.gambarPaket)

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
        holder.kapasitas.text = paket.kapasitas
        holder.harga.text = paket.harga
        holder.uangDp.text = paket.uangDp


        db.collection("restoran").document(restoranId.toString()).collection("paket").document(paket.idPaket)
            .get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    val fotoo = document.getString("foto") ?:""
                    if (fotoo.isNotEmpty()) {
                        Picasso.get()
                            .load(fotoo)
                            .placeholder(R.drawable.resto)
                            .error(R.drawable.resto)
                            .into(holder.gambarPaket)
                    } else {
                        holder.gambarPaket.setImageResource(R.drawable.resto)
                    }


                } else {
                    Log.e("Addpaketadapter", "Document tidak ditemukan untuk ID: ${paket.idRestoran}")
                }
            }
            .addOnFailureListener { exception ->
                Log.e("addpaketadapter", "Error mengambil data restoran: ${exception.message}", exception)
            }


    if (paket.namaPaket.isNullOrEmpty() || paket.harga.isNullOrEmpty()) {

            holder.pesanButton.isEnabled = false
            holder.pesanButton.alpha = 0.5f
        } else {
            holder.pesanButton.isEnabled = true
            holder.pesanButton.alpha = 1f
        }


        holder.pesanButton.setOnClickListener {

            val activity = holder.itemView.context as FragmentActivity

            val addBookingFragment = AddBookingFragment().apply {
                arguments = Bundle().apply {
                    putString("idRestoran", restoranId)
                    putString("paketName", paket.namaPaket)
                    putString("idPaket", paket.idPaket)

                }
            }

            activity.supportFragmentManager.beginTransaction()
                .replace(R.id.frameContainer, addBookingFragment)
                .addToBackStack(null)
                .commit()
        }
    }


        override fun getItemCount(): Int {
        return paketList.size
    }
}
