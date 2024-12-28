package project.paba.app

import BookingInfo
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import project.paba.app.BookingAdapter.BookingViewHolder

class HistoryAdapter(private val BookingInfoo: ArrayList<BookingInfo>,
                     private val dataRestoran: ArrayList<dataRestoran>
                     ) : RecyclerView
.Adapter<HistoryAdapter.HistoryHolder> () {

    private lateinit var onItemClickCallback: OnItemClickCallback

//    fun delData(position: Int) {
//        BookingInfoo.removeAt(position)
//        notifyItemRemoved(position)
//    }

    interface OnItemClickCallback {
        fun onItemClicked(data:BookingInfo, data2:dataRestoran)
        fun delData(pos: Int)
    }

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback){
        this.onItemClickCallback = onItemClickCallback
    }

    inner class HistoryHolder(itemView: View) : RecyclerView.ViewHolder(itemView)  {
        val tvResto: TextView = itemView.findViewById(R.id.tv_resto)
        val tvGambarResto: ImageView = itemView.findViewById(R.id.iv_GambarResto)
        val tvAddress: TextView = itemView.findViewById(R.id.tv_address)
        val tvDate: TextView = itemView.findViewById(R.id.tv_tanggal)
        val tvTime: TextView = itemView.findViewById(R.id.tv_jam)
        val tvStatus: TextView = itemView.findViewById(R.id.tv_status)
        var _btnHapus = itemView.findViewById<ImageView>(R.id.btnHapus)

        fun bind(booking: BookingInfo, restoran: dataRestoran) {
            tvResto.text = restoran.namaResto
//            tvGambarResto.sr = restoran.foto
            tvAddress.text = restoran.lokasi
            tvDate.text = booking.date
            tvTime.text = booking.time
            tvStatus.text = booking.status

            Log.d("TEST", restoran.foto)
            Picasso.get()
                .load(restoran.foto)
                .into(tvGambarResto)

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryHolder {
        val view: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.history_recycler, parent, false)
        return HistoryHolder(view)
    }

    override fun getItemCount(): Int {
        return maxOf(BookingInfoo.size, dataRestoran.size)
    }

    override fun onBindViewHolder(holder: HistoryHolder, position: Int) {
        val booking = BookingInfoo.getOrNull(position)
        val restoran = dataRestoran.getOrNull(position)

        if (booking != null && restoran != null) {
            holder.bind(booking, restoran)
        }

        holder.tvGambarResto.setOnClickListener{
//            Toast.makeText(holder.itemView.context, wayang.nama, Toast.LENGTH_LONG).show()
            onItemClickCallback.onItemClicked(BookingInfoo[position], dataRestoran[position])
        }

        holder._btnHapus.setOnClickListener{
            onItemClickCallback.delData(position)
        }
    }

    // function update data
    fun updateData(filteredBookings: List<BookingInfo>, filteredRestaurants: List<dataRestoran>) {
        BookingInfoo.clear()
        dataRestoran.clear()

        BookingInfoo.addAll(filteredBookings)
        dataRestoran.addAll(filteredRestaurants)

        notifyDataSetChanged()
    }

}