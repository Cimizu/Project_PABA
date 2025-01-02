package project.paba.app

import BookingInfo
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore

class HistoryAdapter(private val bookingList: ArrayList<BookingInfo>) : RecyclerView.Adapter<HistoryAdapter.HistoryHolder>() {

    inner class HistoryHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvResto: TextView = itemView.findViewById(R.id.tv_resto)
        val tvGambarResto: ImageView = itemView.findViewById(R.id.iv_GambarResto)
        val tvAddress: TextView = itemView.findViewById(R.id.tv_address)
        val tvDate: TextView = itemView.findViewById(R.id.tv_tanggal)
        val tvTime: TextView = itemView.findViewById(R.id.tv_jam)
        val tvStatus: TextView = itemView.findViewById(R.id.tvStatus)
        val ivDelete: ImageView = itemView.findViewById(R.id.btnDelete)
        val btn_Detail : Button = itemView.findViewById(R.id.btn_detail)

    }
    fun updateData(newList: List<BookingInfo>) {
        bookingList.clear()
        bookingList.addAll(newList)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryHolder {
        val view: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.history_recycler, parent, false)
        return HistoryHolder(view)
    }

    override fun onBindViewHolder(holder: HistoryHolder, position: Int) {
        val booking = bookingList[position]
        holder.tvResto.text = booking.resto
//        holder.tvGambarResto.text = booking.foto // Assuming foto is a string, you may want to load an image here
        holder.tvAddress.text = booking.address
        holder.tvDate.text = booking.date
        holder.tvTime.text = booking.time
        holder.tvStatus.text = booking.statusString

        holder.ivDelete.setOnClickListener {
            deleteBooking(position)
        }
        holder.btn_Detail.setOnClickListener {
            val context = holder.itemView.context
            if (context is androidx.fragment.app.FragmentActivity) {
                val detailFragment = konfirmasi_checkin().apply {
                    arguments =Bundle().apply {
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
    }
    private fun deleteBooking(position: Int) {
        val booking = bookingList[position]
        val db = FirebaseFirestore.getInstance()
        db.collection("bookings").document(booking.id.toString())
                db.collection("bookings").whereEqualTo("name", booking.name)
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

    override fun getItemCount(): Int {
        return bookingList.size
    }
}
