package project.paba.app

import BookingInfo
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*

class AddBookingFragment : Fragment() {

    private val db = FirebaseFirestore.getInstance()
    private lateinit var edtNama: EditText
    private lateinit var edtTanggal: EditText
    private lateinit var edtJam: EditText
    private lateinit var edtNotelp: EditText
    private lateinit var edtCttn: EditText
    private lateinit var tvPaketName: TextView
    private lateinit var tvRestoName: TextView
    private lateinit var tvAddress: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_add_booking, container, false)

        edtNama = view.findViewById(R.id.edt_nama)
        edtTanggal = view.findViewById(R.id.edt_tanggal)
        edtJam = view.findViewById(R.id.edt_jam)
        edtNotelp = view.findViewById(R.id.edt_notelp)
        edtCttn = view.findViewById(R.id.edt_cttn)
        tvPaketName = view.findViewById(R.id.tv_paketAdd)
        tvRestoName = view.findViewById(R.id.tv_namaResto)
        tvAddress = view.findViewById(R.id.tv_alamatResto)
        val btnBookingNow: Button = view.findViewById(R.id.btn_bookingNow)

        // Ambil data dari arguments
        val paketName = arguments?.getString("paketName")
        val idRestoran = arguments?.getString("idRestoran")
        val name = arguments?.getString("name")
        val date = arguments?.getString("date")
        val time = arguments?.getString("time")
        val phone = arguments?.getString("phone")
        val notes = arguments?.getString("notes")
        val bookingId = arguments?.getString("bookingId")

        Log.d("AddBookingFragment", "Paket: $paketName, idRestoran: $idRestoran")

        // Set nilai ke TextView
        tvPaketName.text = paketName ?: "Nama Paket Tidak Ditemukan"

        if (!idRestoran.isNullOrEmpty()) {
            fetchRestoranData(idRestoran)
        } else {
            tvRestoName.text = "Nama Restoran Tidak Ditemukan"
            tvAddress.text = "Alamat Tidak Ditemukan"
        }

        // Jika data sudah ada (update booking)
        if (bookingId != null) {
            edtNama.setText(name)
            edtTanggal.setText(date)
            edtJam.setText(time)
            edtNotelp.setText(phone)
            edtCttn.setText(notes)
        }

        // Date picker
        edtTanggal.setOnClickListener {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            val datePickerDialog = DatePickerDialog(requireContext(), { _, selectedYear, selectedMonth, selectedDay ->
                edtTanggal.setText("$selectedDay/${selectedMonth + 1}/$selectedYear")
            }, year, month, day)
            datePickerDialog.show()
        }

        // Time picker
        edtJam.setOnClickListener {
            val calendar = Calendar.getInstance()
            val hour = calendar.get(Calendar.HOUR_OF_DAY)
            val minute = calendar.get(Calendar.MINUTE)

            val timePickerDialog = TimePickerDialog(requireContext(), { _, selectedHour, selectedMinute ->
                edtJam.setText(String.format("%02d:%02d", selectedHour, selectedMinute))
            }, hour, minute, true)
            timePickerDialog.show()
        }

        // Tombol booking
        btnBookingNow.setOnClickListener {
            val newName = edtNama.text.toString()
            val newDate = edtTanggal.text.toString()
            val newTime = edtJam.text.toString()
            val newPhone = edtNotelp.text.toString()
            val newNotes = edtCttn.text.toString()

            if (newName.isNotEmpty() && newDate.isNotEmpty() && newTime.isNotEmpty() && newPhone.isNotEmpty() && newNotes.isNotEmpty()) {
                getNextId { nextId ->
                    if (nextId != -1) {
                        val bookingInfo = BookingInfo(
                            nextId,
                            tvRestoName.text.toString(),
                            paketName ?: "",
                            newName,
                            tvAddress.text.toString(),
                            newDate,
                            newTime,
                            newPhone,
                            newNotes,
                            false,
                            true
                        )
                        db.collection("bookings").document(nextId.toString()).set(bookingInfo)
                            .addOnSuccessListener {
                                navigateToBookingList()
                            }
                            .addOnFailureListener { e ->
                                Log.e("Firebase", "Error adding document", e)
                            }
                    } else {
                        Toast.makeText(requireContext(), "Error generating booking ID", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                Toast.makeText(requireContext(), "Semua field harus diisi!", Toast.LENGTH_SHORT).show()
            }
        }

        return view
    }

    private fun fetchRestoranData(idRestoran: String) {
        db.collection("restoran").document(idRestoran)
            .get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    val restoName = document.getString("namaResto") ?: "Nama Restoran Tidak Ditemukan"
                    val address = document.getString("lokasi") ?: "Alamat Tidak Ditemukan"

                    tvRestoName.text = restoName
                    tvAddress.text = address
                } else {
                    Log.e("AddBookingFragment", "Document tidak ditemukan untuk ID: $idRestoran")
                }
            }
            .addOnFailureListener { exception ->
                Log.e("AddBookingFragment", "Error mengambil data restoran: ${exception.message}", exception)
            }
    }

    private fun navigateToBookingList() {
        parentFragmentManager.beginTransaction()
            .replace(R.id.frameContainer, BookingListFragment())
            .addToBackStack(null)
            .commit()
    }

    private fun getNextId(onComplete: (Int) -> Unit) {
        val db = FirebaseFirestore.getInstance()
        val idDocRef = db.collection("metadata").document("bookingId")

        db.runTransaction { transaction ->
            val snapshot = transaction.get(idDocRef)
            val currentId = snapshot.getLong("lastId") ?: 0
            val nextId = currentId + 1
            transaction.update(idDocRef, "lastId", nextId)
            nextId
        }.addOnSuccessListener { nextId ->
            onComplete(nextId.toInt())
        }.addOnFailureListener { e ->
            Log.e("AddBookingFragment", "Error getting next id", e)
            onComplete(-1)
        }
    }
}