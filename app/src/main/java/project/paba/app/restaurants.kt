package project.paba.app

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore

class restaurants : Fragment() {

    private lateinit var restaurantsAdapter: RestaurantsAdapter
    private val db = FirebaseFirestore.getInstance()
    private val restaurantList = mutableListOf<dataRestoran>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_restaurants, container, false)

        val recyclerView: RecyclerView = view.findViewById(R.id.rvRestaurants)
        recyclerView.layoutManager = LinearLayoutManager(context)

        restaurantsAdapter = RestaurantsAdapter(restaurantList) { restaurantId ->
            val fragment = paket.newInstance(restaurantId)
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, fragment)
                .addToBackStack(null)
                .commit()
        }
        recyclerView.adapter = restaurantsAdapter

        fetchRestaurants()

        return view
    }

    private fun fetchRestaurants() {
        db.collection("resto").get()
            .addOnSuccessListener { result ->
                restaurantList.clear()
                for (document in result) {
                    val restaurant = document.toObject(dataRestoran::class.java)
                    restaurantList.add(restaurant)
                }
                restaurantsAdapter.notifyDataSetChanged()
            }
            .addOnFailureListener { e ->
                e.printStackTrace()
                Toast.makeText(context, "Failed to fetch restaurants", Toast.LENGTH_SHORT).show()
            }
    }
}