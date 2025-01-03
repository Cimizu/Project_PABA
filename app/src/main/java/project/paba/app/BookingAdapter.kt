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
import com.squareup.picasso.Picasso
import java.text.SimpleDateFormat
import java.time.DateTimeException
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Locale
import java.util.UUID

class BookingAdapter(private val bookingList: MutableList<BookingInfo>) : RecyclerView.Adapter<BookingAdapter.BookingViewHolder>() {

    private val db = FirebaseFirestore.getInstance()

    class BookingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ivGambarResto: ImageView = itemView.findViewById(R.id.iv_gambarResto)
        val tvResto: TextView = itemView.findViewById(R.id.tv_resto)
        val tvPaket: TextView = itemView.findViewById(R.id.tv_paket)
        val tvNama: TextView = itemView.findViewById(R.id.tv_nama)
        val tvAlamat: TextView = itemView.findViewById(R.id.tv_alamat)
        val tvTanggal: TextView = itemView.findViewById(R.id.tv_tanggal)
        val tvJam: TextView = itemView.findViewById(R.id.tv_jam)
        val tvCttn: TextView = itemView.findViewById(R.id.tv_cttn)

        val ibTrash: ImageView = itemView.findViewById(R.id.ib_trash)
        val ibEdit: ImageButton = itemView.findViewById(R.id.ib_edit)
//        val btnCekStatus: Button = itemView.findViewById(R.id.btn_cekStatus)
        val btnBatal: Button = itemView.findViewById(R.id.btn_batal)
        val tvsisa : TextView = itemView.findViewById(R.id.tv_sisaBayar)

        val btnCheckin : Button = itemView.findViewById(R.id.btn_checkin)
        val btnPembayaran : Button = itemView.findViewById(R.id.btn_pembayaran)
        val btn_detail : Button = itemView.findViewById(R.id.btn_detail)
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
        holder.tvsisa.text = String.format("Rp %,03d", booking.hargaSisa)

        db.collection("restoran").document(booking.idResto)
            .get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    val imageUrl = document.getString("foto")
                    if (!imageUrl.isNullOrEmpty()) {
                        Picasso.get()
                            .load(imageUrl)
                            .placeholder(R.drawable.resto)
                            .error(R.drawable.resto)
                            .into(holder.ivGambarResto)
                    } else {
                        holder.ivGambarResto.setImageResource(R.drawable.resto)
                    }
                } else {
                    holder.ivGambarResto.setImageResource(R.drawable.resto)
                }
            }
            .addOnFailureListener { e ->
                Log.e("BookingAdapter", "Error loading restaurant image: ", e)
                holder.ivGambarResto.setImageResource(R.drawable.resto)
            }

        holder.btnPembayaran.setOnClickListener {
            val context = holder.itemView.context
            if (context is androidx.fragment.app.FragmentActivity) {
                val detailFragment = payment().apply {
                    arguments = Bundle().apply {
                        putParcelable(
                            "kirimData",
                            booking
                        ) // Kirim data restoran sebagai Parcelable
                    }
                }
                // Ganti fragment
                context.supportFragmentManager.beginTransaction()
                    .replace(
                        R.id.frameContainer,
                        detailFragment
                    )
                    .addToBackStack(null) // Buat back navigation
                    .commit()
            }
        }

        // Check status_bayar and update button state
        val db = FirebaseFirestore.getInstance()
        db.collection("bookings").document(booking.id.toString())
            .get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    val statusBayar = document.getBoolean("status_bayar") ?: false
                    val statusDP = document.getBoolean("statusDP") ?: false
                    val statusSisa = document.getBoolean("statusSisa") ?: false
                    val statusAktif = document.getBoolean("status_aktif") ?: false

                    if (statusBayar && statusDP) {
                        holder.btnCheckin.visibility = View.VISIBLE
                    } else {
                        holder.btnCheckin.visibility = View.GONE
                    }

                    if (statusBayar) {
                        holder.ibEdit.visibility = View.GONE
                    }
                    if (statusBayar && statusDP && statusSisa && statusAktif) {
                        holder.btnPembayaran.isEnabled = false
                        holder.ibEdit.visibility = View.GONE
                        holder.btnPembayaran.text = "Pembayaran Berhasil"
                    } else {
                        // Jika salah satu atau lebih kondisi tidak terpenuhi
                        holder.btnPembayaran.isEnabled = true
                        holder.btnPembayaran.text = "Lakukan Pembayaran"
                    }
                } else {
                    Log.e("BookingAdapter", "Document tidak ditemukan atau null")
                }
            }
            .addOnFailureListener { e ->
                Log.e("BookingAdapter", "Error getting document", e)
            }

//        cek status aktif
        val currentDate = Calendar.getInstance().time
        val bookingDate = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).parse("${booking.date} ${booking.time}")
        val bookingDateWithTolerance = bookingDate?.let {
            Calendar.getInstance().apply {
                time = it
                add(Calendar.MINUTE, 15) // toleransi 15 menit
            }.time
        }

        if (bookingDateWithTolerance != null && currentDate.before(bookingDateWithTolerance)) {
            // Booking masih dalam waktu toleransi, status tetap aktif
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
            // Booking sudah melewati waktu toleransi, ubah status menjadi tidak aktif
            holder.btnPembayaran.isVisible = false
            holder.btnBatal.isEnabled = false
            holder.btnBatal.text = "Dibatalkan"
            db.collection("bookings").document(booking.id.toString())
                .update("status_aktif", false, "statusString", "EXPIRED")
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
                    putString("idRestoo", booking.idResto)
                    putString("idPakett", booking.idPaket)
                    putString("restoName", booking.resto)
                    putString("paketName", booking.paket)
                    putString("name", booking.name)
                    putString("address", booking.address)
                    putString("date", booking.date)
                    putString("time", booking.time)
                    putString("phone", booking.phone)
                    putString("notes", booking.notes)
                    putInt("addEdit", 1)
                }
            }
            (context as AppCompatActivity).supportFragmentManager.beginTransaction()
                .replace(R.id.frameContainer, fragment, AddBookingFragment::class.java.simpleName)
                .addToBackStack(null)
                .commit()
        }

//        holder.btnCekStatus.setOnClickListener {
//            updatePaymentStatus(booking)
//        }
        fun generateUniqueCode(): String {
            // You can use UUID or combine with the timestamp to make it even more unique
            val uniqueCode = "BOOK-" + UUID.randomUUID().toString() // Generates a UUID-based unique code
            return uniqueCode
        }
        holder.btn_detail.setOnClickListener {
            val context = holder.itemView.context
            if (context is androidx.fragment.app.FragmentActivity) {
                val detailFragment = konfirmasi_checkin().apply {
                    arguments = Bundle().apply {
                        putParcelable(
                            "kirimData",
                            booking
                        ) // Kirim data restoran sebagai Parcelable
                    }
                }
                // Ganti fragment
                context.supportFragmentManager.beginTransaction()
                    .replace(
                        R.id.frameContainer,
                        detailFragment
                    )
                    .addToBackStack(null) // Buat back navigation
                    .commit()
            }

        }

        //kode unik
        holder.btnCheckin.setOnClickListener {
            if (booking.status_bayar == true && booking.status_aktif == true) {
                val uniqueCode = generateUniqueCode() // Generate unique code
                val timestamp = System.currentTimeMillis() // Get current timestamp

                // Update booking with unique code and timestamp
                db.collection("bookings").document(booking.id.toString())
                    .update(
                        "uniqueCode", uniqueCode,
                        "timestamp" ,timestamp,
                        "status_aktif", false,
                        "statusString", "USED"
                    )
                    .addOnSuccessListener {
                        Log.d("BookingAdapter", "Unique code and timestamp updated successfully")

                        // Show QR code dialog
                        val qrCodeDialog = QRCodeDialogFragment.newInstance(uniqueCode)
                        qrCodeDialog.show((holder.itemView.context as AppCompatActivity).supportFragmentManager, "QRCodeDialog")
                    }
                    .addOnFailureListener { e ->
                        Log.e("BookingAdapter", "Error updating unique code and timestamp", e)
                    }
            }
        }

        // Batal button
        holder.btnBatal.setOnClickListener {
            // Disable button
            holder.btnBatal.isEnabled = false
            holder.btnBatal.text = "Dibatalkan"
            holder.btnPembayaran.isEnabled = false
            db.collection("bookings").document(booking.id.toString())
                .update("status_aktif", false,"statusString", "BATAL")
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
        db.collection("bookings").document(booking.id.toString())
            .update("status_aktif", false, "statusString", "BATAL")
            .addOnSuccessListener {
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

}