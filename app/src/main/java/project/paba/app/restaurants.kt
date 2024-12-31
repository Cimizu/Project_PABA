package project.paba.app

import adapterRestoran
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.SearchView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class restaurants : Fragment() {
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var restaurantListRecyclerView: RecyclerView
    private lateinit var adapter: adapterRestoran
    private val restaurantData = mutableListOf<dataRestoran>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
    }

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_restaurants, container, false)

        // Inisialisasi RecyclerView
        restaurantListRecyclerView = view.findViewById(R.id.rvRestorant)
        restaurantListRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        adapter = adapterRestoran(restaurantData) { restaurant ->
            // Tindakan saat tombol reserve diklik
            Toast.makeText(requireContext(), "Reservasi untuk ${restaurant.namaResto}", Toast.LENGTH_SHORT).show()
        }
        restaurantListRecyclerView.adapter = adapter

        // Inisialisasi SearchView
        val searchView: SearchView = view.findViewById(R.id.searchView)

        // Menambahkan listener pada SearchView untuk melakukan pencarian
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                // Bisa dibiarkan kosong atau digunakan untuk aksi lain jika diperlukan
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                // Filter daftar restoran berdasarkan nama
                filterRestaurants(newText)
                return true
            }
        })

        // Menginisialisasi tombol filter
        val btnSurabaya: Button = view.findViewById(R.id.btnSurabaya)
        val btnJakarta: Button = view.findViewById(R.id.btnJakarta)
        val btnSemua: Button = view.findViewById(R.id.btnSemua)



        // Listener untuk btnSemua
        btnSemua.setOnClickListener {
            // Menampilkan semua restoran (reset filter)
            filterRestaurants(null)
        }

        // Listener untuk btnSurabaya
        btnSurabaya.setOnClickListener {
            filterByLocation("Surabaya")
        }

        // Listener untuk btnJakarta
        btnJakarta.setOnClickListener {
            filterByLocation("Jakarta")
        }

        if (auth.currentUser != null) {
            fetchRestaurantData()
        } else {
            restaurantData.clear()
            restaurantData.add(dataRestoran("Silakan login untuk melihat data restoran.", "", "", "", "", "", ""))
            adapter.notifyDataSetChanged()
        }

        return view
    }

    private fun fetchRestaurantData() {
        db.collection("restoran")
            .get()
            .addOnSuccessListener { result ->
                restaurantData.clear()
                if (result.isEmpty) {
                    restaurantData.add(dataRestoran("Tidak ada restoran yang ditemukan.", "", "", "", "", "", "", ""))
                } else {
                    for (document in result) {
                        val uidRestoran = document.id  // Mendapatkan ID dokumen (UID)
                        val namaResto = document.getString("namaResto") ?: "Tidak Ada Nama"
                        val namaResto2 = document.getString("namaResto2") ?: "Tidak Ada Nama"
                        val deskripsi = document.getString("deskripsi") ?: "Tidak Ada Deskripsi"
                        val lokasi = document.getString("lokasi") ?: "Tidak Ada Alamat"
                        val foto = document.getString("foto") ?: ""
                        val noTelp = document.getString("noTelp") ?: ""
                        val jambuka = document.getString("jambuka") ?: ""
                        val jamtutup = document.getString("jamtutup") ?: ""

                        Log.d("FirebaseData", "UID: $uidRestoran, namaResto: $namaResto")
                        restaurantData.add(dataRestoran(uidRestoran, namaResto, namaResto2, deskripsi, lokasi, foto, noTelp, jambuka, jamtutup))
                    }
                }
                adapter.notifyDataSetChanged()
            }
            .addOnFailureListener { exception ->
                Toast.makeText(requireContext(), "Error: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
    }
    private fun filterRestaurants(query: String?) {
        val filteredList = mutableListOf<dataRestoran>()
        if (query.isNullOrEmpty()) {
            filteredList.addAll(restaurantData) // Tampilkan semua restoran jika pencarian kosong
        } else {
            for (restaurant in restaurantData) {
                if (restaurant.namaResto.contains(query, ignoreCase = true)) {
                    filteredList.add(restaurant)
                }
            }
        }
        adapter.updateData(filteredList)
    }

    private fun filterByLocation(location: String) {
        val filteredList = mutableListOf<dataRestoran>()
        for (restaurant in restaurantData) {
            if (restaurant.lokasi.contains(location, ignoreCase = true)) {
                filteredList.add(restaurant)
            }
        }
        adapter.updateData(filteredList)
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            restaurants().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

}
