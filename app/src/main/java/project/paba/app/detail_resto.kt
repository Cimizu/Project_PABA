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
//import com.squareup.picasso.Picasso

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [detail_resto.newInstance] factory method to
 * create an instance of this fragment.
 */
class detail_resto : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

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

        return inflater.inflate(R.layout.fragment_detail_resto, container, false)
    }
        override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
            super.onViewCreated(view, savedInstanceState)
        // Bind UI elements
        val _restoranImage = view.findViewById<ImageView>(R.id.restaurantImage)
        val _namaResto = view.findViewById<TextView>(R.id.namaResto)
        val _namaResto2 = view.findViewById<TextView>(R.id.namaResto2)
        val _jambuka = view.findViewById<TextView>(R.id.jambuka)
        val _jamtutup = view.findViewById<TextView>(R.id.jamtutup)
        val _lokasi = view.findViewById<TextView>(R.id.lokasi)
        val _noTelp = view.findViewById<TextView>(R.id.noTelp)
        val _deskripsi = view.findViewById<TextView>(R.id.deskripsi)
//        val _btnreservasi = view.findViewById<Button>(R.id.btnreservasi)
        val _btnLihatpaket = view.findViewById<Button>(R.id.btnLihatpaket)

        // Get data passed from arguments
        val dataIntent = arguments?.getParcelable<dataRestoran>("kirimData")
        dataIntent?.let {
            _namaResto.text = it.namaResto
            _namaResto2.text = it.namaResto2
            _jambuka.text = it.jambuka
            _jamtutup.text = it.jamtutup
            _lokasi.text = it.lokasi
            _noTelp.text = it.noTelp
            _deskripsi.text = it.deskripsi

//            if (it.foto.isNotEmpty()) {
//                Picasso.get()
//                    .load(it.foto) // Load the image from the URL
//                    .placeholder(R.drawable.restoran) // Placeholder image while loading
//                    .error(R.drawable.restoran) // Error image if loading fails
//                    .into(_restoranImage) // Set the image into the ImageView
//            } else {
//                // Fallback if no photo URL is provided
//                _restoranImage.setImageResource(R.drawable.restoran)
//            }
        }

        // Button for booking
//        _btnreservasi.setOnClickListener {
//            val intent = Intent(requireContext(), addBooking::class.java)
//            intent.putExtra("namaResto", dataIntent?.namaResto)
//            intent.putExtra("alamatResto", dataIntent?.lokasi)
//            startActivity(intent)
//        }

        // Button to navigate to Paket (Fragment)
        _btnLihatpaket.setOnClickListener {
            val restoranId = dataIntent?.idResto

            // Create new Fragment
            val paketFragment = paket().apply {
                arguments = Bundle().apply {
                    putString("restoranId", restoranId) // Send restaurant ID to Fragment
                }
            }

            // Check if the fragment is already added
            val existingFragment =
                parentFragmentManager.findFragmentByTag(paket::class.java.simpleName)
            if (existingFragment == null) {
                parentFragmentManager.beginTransaction()
                    .replace(R.id.frameContainer, paketFragment) // Replace with the correct container ID
                    .addToBackStack(null) // To enable back navigation
                    .commit()
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
         * @return A new instance of fragment detail_resto.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            detail_resto().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}