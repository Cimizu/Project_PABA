package project.paba.app

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.firestore.FirebaseFirestore

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [AddResto.newInstance] factory method to
 * create an instance of this fragment.
 */
class AddResto : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null


    private lateinit var edtNama: EditText
    private lateinit var edtAlamat: EditText
    private lateinit var edtJamBuka: EditText
    private lateinit var edtNoTelp: EditText
    private lateinit var edtDeskripsi: EditText
    private lateinit var btnUpdate: Button
    private lateinit var edtFoto:EditText
    private lateinit var edtJamTutup :EditText



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
        return inflater.inflate(R.layout.fragment_add_resto, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        edtNama = view.findViewById(R.id.edt_nama_resto)
        edtAlamat = view.findViewById(R.id.edtAlamat)
        edtJamBuka = view.findViewById(R.id.edtJamBuka)
        edtJamTutup = view.findViewById(R.id.edtJamTutup)
        edtNoTelp = view.findViewById(R.id.edtNoTelp)
        edtDeskripsi = view.findViewById(R.id.edtDeskripsi)
        btnUpdate = view.findViewById(R.id.btnMasuk)
        edtFoto = view.findViewById(R.id.edtFoto)

        // Firebase Firestore instance
        val db = FirebaseFirestore.getInstance()

        btnUpdate.setOnClickListener {
            // Collecting data from the EditTexts
            val namaResto = edtNama.text.toString().trim()
            val alamat = edtAlamat.text.toString().trim()
            val jamBuka = edtJamBuka.text.toString().trim()
            val jamTutup = edtJamTutup.text.toString().trim()
            val noTelp = edtNoTelp.text.toString().trim()
            val deskripsi = edtDeskripsi.text.toString().trim()
            val foto = edtFoto.text.toString().trim()

            if (namaResto.isEmpty() || alamat.isEmpty() || jamBuka.isEmpty() || jamTutup.isEmpty() || noTelp.isEmpty() || deskripsi.isEmpty()) {
                Toast.makeText(requireContext(), "Silahkan input semua field yang masih kosong!", Toast.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            }

            // Create a new dataRestoran object
            val restoran = dataRestoran(
                idResto = db.collection("restoran")
                    .document().id, // Generate a unique ID for the restaurant
                namaResto = namaResto,
                namaResto2 = namaResto,
                deskripsi = deskripsi,
                lokasi = alamat,
                foto = foto,
                noTelp = noTelp,
                jambuka = jamBuka,
                jamtutup = jamTutup
            )

            // Save to Firestore
            db.collection("restoran").document(restoran.idResto).set(restoran)
                .addOnSuccessListener {
                    Toast.makeText(
                        requireContext(),
                        "Restoran berhasil terinput",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(
                        requireContext(),
                        "Error input restoran: ${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }


        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment AddResto.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            AddResto().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}