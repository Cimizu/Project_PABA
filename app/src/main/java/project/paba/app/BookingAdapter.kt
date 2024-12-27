package project.paba.app

import BookingInfo
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class BookingAdapter(private val bookingList: ArrayList<BookingInfo>) : RecyclerView
    .Adapter<BookingAdapter.BookingViewHolder>() {

    class BookingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvResto: TextView = itemView.findViewById(R.id.tv_resto)
        val tvName: TextView = itemView.findViewById(R.id.tv_nama)
        val tvDate: TextView = itemView.findViewById(R.id.tv_tanggal)
        val tvTime: TextView = itemView.findViewById(R.id.tv_jam)
        val tvPhone: TextView = itemView.findViewById(R.id.tv_notelp)
        val tvNotes: TextView = itemView.findViewById(R.id.tv_cttn)

        fun bind(booking: BookingInfo) {
            tvResto.text = booking.resto
            tvName.text = booking.name
            tvDate.text = booking.date
            tvTime.text = booking.time
            tvPhone.text = booking.phone
            tvNotes.text = booking.notes
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookingViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.booking_recycler, parent, false)
        return BookingViewHolder(view)
    }

    override fun getItemCount(): Int {
        return bookingList.size
    }

    override fun onBindViewHolder(holder: BookingViewHolder, position: Int) {
        val booking = bookingList[position]
        holder.bind(booking)
    }
}