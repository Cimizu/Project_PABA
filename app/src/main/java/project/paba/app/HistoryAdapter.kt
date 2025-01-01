
package project.paba.app

import BookingInfo
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import project.paba.app.BookingAdapter.BookingViewHolder

class HistoryAdapter(private val BookingInfoo: ArrayList<BookingInfo>) : RecyclerView
.Adapter<HistoryAdapter.HistoryHolder> () {

    inner class HistoryHolder(itemView: View) : RecyclerView.ViewHolder(itemView)  {
        val tvResto: TextView = itemView.findViewById(R.id.tv_resto)
        val tvGambarResto: TextView = itemView.findViewById(R.id.iv_GambarResto)
        val tvAddress: TextView = itemView.findViewById(R.id.tv_address)
        val tvDate: TextView = itemView.findViewById(R.id.tv_tanggal)
        val tvTime: TextView = itemView.findViewById(R.id.tv_jam)
        val tvStatus: TextView = itemView.findViewById(R.id.tv_status)

        fun bind(booking: BookingInfo, restoran: dataRestoran) {
            tvResto.text = restoran.namaResto
            tvGambarResto.text = restoran.foto
//            tvAddress.text = restoran.alamat
            tvDate.text = booking.date
            tvTime.text = booking.time
//            tvStatus.text = booking.status
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryHolder {
        val view: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.history_recycler, parent, false)
        return HistoryHolder(view)
    }

    override fun getItemCount(): Int {
        return BookingInfoo.size
    }

    override fun onBindViewHolder(holder: HistoryHolder, position: Int) {
        val booking = BookingInfoo[position]
//        val restoran = dataRestoran[position]
//        holder.bind(booking, restoran)
    }

}