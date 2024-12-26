package project.paba.app

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class RestaurantsAdapter(private val restaurantList: List<dataRestoran>, private val onBookingClick: (String) -> Unit) : RecyclerView.Adapter<RestaurantsAdapter.RestaurantViewHolder>() {

    class RestaurantViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val _tv_restaurant_name: TextView = itemView.findViewById(R.id.tv_restaurant_name)
        val _btn_booking: Button = itemView.findViewById(R.id.btn_booking)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RestaurantViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.restaurant_recycler, parent, false)
        return RestaurantViewHolder(view)
    }

    override fun onBindViewHolder(holder: RestaurantViewHolder, position: Int) {
        val restaurant = restaurantList[position]
        holder._tv_restaurant_name.text = restaurant.namaResto
        holder._btn_booking.setOnClickListener {
            onBookingClick(restaurant.id)
        }
    }

    override fun getItemCount(): Int {
        return restaurantList.size
    }
}