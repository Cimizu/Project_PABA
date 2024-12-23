package project.paba.app

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class bookingList : AppCompatActivity() {
    private lateinit var bookingAdapter: BookingAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_booking_list)

        val name = intent.getStringExtra("NAME")
        val date = intent.getStringExtra("DATE")
        val time = intent.getStringExtra("TIME")
        val phone = intent.getStringExtra("PHONE")
        val notes = intent.getStringExtra("NOTES")

        val bookingInfo = BookingInfo(name ?: "", date ?: "", time ?: "", phone ?: "", notes ?: "")

        bookingAdapter = BookingAdapter(listOf(bookingInfo))

        val recyclerView: RecyclerView = findViewById(R.id.rvBooking)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = bookingAdapter
    }
}