package project.paba.app

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class paket : Fragment() {
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var paketListRecyclerView: RecyclerView
    private lateinit var adapter: adapterPaket
    private val paketData = mutableListOf<paketRestoran>()
    private var restoranId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        restoranId = arguments?.getString("restoranId")
        Log.d("PaketFragment", "Restoran ID: $restoranId")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_paket, container, false)

        paketListRecyclerView = view.findViewById(R.id.rvPaket)
        paketListRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        adapter = adapterPaket(paketData) { paket ->
            Toast.makeText(requireContext(), "Memesan paket: ${paket.namaPaket}", Toast.LENGTH_SHORT).show()
        }
        paketListRecyclerView.adapter = adapter

        fetchPaketData()

        return view
    }

    private fun fetchPaketData() {
        // Mengakses subkoleksi "paket" dalam koleksi "restoran" berdasarkan restoranId
        db.collection("restoran") // Koleksi restoran
            .document(restoranId ?: "") // ID restoran yang diterima sebagai argument
            .collection("paket") // Subkoleksi "paket" dalam setiap restoran
            .get()
            .addOnSuccessListener { result ->
                paketData.clear()
                if (result.isEmpty) {
                    paketData.add(paketRestoran("Tidak ada paket yang ditemukan.", "", "", "", ""))
                } else {
                    for (document in result) {
                        val namaPaket = document.getString("namaPaket") ?: "Nama tidak tersedia"
                        val deskripsi = document.getString("deskripsi") ?: "Deskripsi tidak tersedia"
                        val kapasitas = document.getString("kapasitas") ?: "Kapasitas tidak tersedia"
                        val harga = document.getString("harga") ?: "Harga tidak tersedia"
                        val uangDp = document.getString("uangDp") ?: "Uang DP tidak tersedia"

                        paketData.add(paketRestoran(namaPaket, deskripsi, kapasitas, harga, uangDp))
                    }
                }

                adapter.notifyDataSetChanged()
            }
            .addOnFailureListener { exception ->
                Toast.makeText(requireContext(), "Error: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
    }
    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            paket().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
