package project.paba.app

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*

class addBooking : AppCompatActivity() {

    private val db = FirebaseFirestore.getInstance()
    private val bookingInfoList = ArrayList<BookingInfo>()

    private lateinit var bookingAdapter: BookingAdapter
    private lateinit var edtNama: EditText
    private lateinit var edtTanggal: EditText
    private lateinit var edtJam: EditText
    private lateinit var edtNotelp: EditText
    private lateinit var edtCttn: EditText

    private val data: MutableList<Map<String, String>> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_booking)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        edtNama = findViewById(R.id.edt_nama)
        edtTanggal = findViewById(R.id.edt_tanggal)
        edtJam = findViewById(R.id.edt_jam)
        edtNotelp = findViewById(R.id.edt_notelp)
        edtCttn = findViewById(R.id.edt_cttn)

        val btnBookingNow: Button = findViewById(R.id.btn_bookingNow)

        edtTanggal.setOnClickListener {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            val datePickerDialog = DatePickerDialog(this, { _, selectedYear, selectedMonth, selectedDay ->
                edtTanggal.setText("$selectedDay/${selectedMonth + 1}/$selectedYear")
            }, year, month, day)
            datePickerDialog.show()
        }

        edtJam.setOnClickListener {
            val calendar = Calendar.getInstance()
            val hour = calendar.get(Calendar.HOUR_OF_DAY)
            val minute = calendar.get(Calendar.MINUTE)

            val timePickerDialog = TimePickerDialog(this, { _, selectedHour, selectedMinute ->
                edtJam.setText("$selectedHour:$selectedMinute")
            }, hour, minute, true)
            timePickerDialog.show()
        }

        btnBookingNow.setOnClickListener {
            val name = edtNama.text.toString()
            val date = edtTanggal.text.toString()
            val time = edtJam.text.toString()
            val phone = edtNotelp.text.toString()
            val notes = edtCttn.text.toString()

            if (name.isNotEmpty() && date.isNotEmpty() && time.isNotEmpty() && phone.isNotEmpty() && notes.isNotEmpty()) {
                TambahData(db, name, date, time, phone, notes)
            } else {
                Log.e("Validation", "All fields must be filled")
            }
        }
    }

     fun TambahData(db: FirebaseFirestore, name: String, date: String, time: String, phone: String, notes: String) {
        val bookingInfo = BookingInfo(name, date, time, phone, notes)
        db.collection("bookings").add(bookingInfo)
            .addOnSuccessListener {
                Log.d("Firebase", "Data successfully saved")
                readData(db)
            }
            .addOnFailureListener { e ->
                Log.e("Firebase", "Error adding document", e)
            }
    }

    fun readData(db: FirebaseFirestore) {
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