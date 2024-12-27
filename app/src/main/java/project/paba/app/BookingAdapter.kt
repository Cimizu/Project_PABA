package project.paba.app

import BookingInfo
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
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
        val ibEdit : ImageButton = itemView.findViewById(R.id.ib_edit)
        val btnCekStatus : Button = itemView.findViewById(R.id.btn_cekStatus)
        val btnBatal : Button = itemView.findViewById(R.id.btn_batal)
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
        holder.ibEdit.setOnClickListener {
            val context = holder.itemView.context
            val intent = Intent(context, addBooking::class.java)
            intent.putExtra("bookingId", booking.id)
            if (booking.id != null) {
                Log.d("BookingAdapter", "Booking ID: ${booking.id}")
                holder.tvNama.text = booking.name
                holder.tvTanggal.text = booking.date
                holder.tvJam.text = booking.time
                holder.tvCttn.text = booking.notes
            }
            intent.putExtra("restoName", booking.resto)
            intent.putExtra("name", booking.name)
            intent.putExtra("address", booking.address)
            intent.putExtra("date", booking.date)
            intent.putExtra("time", booking.time)
            intent.putExtra("phone", booking.phone)
            intent.putExtra("notes", booking.notes)

            context.startActivity(intent)
        }

        // jadi kalau dia batalin maka status akan berubah menjadi batal


        holder.btnCekStatus.setOnClickListener {
//            disable button
            holder.btnCekStatus.isEnabled = false
            holder.btnCekStatus.text = "Pembayaran Berhasil"
        }

        holder.btnBatal.setOnClickListener {
//            disable button
            holder.btnBatal.isEnabled = false

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