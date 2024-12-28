import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import project.paba.app.R
import project.paba.app.dataRestoran
import project.paba.app.detail_resto
import project.paba.app.restaurants

class adapterRestoran(
    private var restaurantList: List<dataRestoran>, // Menggunakan var untuk memungkinkan modifikasi data
    private val onReserveClick: (dataRestoran) -> Unit // Menggunakan lambda untuk event handler
) : RecyclerView.Adapter<adapterRestoran.RestaurantViewHolder>() {

    // Menambahkan metode untuk memperbarui data
    fun updateData(newList: List<dataRestoran>) {
        restaurantList = newList
        notifyDataSetChanged()
    }

    inner class RestaurantViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val restaurantImage: ImageView = view.findViewById(R.id.restaurantImage)
        val namaResto: TextView = view.findViewById(R.id.namaResto)
        val namaResto2: TextView = view.findViewById(R.id.namaResto2)
        val reserveButton: Button = view.findViewById(R.id.reserveButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RestaurantViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.restaurant_recycler, parent, false)
        return RestaurantViewHolder(view)
    }

    override fun onBindViewHolder(holder: RestaurantViewHolder, position: Int) {
        val restaurants = restaurantList[position]

        // Set text data
        holder.namaResto.text = restaurants.namaResto
        holder.namaResto2.text = restaurants.namaResto2

        if (restaurants.foto.isNotEmpty()) {
            Picasso.get()
                .load(restaurants.foto) // Load the image from the URL stored in the foto field
                .placeholder(R.drawable.restoran) // Placeholder image while loading
                .error(R.drawable.restoran) // Error image if loading fails
                .into(holder.restaurantImage) // Set the image into the ImageView
        } else {
            // Fallback if no photo URL is provided
            holder.restaurantImage.setImageResource(R.drawable.restoran)
        }

        holder.reserveButton.setOnClickListener {
            val context = holder.itemView.context
            if (context is androidx.fragment.app.FragmentActivity) { // Pastikan context adalah FragmentActivity
                val detailFragment = detail_resto().apply {
                    arguments = Bundle().apply {
                        putParcelable(
                            "kirimData",
                            restaurants
                        ) // Kirim data restoran sebagai Parcelable
                    }
                }

                // Ganti fragment
                context.supportFragmentManager.beginTransaction()
                    .replace(
                        R.id.frameContainer,
                        detailFragment
                    ) // Gunakan ID container dari layout utama
                    .addToBackStack(null) // Untuk mendukung back navigation
                    .commit()
            }
        }
    }

    override fun getItemCount(): Int {
        return restaurantList.size
    }
}
