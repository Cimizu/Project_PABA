package project.paba.app

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [paket.newInstance] factory method to
 * create an instance of this fragment.
 */
class paket : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var _namaPaket: TextView
    private lateinit var _dekskripsi: TextView
    private lateinit var _harga: TextView
    private lateinit var _uangDp: TextView
    private lateinit var _kapasitas: TextView
    private lateinit var _btnPesan: Button
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_paket, container, false)

        // Inisialisasi Firebase
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        // Inisialisasi Views
        _namaPaket = view.findViewById(R.id.namaPaket)
        _dekskripsi = view.findViewById(R.id.dekskripsi)
        _kapasitas = view.findViewById(R.id.kapasitas)
        _uangDp = view.findViewById(R.id.uangDp)
        _harga = view.findViewById(R.id.harga)
        _btnPesan = view.findViewById(R.id.btnPesan)

        // Mengambil data dari Firestore
        loadPaketData()

        // Set listener untuk tombol pesan
        _btnPesan.setOnClickListener {
            pesanPaket()
        }

        return view
    }

    private fun loadPaketData() {
        // Ambil ID restoran dan ID paket dari argument atau sumber lain
        val idRestoran = "1" // Ganti dengan ID restoran yang sesuai
        val idPaket = "1" // Ganti dengan ID paket yang sesuai

        // Query Firestore sesuai dengan struktur yang diinginkan
        db.collection("restoran")
            .document(idRestoran) // Mengakses restoran berdasarkan ID
            .collection("paket") // Mengakses koleksi paket di dalam restoran
            .document(idPaket) // Mengakses paket berdasarkan ID paket
            .get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    // Mengambil data dari dokumen
                    val namaPaket = document.getString("namaPaket")
                    val deskripsi = document.getString("deskripsi")
                    val kapasitas = document.getLong("kapasitas")?.toInt() ?: 0
                    val harga = document.getString("harga")
                    val uangDp = document.getString("uangDp")

                    // Menampilkan data ke UI
                    _namaPaket.text = namaPaket
                    _dekskripsi.text = deskripsi
                    _kapasitas.text = kapasitas.toString()
                    _harga.text = harga
                    _uangDp.text = uangDp
                } else {
                    Toast.makeText(requireContext(), "Data paket tidak ditemukan", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { exception ->
                Toast.makeText(requireContext(), "Gagal memuat data: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun pesanPaket() {
        // Fungsi untuk menangani logika pemesanan
        Toast.makeText(requireContext(), "Paket berhasil dipesan!", Toast.LENGTH_SHORT).show()
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment paket.
         */
        // TODO: Rename and change types and number of parameters
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