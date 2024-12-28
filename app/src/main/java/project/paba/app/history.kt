package project.paba.app

import BookingInfo
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [history.newInstance] factory method to
 * create an instance of this fragment.
 */
class history : Fragment() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    var DataBooking = ArrayList<BookingInfo>()
    var DataRestaurant = ArrayList<dataRestoran>()
    lateinit var lvAdapter : HistoryAdapter

    private var _nama: MutableList<String> = emptyList<String>().toMutableList()
    private var _resto: MutableList<String> = emptyList<String>().toMutableList()
    private var _gambarResto: MutableList<String> = emptyList<String>().toMutableList()
    private var _alamatresto: MutableList<String> = emptyList<String>().toMutableList()
    private var _jam: MutableList<String> = emptyList<String>().toMutableList()
    private var _tanggal: MutableList<String> = emptyList<String>().toMutableList()
    private var _status: MutableList<String> = emptyList<String>().toMutableList()


    private var arBooking = arrayListOf<BookingInfo>()
    private var arRestoran = arrayListOf<dataRestoran>()

    private lateinit var _rvHistory : RecyclerView

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

    fun TampilkanData(){
        _rvHistory.layoutManager = LinearLayoutManager(requireContext())

        val adapterHistory = HistoryAdapter(arBooking, arRestoran)
        _rvHistory.adapter = adapterHistory

        adapterHistory.setOnItemClickCallback(object : HistoryAdapter.OnItemClickCallback{
            override fun onItemClicked(data1: BookingInfo, data2: dataRestoran) {
                // Create the HistoryDetail fragment
                val fragment = HistoryDetail.newInstance(data1, data2)

                // Replace the current fragment with HistoryDetail
                requireActivity().supportFragmentManager.beginTransaction()
                    .replace(R.id.frameContainer, fragment)  // Replace the container with the HistoryDetail fragment
                    .addToBackStack(null)  // Optional: Add this transaction to the back stack, so the user can navigate back
                    .commit()  // Commit the transaction to execute the change
            }

            override fun delData(pos: Int) {
                AlertDialog.Builder(requireContext())
                    .setTitle("HAPUS DATA")
                    .setMessage("Apakah Benar Data Booking "+ _resto[pos] + " Akan Dihapus? ")
                    .setPositiveButton(
                        "HAPUS",
                        DialogInterface.OnClickListener{ dialog, which ->
                            _alamatresto.removeAt(pos)
                            _status.removeAt(pos)
                            _gambarResto.removeAt(pos)
                            _jam.removeAt(pos)
                            _tanggal.removeAt(pos)
                            TampilkanData()
                        }
                    )
                    .setNegativeButton(
                        "BATAL",
                        DialogInterface.OnClickListener{ dialog, which ->
                            Toast.makeText(
                                requireContext(),
                                "Data Batal Dihapus",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    ).show()
            }
        })
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize FirebaseAuth and FirebaseFirestore
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        // Check if the user is logged in, and fetch data accordingly
        if (auth.currentUser != null) {
            fetchRestaurantData()
        } else {
            arRestoran.clear()
            arRestoran.add(dataRestoran("Silakan login untuk melihat data restoran.", "", "", "", "", ""))
            lvAdapter.notifyDataSetChanged()
        }

        // Initialize RecyclerView and Adapter
        _rvHistory = view.findViewById(R.id.rvHistory)
        _rvHistory.layoutManager = LinearLayoutManager(requireContext())

        lvAdapter = HistoryAdapter(arBooking, arRestoran)
        _rvHistory.adapter = lvAdapter

        // Initialize filter buttons
        val _btnSemua: Button = view.findViewById(R.id.btnSemua)
        val _btnActive: Button = view.findViewById(R.id.btnActive)
        val _btnExpired: Button = view.findViewById(R.id.btnExpired)

        // Set up listeners for filter buttons
        _btnSemua.setOnClickListener {
            filterHistory(null)  // Reset filter (show all)
        }

        _btnActive.setOnClickListener {
            filterByStatus("Active")  // Filter active bookings
        }

        _btnExpired.setOnClickListener {
            filterByStatus("Expired")  // Filter expired bookings
        }

        // Assuming pos is passed from somewhere, here is how to use it:
        val _btnHapus: Button = view.findViewById(R.id.btnHapus)
        _btnHapus.setOnClickListener {
            val pos = getSelectedItemPosition()
            if (pos != -1) {  // cek posisi
                delData(pos)
            } else {
                Toast.makeText(requireContext(), "No item selected", Toast.LENGTH_SHORT).show()
            }
        }

        // fungsi delData pindah
        fun delData(pos: Int) {
            AlertDialog.Builder(requireContext())
                .setTitle("HAPUS DATA")
                .setMessage("Apakah Benar Data Booking ${_resto[pos]} Akan Dihapus? ")
                .setPositiveButton(
                    "HAPUS",
                    DialogInterface.OnClickListener { dialog, which ->
                        // Remove data from lists at position `pos`
                        _alamatresto.removeAt(pos)
                        _status.removeAt(pos)
                        _gambarResto.removeAt(pos)
                        _jam.removeAt(pos)
                        _tanggal.removeAt(pos)
                        // Refresh data
                        TampilkanData()
                    }
                )
                .setNegativeButton(
                    "BATAL",
                    DialogInterface.OnClickListener { dialog, which ->
                        Toast.makeText(
                            requireContext(),
                            "Data Batal Dihapus",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                ).show()
        }


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_history, container, false)
    }

    private fun fetchRestaurantData() {
        db.collection("restoran")
            .get()
            .addOnSuccessListener { result ->
                arRestoran.clear()
                if (result.isEmpty) {
                    arRestoran.add(dataRestoran("Tidak ada restoran yang ditemukan.", "", "", "", "", ""))
                } else {
                    for (document in result) {
                        val uidRestoran = document.id  // Mendapatkan ID dokumen (UID)
                        val namaResto = document.getString("namaResto") ?: "Tidak Ada Nama"
                        val namaResto2 = document.getString("namaResto2") ?: "Tidak Ada Nama"
                        val lokasi = document.getString("lokasi") ?: "Tidak Ada Alamat"
                        val foto = document.getString("foto") ?: ""

                        Log.d("FirebaseData", "UID: $uidRestoran, namaResto: $namaResto")
                        arRestoran.add(dataRestoran(uidRestoran, namaResto, namaResto2, lokasi, foto))
                    }
                }
                lvAdapter.notifyDataSetChanged()
            }
            .addOnFailureListener { exception ->
                Toast.makeText(requireContext(), "Error: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun fetchBookingData() {
        db.collection("bookings")
            .get()
            .addOnSuccessListener { result ->
                arBooking.clear()
                if (result.isEmpty) {
                    arBooking.add(BookingInfo(1111, "", "", "", "", ""))
                } else {
                    for (document in result) {
                        val uidBooking = document.id.toInt()  // Mendapatkan ID dokumen (UID)
                        val namaResto = document.getString("namaResto") ?: "Tidak Ada Nama Resto"
                        val namaPemesan = document.getString("namaResto2") ?: "Tidak Ada Nama Pemesan"
                        val address = document.getString("address") ?: "Tidak Ada address"
                        val date = document.getString("date") ?: "Tidak Ada date"
                        val time = document.getString("time") ?: "Tidak Ada time"
                        val notes = document.getString("notes") ?: "Tidak Ada notes"

                        Log.d("FirebaseData", "UID: $uidBooking, namaResto: $namaResto")
                        arBooking.add(BookingInfo(uidBooking, namaResto, namaPemesan,address, date, time, "", notes, ))
                    }
                }
                lvAdapter.notifyDataSetChanged()
            }
            .addOnFailureListener { exception ->
                Toast.makeText(requireContext(), "Error: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun filterHistory(query: String?) {
        val filteredBookings = mutableListOf<BookingInfo>()
        val filteredRestaurants = mutableListOf<dataRestoran>()

        if (query.isNullOrEmpty()) {
            // If query is empty, show all items
            filteredBookings.addAll(arBooking)
            filteredRestaurants.addAll(arRestoran)
        } else {
            // Filter the bookings based on the query
            for (booking in arBooking) {
                if (booking.status.contains(query, ignoreCase = true) || booking.date.contains(query, ignoreCase = true)) {
                    filteredBookings.add(booking)
                }
            }

            // Filter the restaurants based on the query
            for (restaurant in arRestoran) {
                if (restaurant.namaResto.contains(query, ignoreCase = true)) {
                    filteredRestaurants.add(restaurant)
                }
            }
        }

        // Update the adapter with the filtered data
        lvAdapter.updateData(filteredBookings, filteredRestaurants)
    }


    private fun filterByStatus(status: String) {
        // Create a filtered list to hold bookings with the specified status
        val filteredBookings = mutableListOf<BookingInfo>()

        // Loop through the BookingInfoo list and filter by status
        for (booking in arBooking) {
            if (booking.status.equals(status, ignoreCase = true)) {
                filteredBookings.add(booking)
            }
        }

        // Update the adapter with the filtered bookings
        lvAdapter.updateData(filteredBookings, arRestoran)
    }



    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment history.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            history().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}