package project.paba.app

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore

class bookingList : AppCompatActivity(){

    private lateinit var bookingAdapter: BookingAdapter
    private val db = FirebaseFirestore.getInstance()
    private val bookingInfoList = ArrayList<BookingInfo>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_booking_list)

        val recyclerView: RecyclerView = findViewById(R.id.rvBooking)
        recyclerView.layoutManager = LinearLayoutManager(this)

        bookingAdapter = BookingAdapter(bookingInfoList)
        recyclerView.adapter = bookingAdapter

        readData()
    }

    fun readData() {
        db.collection("bookings").get()
            .addOnSuccessListener { result ->
                bookingInfoList.clear()
                for (document in result) {
                    val booking = document.toObject(BookingInfo::class.java)
                    bookingInfoList.add(booking)
                }
                bookingAdapter.notifyDataSetChanged()
            }
            .addOnFailureListener { e ->
                Log.e("Firebase", "Error reading documents", e)
            }
    }

}