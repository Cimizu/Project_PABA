package project.paba.app

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso

class addRestoAdapter (private var restaurantList: List<dataRestoran>,
    private val onReserveClick: (dataRestoran) -> Unit
) : RecyclerView.Adapter<addRestoAdapter.RestaurantViewHolder>() {

    // Menambahkan metode untuk memperbarui data
    fun updateData(newList: List<dataRestoran>) {
        restaurantList = newList
        notifyDataSetChanged()
    }

    inner class RestaurantViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val restaurantImage: ImageView = view.findViewById(R.id.gambarResto)
        val namaResto: TextView = view.findViewById(R.id.namaRestoran)
        val jamBuka: TextView = view.findViewById(R.id.jamBuka)
        val jamTutup: TextView = view.findViewById(R.id.jamTutup)
        val location: TextView = view.findViewById(R.id.jalan)
        val deskripsi: TextView = view.findViewById(R.id.deskripsi)

        val btnLihatPaket : Button = view.findViewById(R.id.btnIsiPaket)
        val btnTambahPaket : Button = view.findViewById(R.id.btnAddPaket)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RestaurantViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.addrestoran_recycler, parent, false)
        return RestaurantViewHolder(view)
    }

    override fun getItemCount(): Int {
        return restaurantList.size
    }

    override fun onBindViewHolder(holder: RestaurantViewHolder, position: Int) {
        val restaurants = restaurantList[position]

        // Set text data
        holder.namaResto.text = restaurants.namaResto
        holder.jamBuka.text = restaurants.jambuka
        holder.jamTutup.text = restaurants.jamtutup
        holder.location.text = restaurants.lokasi
        holder.deskripsi.text = restaurants.deskripsi


        if (restaurants.foto.isNotEmpty()) {
            Picasso.get()
                .load(restaurants.foto)
                .placeholder(R.drawable.restoran)
                .error(R.drawable.restoran)
                .into(holder.restaurantImage) // Set the image into the ImageView
        } else {
            holder.restaurantImage.setImageResource(R.drawable.restoran)
        }

        holder.btnLihatPaket.setOnClickListener {
            val fragment = listAddPaket.newInstance(restaurants.idResto, "")
            val fragmentManager = (holder.itemView.context as? AppCompatActivity)?.supportFragmentManager
            fragmentManager?.beginTransaction()
                ?.replace(R.id.frameContainer, fragment)
                ?.addToBackStack(null)
                ?.commit()

        }


        holder.btnTambahPaket.setOnClickListener {
            val fragment = addPaket.newInstance(restaurants.idResto, "")
            val fragmentManager = (holder.itemView.context as? AppCompatActivity)?.supportFragmentManager
            fragmentManager?.beginTransaction()
                ?.replace(R.id.frameContainer, fragment)
                ?.addToBackStack(null)
                ?.commit()
        }


    }
}
