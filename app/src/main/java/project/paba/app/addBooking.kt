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

class addBooking : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_booking)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val _edt_nama: EditText = findViewById(R.id.edt_nama)
        val _edt_tanggal: EditText = findViewById(R.id.edt_tanggal)
        val _edt_jam: EditText = findViewById(R.id.edt_jam)
        val _edt_notelp: EditText = findViewById(R.id.edt_notelp)
        val _edt_cttn: EditText = findViewById(R.id.edt_cttn)

        _edt_tanggal.setOnClickListener {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            val datePickerDialog = DatePickerDialog(this, { _, selectedYear, selectedMonth, selectedDay ->
                _edt_tanggal.setText("$selectedDay/${selectedMonth + 1}/$selectedYear")
            }, year, month, day)
            datePickerDialog.show()
        }

        _edt_jam.setOnClickListener {
            val calendar = Calendar.getInstance()
            val hour = calendar.get(Calendar.HOUR_OF_DAY)
            val minute = calendar.get(Calendar.MINUTE)

            val timePickerDialog = TimePickerDialog(this, { _, selectedHour, selectedMinute ->
                _edt_jam.setText("$selectedHour:$selectedMinute")
            }, hour, minute, true)
            timePickerDialog.show()
        }

        val _btn_bookingNow: Button = findViewById(R.id.btn_bookingNow)
        _btn_bookingNow.setOnClickListener {
            val name = _edt_nama.text.toString()
            val date = _edt_tanggal.text.toString()
            val time = _edt_jam.text.toString()
            val phone = _edt_notelp.text.toString()
            val notes = _edt_cttn.text.toString()

            val resultIntent = Intent()
            resultIntent.putExtra("NAME", name)
            resultIntent.putExtra("DATE", date)
            resultIntent.putExtra("TIME", time)
            resultIntent.putExtra("PHONE", phone)
            resultIntent.putExtra("NOTES", notes)
            setResult(RESULT_OK, resultIntent)
            finish()

        }
    }
}