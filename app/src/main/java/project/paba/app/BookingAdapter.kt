package project.paba.app

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class BookingAdapter(private val bookingList: List<BookingInfo>) : RecyclerView.Adapter<BookingAdapter.BookingViewHolder>() {

    class BookingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameTextView: TextView = itemView.findViewById(R.id.tv_resto)
        val dateTextView: TextView = itemView.findViewById(R.id.tv_tanggal)
        val timeTextView: TextView = itemView.findViewById(R.id.tv_jam)
        val phoneTextView: TextView = itemView.findViewById(R.id.tv_notelp)
        val notesTextView: TextView = itemView.findViewById(R.id.tv_cttn)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookingViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.booking_recycler, parent, false)
        return BookingViewHolder(view)
    }

    override fun onBindViewHolder(holder: BookingViewHolder, position: Int) {
        val bookingInfo = bookingList[position]
        holder.nameTextView.text = bookingInfo.name
        holder.dateTextView.text = bookingInfo.date
        holder.timeTextView.text = bookingInfo.time
        holder.phoneTextView.text = bookingInfo.phone
        holder.notesTextView.text = bookingInfo.notes
    }

    override fun getItemCount() = bookingList.size
}