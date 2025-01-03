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
import java.text.SimpleDateFormat
import java.util.Locale

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

        // Mengambil gambar dari Firestore
        db.collection("restoran").document(restoranId.toString()).collection("paket").document(paket.idPaket)
            .get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    val fotoo = document.getString("foto") ?: ""
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


        fun convertDateToMillis(dateString: String): Long {
            val format = SimpleDateFormat("d/M/yyyy", Locale.getDefault())
            return try {
                val date = format.parse(dateString)
                date?.time ?: 0L
            } catch (e: Exception) {
                0L
            }
        }

// Ambil tanggal saat ini
        val currentDate = System.currentTimeMillis()
        val threeDaysLater = currentDate + 1 * 24 * 60 * 60 * 1000  // 1 hari dalam milidetik

        val keywords = listOf("ultah", "ulang tahun", "venue", "pernikahan")

        db.collection("bookings")
            .whereEqualTo("idPaket", paket.idPaket)
            .whereEqualTo("status_bayar", true)  // Filter status bayar
            .whereEqualTo("status_aktif", true)  // Filter status aktif
            .get()
            .addOnSuccessListener { querySnapshot ->
                var jumlahBooking = 0

                for (document in querySnapshot) {

                    val bookingDateString = document.getString("date") ?: ""
                    val bookingDateMillis = convertDateToMillis(bookingDateString)


                    if (bookingDateMillis in currentDate until threeDaysLater) {


                        val namaPaketBooking = document.getString("paket")?.lowercase() ?: ""
                        if (keywords.any { keyword -> namaPaketBooking.contains(keyword.lowercase()) }) {
                            jumlahBooking++
                        }
                    }
                }

                // Jika ada 3 pemesanan dan ada kecocokan kata kunci
                if (jumlahBooking >= 1) {
                    holder.pesanButton.isEnabled = false
                    holder.pesanButton.alpha = 0.5f
                    holder.pesanButton.visibility = View.GONE
                } else {
                    holder.pesanButton.isEnabled = true
                    holder.pesanButton.alpha = 1f
                    holder.pesanButton.visibility = View.VISIBLE
                }
            }
            .addOnFailureListener { exception ->
                Log.e("addpaketadapter", "Error mengambil data bookings: ${exception.message}", exception)
            }



        // Mengatur click listener tombol pesan
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
