package project.paba.app

import BookingInfo
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore

class BookingAdapter(private val bookingList: MutableList<BookingInfo>) : RecyclerView.Adapter<BookingAdapter.BookingViewHolder>() {

    class BookingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ivGambarResto: ImageView = itemView.findViewById(R.id.iv_gambarResto)
        val tvResto: TextView = itemView.findViewById(R.id.tv_resto)
        val tvNama: TextView = itemView.findViewById(R.id.tv_nama)
        val tvAlamat: TextView = itemView.findViewById(R.id.tv_alamat)
        val tvTanggal: TextView = itemView.findViewById(R.id.tv_tanggal)
        val tvJam: TextView = itemView.findViewById(R.id.tv_jam)
        val tvCttn: TextView = itemView.findViewById(R.id.tv_cttn)
        val ibTrash: ImageButton = itemView.findViewById(R.id.ib_trash)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookingViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.booking_recycler, parent, false)
        return BookingViewHolder(view)
    }

    override fun onBindViewHolder(holder: BookingViewHolder, position: Int) {
        val booking = bookingList[position]
        holder.tvResto.text = booking.resto
        holder.tvNama.text = booking.name
        holder.tvAlamat.text = booking.address
        holder.tvTanggal.text = booking.date
        holder.tvJam.text = booking.time
        holder.tvCttn.text = booking.notes

        holder.ibTrash.setOnClickListener {
            deleteBooking(position)
        }
    }

    override fun getItemCount(): Int {
        return bookingList.size
    }

    private fun deleteBooking(position: Int) {
        val booking = bookingList[position]
        val db = FirebaseFirestore.getInstance()
        db.collection("bookings").whereEqualTo("name", booking.name)
            .whereEqualTo("date", booking.date)
            .whereEqualTo("time", booking.time)
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    db.collection("bookings").document(document.id).delete()
                        .addOnSuccessListener {
                            bookingList.removeAt(position)
                            notifyItemRemoved(position)
                        }
                        .addOnFailureListener { e ->
                            Log.e("Firebase", "Error deleting document", e)
                        }
                }
            }
            .addOnFailureListener { e ->
                Log.e("Firebase", "Error finding document", e)
            }
    }
}