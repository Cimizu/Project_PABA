package project.paba.app

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.SearchView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [listAddResto.newInstance] factory method to
 * create an instance of this fragment.
 */
class listAddResto : Fragment() {
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var restaurantListRecyclerView: RecyclerView
    private lateinit var adapter: addRestoAdapter
    private val restaurantData = mutableListOf<dataRestoran>()
    private lateinit var btnBack : ImageView

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
        return inflater.inflate(R.layout.fragment_list_add_resto, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val addResto : Button = view.findViewById(R.id.AddResto)

        addResto.setOnClickListener {
            val mfEmpat = AddResto()
            val mFragmentManager = parentFragmentManager
            mFragmentManager.beginTransaction().apply {
                replace(R.id.frameContainer, mfEmpat, AddResto::class.java.simpleName)
                addToBackStack(null)
                commit()
            }
        }
        btnBack = view.findViewById(R.id.btnBack)
        btnBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        // Initialize RecyclerView
        restaurantListRecyclerView = view.findViewById(R.id.rvAddRestoran)
        restaurantListRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        adapter = addRestoAdapter(restaurantData) { restaurant ->
            // Action when the reserve button is clicked
            Toast.makeText(
                requireContext(),
                "Restoran untuk ${restaurant.namaResto}",
                Toast.LENGTH_SHORT
            ).show()
        }
        restaurantListRecyclerView.adapter = adapter



        if (auth.currentUser != null) {
            fetchRestaurantData()
        } else {
            restaurantData.clear()
            restaurantData.add(
                dataRestoran(
                    "Silakan login untuk melihat data restoran.",
                    "",
                    "",
                    "",
                    "",
                    "",
                    ""
                )
            )
            adapter.notifyDataSetChanged()
        }
    }

    private fun fetchRestaurantData() {
        db.collection("restoran")
            .get()
            .addOnSuccessListener { result ->
                restaurantData.clear()
                if (result.isEmpty) {
                    restaurantData.add(
                        dataRestoran(
                            "Tidak ada restoran yang ditemukan.",
                            "",
                            "",
                            "",
                            "",
                            "",
                            ""
                        )
                    )
                } else {
                    for (document in result) {
                        val uidRestoran = document.id
                        val namaResto = document.getString("namaResto") ?: "Tidak Ada Nama"
                        val namaResto2 = document.getString("namaResto2") ?: "Tidak Ada Nama"
                        val deskripsi = document.getString("deskripsi") ?: "Tidak Ada Deskripsi"
                        val lokasi = document.getString("lokasi") ?: "Tidak Ada Alamat"
                        val foto = document.getString("foto") ?: ""
                        val noTelp = document.getString("noTelp") ?: ""
                        val jambuka = document.getString("jambuka") ?: ""
                        val jamtutup = document.getString("jamtutup") ?: ""

                        Log.d("FirebaseData", "UID: $uidRestoran, namaResto: $namaResto")
                        restaurantData.add(
                            dataRestoran(
                                uidRestoran,
                                namaResto,
                                namaResto2,
                                deskripsi,
                                lokasi,
                                foto,
                                noTelp,
                                jambuka,
                                jamtutup
                            )
                        )
                    }
                }
                adapter.notifyDataSetChanged()
            }
            .addOnFailureListener { exception ->
                Toast.makeText(requireContext(), "Error: ${exception.message}", Toast.LENGTH_SHORT)
                    .show()
            }
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
