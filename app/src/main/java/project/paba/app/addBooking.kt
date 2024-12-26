package project.paba.app

import BookingInfo
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

    private lateinit var edtNama: EditText
    private lateinit var edtTanggal: EditText
    private lateinit var edtJam: EditText
    private lateinit var edtNotelp: EditText
    private lateinit var edtCttn: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_booking)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.booking)) { v, insets ->
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
                val bookingInfo = BookingInfo("resto 1", name, date, time, phone, notes)
                db.collection("bookings").add(bookingInfo)
                    .addOnSuccessListener {
                        val listIntent = Intent(this, bookingList::class.java)
                        startActivity(listIntent)
                        finish()
                    }
                    .addOnFailureListener { e ->
                        Log.e("Firebase", "Error adding document", e)
                    }
            } else {
                Log.e("Validation", "All fields must be filled")
            }
        }
    }
}