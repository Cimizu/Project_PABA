package project.paba.app

import BookingInfo
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.UUID

class BookingAdapter(private val bookingList: MutableList<BookingInfo>) : RecyclerView.Adapter<BookingAdapter.BookingViewHolder>() {

    class BookingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ivGambarResto: ImageView = itemView.findViewById(R.id.iv_gambarResto)
        val tvResto: TextView = itemView.findViewById(R.id.tv_resto)
        val tvPaket: TextView = itemView.findViewById(R.id.tv_paket)
        val tvNama: TextView = itemView.findViewById(R.id.tv_nama)
        val tvAlamat: TextView = itemView.findViewById(R.id.tv_alamat)
        val tvTanggal: TextView = itemView.findViewById(R.id.tv_tanggal)
        val tvJam: TextView = itemView.findViewById(R.id.tv_jam)
        val tvCttn: TextView = itemView.findViewById(R.id.tv_cttn)

        val ibTrash: ImageButton = itemView.findViewById(R.id.ib_trash)
        val ibEdit: ImageButton = itemView.findViewById(R.id.ib_edit)
        val btnCekStatus: Button = itemView.findViewById(R.id.btn_cekStatus)
        val btnBatal: Button = itemView.findViewById(R.id.btn_batal)

        val btnCheckin : Button = itemView.findViewById(R.id.btn_checkin)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookingViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.booking_recycler, parent, false)
        return BookingViewHolder(view)
    }

    override fun onBindViewHolder(holder: BookingViewHolder, position: Int) {
        val booking = bookingList[position]
        holder.tvResto.text = booking.resto
        holder.tvPaket.text = booking.paket
        holder.tvNama.text = booking.name
        holder.tvAlamat.text = booking.address
        holder.tvTanggal.text = booking.date
        holder.tvJam.text = booking.time
        holder.tvCttn.text = booking.notes

        // Check status_bayar and update button state
        val db = FirebaseFirestore.getInstance()
        db.collection("bookings").document(booking.id.toString())
            .get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    val statusBayar = document.getBoolean("status_bayar") ?: false
                    if (statusBayar) {
                        holder.btnCekStatus.isEnabled = false
                        holder.btnCekStatus.text = "Pembayaran Berhasil"
                        db.collection("bookings").document(booking.id.toString())
                            .update("status_bayar", true)
                            .addOnSuccessListener {
                                Log.d("BookingAdapter", "Status bayar updated successfully")
                            }
                            .addOnFailureListener { e ->
                                Log.e("BookingAdapter", "Error updating status bayar", e)
                            }

                    } else {
                        holder.btnCekStatus.isEnabled = true
                        holder.btnCekStatus.text = "Cek Status"
                    }
                }
            }
            .addOnFailureListener { e ->
                Log.e("BookingAdapter", "Error getting document", e)
            }
//        cek status aktif
        val currentDate = Calendar.getInstance().time
        val bookingDate = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).parse("${booking.date} ${booking.time}")

        if (bookingDate != null && currentDate.before(bookingDate)) {
            holder.btnBatal.isEnabled = true
            holder.btnBatal.text = "Batal"
            db.collection("bookings").document(booking.id.toString())
                .update("status_aktif", true)
                .addOnSuccessListener {
                    Log.d("BookingAdapter", "Status aktif updated successfully")
                }
                .addOnFailureListener { e ->
                    Log.e("BookingAdapter", "Error updating status aktif", e)
                }
        } else {
            holder.btnCekStatus.isVisible = false
            holder.btnBatal.isEnabled = false
            holder.btnBatal.text = "Dibatalkan"
            db.collection("bookings").document(booking.id.toString())
                .update("status_aktif", false)
                .addOnSuccessListener {
                    Log.d("BookingAdapter", "Status aktif updated successfully")
                }
                .addOnFailureListener { e ->
                    Log.e("BookingAdapter", "Error updating status aktif", e)
                }
        }

        holder.ibTrash.setOnClickListener {
            deleteBooking(position)
        }
        holder.ibEdit.setOnClickListener {
            val context = holder.itemView.context
            val fragment = AddBookingFragment().apply {
                arguments = Bundle().apply {
                    putInt("bookingId", booking.id)
                    putString("restoName", booking.resto)
                    putString("paketName", booking.paket)
                    putString("name", booking.name)
                    putString("address", booking.address)
                    putString("date", booking.date)
                    putString("time", booking.time)
                    putString("phone", booking.phone)
                    putString("notes", booking.notes)
                }
            }
            (context as AppCompatActivity).supportFragmentManager.beginTransaction()
                .replace(R.id.frameContainer, fragment, AddBookingFragment::class.java.simpleName)
                .addToBackStack(null)
                .commit()
        }

        holder.btnCekStatus.setOnClickListener {
            // Disable button
            holder.btnCekStatus.isEnabled = false
            holder.btnCekStatus.text = "Pembayaran Berhasil"

            // Update status_bayar to true in Firestore
            val db = FirebaseFirestore.getInstance()
            db.collection("bookings").document(booking.id.toString())
                .update("status_bayar", true)
                .addOnSuccessListener {
                    Log.d("BookingAdapter", "Status bayar updated successfully")
                }
                .addOnFailureListener { e ->
                    Log.e("BookingAdapter", "Error updating status bayar", e)
                }
        }

        fun generateUniqueCode(): String {
            return "BOOK-" + UUID.randomUUID().toString() // Generates a UUID-based unique code
        }

        //kode unik
        holder.btnCheckin.setOnClickListener {
            if(booking.status_bayar == true && booking.status_aktif == true){
                val uniqueCode = generateUniqueCode() // Generate unique code
                Log.d("BookingAdapter", "Kode Unik = $uniqueCode")

                // Update db
                db.collection("bookings").document(booking.id.toString())
                    .update("kodeunik", uniqueCode)
                    .addOnSuccessListener {
                        Log.d("BookingAdapter", "Kodeunik added to Firestore")
                    }
                    .addOnFailureListener { e ->
                        Log.e("BookingAdapter", "Error adding kodeunik", e)
                    }

            }
        }

        holder.btnBatal.setOnClickListener {
            // Disable button
            holder.btnBatal.isEnabled = false
            holder.btnBatal.text = "Dibatalkan"
            holder.btnCekStatus.isEnabled = false
            db.collection("bookings").document(booking.id.toString())
                .update("status_aktif", false)
                .addOnSuccessListener {
                    Log.d("BookingAdapter", "Status aktif updated successfully")
                }
                .addOnFailureListener { e ->
                    Log.e("BookingAdapter", "Error updating status aktif", e)
                }
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