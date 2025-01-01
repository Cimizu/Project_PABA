package project.paba.app

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
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
 * Use the [profile.newInstance] factory method to
 * create an instance of this fragment.
 */
class profile : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var displayNama: TextView
    private lateinit var displayEmail: TextView
    private lateinit var displayNomor: TextView
    private lateinit var logoutButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val _btnEdit = view.findViewById<ImageView>(R.id.btnEdit)
        val _btnPesanan = view.findViewById<Button>(R.id.btnPesanan)
        val _btnAddResto = view.findViewById<Button>(R.id.btnInputResto)



        _btnAddResto.setOnClickListener {
            val mfEmpat = listAddResto()
            val mFragmentManager = parentFragmentManager
            mFragmentManager.beginTransaction().apply {
                replace(R.id.frameContainer, mfEmpat, listAddResto::class.java.simpleName)
                addToBackStack(null)
                commit()
            }
        }
        _btnEdit.setOnClickListener {
            val mfDua = editProfile()

            val mFragmentManager = parentFragmentManager
            mFragmentManager.beginTransaction().apply {
                replace(R.id.frameContainer, mfDua, editProfile::class.java.simpleName)
                addToBackStack(null)
                commit()
            }
        }

        _btnPesanan.setOnClickListener {
            val mfTiga = BookingListFragment()

            val mFragmentManager = parentFragmentManager
            mFragmentManager.beginTransaction().apply {
                replace(R.id.frameContainer, mfTiga, BookingListFragment::class.java.simpleName)
                addToBackStack(null)
                commit()
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_profile, container, false)
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        // Inisialisasi TextView dan Button
        displayNama = view.findViewById(R.id.displayNama)
        displayEmail = view.findViewById(R.id.displayEmail)
        displayNomor = view.findViewById(R.id.displayNomor)
        logoutButton = view.findViewById(R.id.btnSignOut)

        val currentUser = auth.currentUser
        if (currentUser != null) {
            val uid = currentUser.uid

            // Mengambil data pengguna dari Firestore
            db.collection("Customer").document(uid)
                .get()
                .addOnSuccessListener { document ->
                    if (document != null && document.exists()) {
                        // Ambil data pengguna dan set ke TextView
                        displayNama.text = document.getString("nama")
                        displayEmail.text = document.getString("email")
                        displayNomor.text = document.getString("nomorTelepon")
                    } else {
                        Toast.makeText(
                            context,
                            "Data pengguna tidak ditemukan.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
                .addOnFailureListener { e ->
                    Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        } else {
            Toast.makeText(context, "Tidak ada pengguna yang login.", Toast.LENGTH_SHORT).show()
        }

        // Logout Button
        logoutButton.setOnClickListener {
            auth.signOut()  // Logout pengguna
            Toast.makeText(context, "Logout berhasil!", Toast.LENGTH_SHORT).show()

            // Navigasi kembali ke MainActivity (halaman login)
            val intent = Intent(activity, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
            requireActivity().finish()
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
         * @return A new instance of fragment profile.
         */
        // TODO: Rename and change types and number of parameters
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