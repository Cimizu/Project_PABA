package project.paba.app

import adapterPaket
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_paket, container, false)

        // Inisialisasi RecyclerView
        paketListRecyclerView = view.findViewById(R.id.rvPaket)
        paketListRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        adapter = adapterPaket(paketData) { paket ->
            // Aksi ketika paket dipilih
            Toast.makeText(requireContext(), "Memesan paket: ${paket.namaPaket}", Toast.LENGTH_SHORT).show()
        }
        paketListRecyclerView.adapter = adapter

        // Ambil data dari Firestore
        fetchPaketData()

        return view
    }

    private fun fetchPaketData() {
        db.collection("paket")
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

                        Log.d("FirebaseData", "namaPaket: $namaPaket, deskripsi: $deskripsi, kapasitas: $kapasitas, harga: $harga, uangDp: $uangDp")

                        // Tambahkan data paket ke dalam list
                        paketData.add(paketRestoran(namaPaket, deskripsi, kapasitas, harga, uangDp))
                    }
                }

                // Notifikasi bahwa data telah diperbarui
                adapter.notifyDataSetChanged()
            }
            .addOnFailureListener { exception ->
                Toast.makeText(requireContext(), "Error: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
    }
    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            profile().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
