package project.paba.app

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [editProfile.newInstance] factory method to
 * create an instance of this fragment.
 */
class editProfile : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var inputNama: EditText
    private lateinit var inputNomor: EditText
    private lateinit var inputEmail: EditText
    private lateinit var inputPassword: EditText
    private lateinit var btnSimpan: Button
    private lateinit var btnBack: ImageView


    private val auth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }
    private val db: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }

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
        val view = inflater.inflate(R.layout.fragment_edit_profile, container, false)
        inputNama = view.findViewById(R.id.inputNama)
        inputNomor = view.findViewById(R.id.inputNomor)
        inputEmail = view.findViewById(R.id.inputEmail)
        inputPassword = view.findViewById(R.id.inputPassword)
        btnSimpan = view.findViewById(R.id.btnSimpan)
        btnBack = view.findViewById(R.id.btnBack)
        val userId = auth.currentUser?.uid
        if (userId != null) {
            db.collection("Customer").document(userId).get().addOnSuccessListener { document ->
                if (document != null) {
                    inputNama.setText(document.getString("nama"))
                    inputNomor.setText(document.getString("nomorTelepon"))
                    inputEmail.setText(document.getString("email"))
                }
            }.addOnFailureListener {
                Toast.makeText(context, "Gagal memuat profil.", Toast.LENGTH_SHORT).show()
            }
        }
        btnBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        // Tombol Simpan
        btnSimpan.setOnClickListener {
            val nama = inputNama.text.toString().trim()
            val nomorTelepon = inputNomor.text.toString().trim()
            val passwordBaru = inputPassword.text.toString().trim()

            if (nama.isEmpty() || nomorTelepon.isEmpty()) {
                Toast.makeText(context, "Nama dan Nomor Telepon harus diisi.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val updates = hashMapOf<String,Any>(
                "nama" to nama,
                "nomorTelepon" to nomorTelepon,
                "password" to passwordBaru
            )

            if (passwordBaru.isNotEmpty()) {
                auth.currentUser?.updatePassword(passwordBaru)?.addOnCompleteListener { task ->
                    if (!task.isSuccessful) {
                        Toast.makeText(context, "Gagal mengubah password.", Toast.LENGTH_SHORT).show()
                    }
                }
            }

            db.collection("Customer").document(userId!!).update(updates)
                .addOnSuccessListener {
                    Toast.makeText(context, "Profil berhasil diperbarui.", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener {
                    Toast.makeText(context, "Gagal memperbarui profil.", Toast.LENGTH_SHORT).show()
                }
        }

        return view
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment editProfile.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            editProfile().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}