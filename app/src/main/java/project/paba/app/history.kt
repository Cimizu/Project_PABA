package project.paba.app

import BookingInfo
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
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
 * Use the [history.newInstance] factory method to
 * create an instance of this fragment.
 */
class history : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private val db = FirebaseFirestore.getInstance()
    private lateinit var auth: FirebaseAuth
    private lateinit var historyListRecyclerView: RecyclerView
    private lateinit var adapter: HistoryAdapter
    private val bookingList = arrayListOf<BookingInfo>()
    private val originalBookingList = arrayListOf<BookingInfo>()


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
        return inflater.inflate(R.layout.fragment_history, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        historyListRecyclerView = view.findViewById(R.id.rvHistory)
        historyListRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        adapter = HistoryAdapter(bookingList)
        historyListRecyclerView.adapter = adapter

        fetchBookingData()

        val btnUsed: Button = view.findViewById(R.id.btnUsed)
        val btnExpired: Button = view.findViewById(R.id.btnExpired)
        val btnSemua : Button = view.findViewById(R.id.btnSemua)

        btnSemua.setOnClickListener {
            filterBooking(null)
        }

        // Listener for btnSurabaya
        btnUsed.setOnClickListener {
            filterbyStatus("USED")
        }

        // Listener for btnJakarta
        btnExpired.setOnClickListener {
            filterbyStatus("EXPIRED")
        }
    }
    private fun fetchBookingData() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId != null) {
            db.collection("bookings")
                .whereEqualTo("userId", userId)
                .whereEqualTo("status_aktif", false)
                .get()
                .addOnSuccessListener { result ->
                    bookingList.clear()
                    for (document in result) {
                        val booking = document.toObject(BookingInfo::class.java)
                        bookingList.add(booking)
                        originalBookingList.add(booking)

                    }
                    adapter.notifyDataSetChanged()
                    Log.d("Firebase", "Data fetched: ${bookingList.size} items")
                }
                .addOnFailureListener { e ->
                    Log.e("Firebase", "Error reading documents", e)
                }
        } else {
            Log.e("Firebase", "User ID is null")
        }
    }

    private fun filterBooking(query: String?) {
        val filteredList = if (query.isNullOrEmpty()) {
            ArrayList(originalBookingList)
        } else {
            originalBookingList.filter { booking ->
                booking.statusString.contains(query, ignoreCase = true)
            }
        }
        adapter.updateData(filteredList)
    }

    private fun filterbyStatus(status: String) {
        val filteredList = originalBookingList.filter { booking ->
            booking.statusString.equals(status, ignoreCase = true)
        }
        adapter.updateData(filteredList)
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