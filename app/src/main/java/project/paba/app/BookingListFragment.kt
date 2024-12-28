package project.paba.app

import BookingInfo
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class BookingListFragment : Fragment() {

    private val db = FirebaseFirestore.getInstance()
    private lateinit var bookingListRecyclerView: RecyclerView
    private lateinit var adapter: BookingAdapter
    private val bookingList = mutableListOf<BookingInfo>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_booking_list, container, false)

        bookingListRecyclerView = view.findViewById(R.id.rvBooking)
        bookingListRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        adapter = BookingAdapter(bookingList)
        bookingListRecyclerView.adapter = adapter

        fetchBookingData()

        return view
    }

    private fun fetchBookingData() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId != null) {
            db.collection("bookings")
                .whereEqualTo("userId", userId)
                .get()
                .addOnSuccessListener { result ->
                    bookingList.clear()
                    for (document in result) {
                        val booking = document.toObject(BookingInfo::class.java)
                        bookingList.add(booking)
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
}