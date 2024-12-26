package project.paba.app

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore

class bookingList : AppCompatActivity() {

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

        // Start addBooking activity for result
        val intent = Intent(this, addBooking::class.java)
        startActivityForResult(intent, REQUEST_CODE_ADD_BOOKING)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_ADD_BOOKING && resultCode == Activity.RESULT_OK) {
            val newBooking = data?.getParcelableExtra<BookingInfo>("BOOKING_INFO")
            newBooking?.let {
                bookingInfoList.add(it)
                bookingAdapter.notifyDataSetChanged()
            }
        }
    }

    private fun readData() {
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

    companion object {
        const val REQUEST_CODE_ADD_BOOKING = 1
    }
}