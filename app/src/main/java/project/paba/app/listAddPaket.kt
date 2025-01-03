package project.paba.app

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
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
 * Use the [listAddPaket.newInstance] factory method to
 * create an instance of this fragment.
 */
class listAddPaket : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var btnBack : ImageView

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var paketListRecyclerView: RecyclerView
    private lateinit var adapter: addPaketAdapter
    private val paketData = mutableListOf<paketRestoran>()
    private var restoranId: String? = null
    private var namaRestoran: String? = null
    private var alamatRestoran: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        arguments?.let {
            restoranId = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }

        // Mengambil id restoran dari argumen yang dikirim

        Log.d("PaketFragment", "Restoran ID: $restoranId")

        fetchRestoranData()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_list_add_paket, container, false)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize RecyclerView
        paketListRecyclerView = view.findViewById(R.id.rvAddPaket)
        paketListRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        // Set up the adapter
        adapter = addPaketAdapter(paketData,restoranId) { paket ->
            Toast.makeText(requireContext(), "Memesan paket: ${paket.namaPaket}", Toast.LENGTH_SHORT).show()
        }
        paketListRecyclerView.adapter = adapter

        // Fetch data for the "paket"
        fetchPaketData()

        btnBack = view.findViewById(R.id.btnBack)
        btnBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
    }

    /**
     * Mengambil data restoran seperti nama dan alamat
     */
    private fun fetchRestoranData() {
        if (restoranId.isNullOrEmpty()) {
            Log.e("fetchRestoranData", "Restoran ID kosong.")
            return
        }

        db.collection("restoran")
            .document(restoranId!!)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    namaRestoran = document.getString("namaRestoran") ?: "Nama tidak tersedia"
                    alamatRestoran = document.getString("alamatRestoran") ?: "Alamat tidak tersedia"

                    Log.d("fetchRestoranData", "Nama Restoran: $namaRestoran, Alamat: $alamatRestoran")
                } else {
                    Log.e("fetchRestoranData", "Dokumen restoran tidak ditemukan.")
                }
            }
            .addOnFailureListener { exception ->
                Log.e("fetchRestoranData", "Error: ${exception.message}")
            }
    }

    /**
     * Mengambil data paket berdasarkan ID restoran
     */
    private fun fetchPaketData() {
        if (restoranId.isNullOrEmpty()) {
            Log.e("fetchPaketData", "Restoran ID kosong.")
            return
        }

        db.collection("restoran")
            .document(restoranId!!)
            .collection("paket")
            .get()
            .addOnSuccessListener { result ->
                paketData.clear()
                if (result.isEmpty) {
                    Log.d("fetchPaketData", "Tidak ada paket ditemukan.")
                } else {
                    for (document in result) {
                        val idPaket = document.id
                        val namaPaket = document.getString("namaPaket") ?: "Nama tidak tersedia"
                        val deskripsi = document.getString("deskripsi") ?: "Deskripsi tidak tersedia"
                        val kapasitas = document.getString("kapasitas") ?: "Kapasitas tidak tersedia"
                        val harga = document.getString("harga") ?: "Harga tidak tersedia"
                        val uangDp = document.getString("uangDp") ?: "Uang DP tidak tersedia"
                        val idResto = document.getString("restoranId") ?: "ID tidak tersedia"

                        paketData.add(
                            paketRestoran(
                                namaPaket,
                                deskripsi,
                                kapasitas,
                                harga,
                                uangDp,
                                idResto,
                                idPaket
                            )
                        )
                    }
                }
                adapter.notifyDataSetChanged()
            }
            .addOnFailureListener { exception ->
                Log.e("fetchPaketData", "Error: ${exception.message}")
            }
    }


    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment listAddPaket.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            listAddPaket().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}