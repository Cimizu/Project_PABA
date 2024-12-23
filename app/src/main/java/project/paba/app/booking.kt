package project.paba.app

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import java.util.*

class booking : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_booking)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val edtTanggal: EditText = findViewById(R.id.edt_tanggal)
        val edtJam: EditText = findViewById(R.id.edt_jam)

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

        val bookingButton: Button = findViewById(R.id.btn_bookingNow)
        bookingButton.setOnClickListener {
            val name = findViewById<EditText>(R.id.edt_nama).text.toString()
            val date = edtTanggal.text.toString()
            val time = edtJam.text.toString()
            val phone = findViewById<EditText>(R.id.edt_notelp).text.toString()
            val notes = findViewById<EditText>(R.id.edt_cttn).text.toString()

            val intent = Intent(this, bookingList::class.java).apply {
                putExtra("NAME", name)
                putExtra("DATE", date)
                putExtra("TIME", time)
                putExtra("PHONE", phone)
                putExtra("NOTES", notes)
            }
            startActivity(intent)
        }
    }
}