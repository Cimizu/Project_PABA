package project.paba.app

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.firestore.FirebaseFirestore
import java.util.UUID

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [addPaket.newInstance] factory method to
 * create an instance of this fragment.
 */
class addPaket : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var edtNamaPaket: EditText
    private lateinit var edtHarga: EditText
    private lateinit var edtDP: EditText
    private lateinit var edtDeskripsi: EditText
    private lateinit var edtKapasitas: EditText
    private lateinit var btnInput: Button
    private var idRestoran: String? = null
    private lateinit var tvNamaResto: TextView
    private lateinit var tvAlamatResto: TextView
    private lateinit var inputFoto: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            idRestoran = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_add_paket, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        edtNamaPaket = view.findViewById(R.id.edtNamaPaket)
        edtHarga = view.findViewById(R.id.edtHarga)
        edtDP = view.findViewById(R.id.edtDP)
        edtDeskripsi = view.findViewById(R.id.edtDeskripsi)
        edtKapasitas = view.findViewById(R.id.edtKapasitas)
        btnInput = view.findViewById(R.id.btnInputPaket)
        inputFoto=view.findViewById(R.id.edtFoto)
        tvNamaResto = view.findViewById(R.id.tv_namaResto)
        tvAlamatResto = view.findViewById(R.id.tv_alamatResto)

        fetchDataRestoran()

        val db = FirebaseFirestore.getInstance()

        btnInput.setOnClickListener {
            val namaPaket = edtNamaPaket.text.toString().trim()
            val harga = edtHarga.text.toString().trim()
            val DP = edtDP.text.toString().trim()
            val Deskripsi = edtDeskripsi.text.toString().trim()
            val kapasitas = edtKapasitas.text.toString().trim()
            val foto = inputFoto.text.toString().trim()

            val idResto = this.idRestoran ?: "defaultId" // Fallback if idRestoran is null

            if (namaPaket.isEmpty() || harga.isEmpty() || DP.isEmpty() || Deskripsi.isEmpty() || kapasitas.isEmpty()) {
                Toast.makeText(
                    requireContext(),
                    "Silahkan input semua field yang masih kosong!",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            val idPaket = UUID.randomUUID().toString()
            val paketRestoran = paketRestoran(
                namaPaket = namaPaket,
                deskripsi = Deskripsi,
                kapasitas = kapasitas,
                harga = harga,
                uangDp = DP,
                idRestoran = idResto,
                idPaket = idPaket,
                foto = foto
            )

            db.collection("restoran").document(idResto)
                .collection("paket").document(idPaket)
                .set(paketRestoran)
                .addOnSuccessListener {
                    Toast.makeText(
                        requireContext(),
                        "Paket berhasil ditambahkan!",
                        Toast.LENGTH_SHORT
                    ).show()
                    clearFields()

                    parentFragmentManager.popBackStack()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(
                        requireContext(),
                        "Gagal menambahkan paket: ${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
        }
    }

    private fun fetchDataRestoran() {
        val db = FirebaseFirestore.getInstance()

        // cari nama resto di id restoran
        idRestoran?.let {
            db.collection("restoran").document(it)
                .get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        val namaResto = document.getString("namaResto")
                        val alamatResto = document.getString("lokasi")
                        tvNamaResto.text = namaResto ?: "Nama tidak ditemukan"
                        tvAlamatResto.text = alamatResto ?: "Alamat tidak ditemukan"
                    }
                }
                .addOnFailureListener { e ->
                    Toast.makeText(requireContext(), "Gagal memuat data restoran: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun clearFields() {
        edtNamaPaket.text.clear()
        edtHarga.text.clear()
        edtDP.text.clear()
        edtDeskripsi.text.clear()
        edtKapasitas.text.clear()
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment addPaket.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            addPaket().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}