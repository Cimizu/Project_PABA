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
import androidx.core.view.isVisible
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*

class AddBookingFragment : Fragment() {

    private val db = FirebaseFirestore.getInstance()
    private lateinit var edtNama: EditText
    private lateinit var edtTanggal: EditText
    private lateinit var edtJam: EditText
    private lateinit var edtNotelp: EditText
    private lateinit var edtCttn: EditText
    private lateinit var edtJumlahOrang: EditText
    private lateinit var tvHargaTotal: TextView
    private lateinit var tvHargaDP: TextView
    private lateinit var tvPaketName: TextView
    private lateinit var tvRestoName: TextView
    private lateinit var tvAddress: TextView
    private var bookingId: Int? = null
    private var jamBuka: String = "00:00"
    private var jamTutup: String = "23:59"
    private var maxJumlahOrang: Int = 0

    private var restoid: String = ""
    private var paketid: String = ""

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
        edtJumlahOrang = view.findViewById(R.id.edt_jumlahOrang)
        tvHargaTotal = view.findViewById(R.id.tv_hargaTotal)
        tvHargaDP = view.findViewById(R.id.tv_hargaDP)
        tvPaketName = view.findViewById(R.id.tv_paketAdd)
        tvRestoName = view.findViewById(R.id.tv_namaResto)
        tvAddress = view.findViewById(R.id.tv_alamatResto)
        val btnBookingNow: Button = view.findViewById(R.id.btn_bookingNow)
        val btnUpdateBooking: Button = view.findViewById(R.id.btn_updateBooking)

        // Ambil data dari arguments
        val paketName = arguments?.getString("paketName")
        val idRestoran = arguments?.getString("idRestoran")?:""
        val addEdit = arguments?.getInt("addEdit")
        val idPaket = arguments?.getString("idPaket")?:""

        bookingId = arguments?.getInt("bookingId")
        paketid = arguments?.getString("idPakett")?:""
        restoid = arguments?.getString("idRestoo")?:""


        Log.d("AddBookingFragment", "Paket: $paketName, idRestoran: $idRestoran, bookingId: $bookingId , idPaket: $idPaket")
        Log.d("AddBookingFragment", "Paket: $paketName, idRestoran: $restoid, bookingId: $bookingId , idPaket: $paketid")

        Log.d("AddBookingFragment", "Jumlah Orang: $maxJumlahOrang")



        // pengecekan idRestorannya ada atau engga
        if (!idRestoran.isNullOrEmpty()) {
            fetchRestoranData(idRestoran)

        } else {
            tvRestoName.text = "Nama Restoran Tidak Ditemukan"
            tvAddress.text = "Alamat Tidak Ditemukan"
        }

        if (addEdit == 0 ) {
            btnBookingNow.visibility = View.VISIBLE
            btnUpdateBooking.visibility = View.GONE
            // Kosongkan semua input jika `addEdit != 0`
            edtNama.text.clear()
            edtTanggal.text.clear()
            edtJam.text.clear()
            edtNotelp.text.clear()
            edtCttn.text.clear()
            edtJumlahOrang.text.clear()
            tvHargaTotal.text = "0"
            tvHargaDP.text = "0"
        } else {
            btnUpdateBooking.visibility = View.VISIBLE
            btnBookingNow.visibility = View.GONE
            fetchBookingData(bookingId!!)
        }


        // Date picker
        edtTanggal.setOnClickListener {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            val datePickerDialog = DatePickerDialog(requireContext(), { _, selectedYear, selectedMonth, selectedDay ->
                val selectedCalendar = Calendar.getInstance()
                selectedCalendar.set(selectedYear, selectedMonth, selectedDay)

                // Validasi H-1 hari
                if (rangeDate(selectedCalendar)) {
                    edtTanggal.setText("$selectedDay/${selectedMonth + 1}/$selectedYear")
                } else {
                    Toast.makeText(
                        requireContext(),
                        "Tanggal tidak boleh sebelum hari ini!",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }, year, month, day)

            datePickerDialog.show()
        }

        // Time picker
        edtJam.setOnClickListener {
            val calendar = Calendar.getInstance()
            val hour = calendar.get(Calendar.HOUR_OF_DAY)
            val minute = calendar.get(Calendar.MINUTE)

            val selectedDate = edtTanggal.text.toString() // Ambil tanggal yang dipilih

            val timePickerDialog = TimePickerDialog(requireContext(), { _, selectedHour, selectedMinute ->
                val selectedTime = String.format("%02d:%02d", selectedHour, selectedMinute)

                if (rangeWaktu(selectedTime, selectedDate)) {
                    edtJam.setText(selectedTime)
                } else {
                    Toast.makeText(
                        requireContext(),
                        "Jam yang dipilih harus antara $jamBuka dan $jamTutup dan bukan jam saat ini - 1 jam",
                        Toast.LENGTH_SHORT
                    ).show()
                }
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
            val newJumlahOrang = edtJumlahOrang.text.toString().toIntOrNull() ?: 0
            val newHargaTotal = tvHargaTotal.text.toString().toIntOrNull() ?: 0
            val newHargaDP = tvHargaDP.text.toString().toIntOrNull() ?: 0
            val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
            val uniqueCode = generateUniqueCode()

            // jika salah
            if (newJumlahOrang > maxJumlahOrang) {
                Toast.makeText(
                    requireContext(),
                    "Jumlah orang yang diinputkan tidak boleh lebih dari $maxJumlahOrang.",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }


            if (newName.isNotEmpty() && newDate.isNotEmpty() && newTime.isNotEmpty() && newPhone.isNotEmpty() && newNotes.isNotEmpty()) {

                // Create new booking
                getNextId { nextId ->
                    if (nextId != -1) {
                        val bookingInfo = BookingInfo(
                            nextId,
                            tvRestoName.text.toString(),
                            paketName?:"",
                            newName,
                            tvAddress.text.toString(),
                            newDate,
                            newTime,
                            newPhone,
                            newNotes,
                            false,
                            true,
                            userId,
                            uniqueCode,
                            hargaTotal = newHargaTotal,
                            hargaDP = newHargaDP,
                            jumlahOrang = newJumlahOrang,
                            idResto = idRestoran,
                            idPaket = idPaket
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

        // Tombol update
        btnUpdateBooking.setOnClickListener {
            val newName = edtNama.text.toString()
            val newDate = edtTanggal.text.toString()
            val newTime = edtJam.text.toString()
            val newPhone = edtNotelp.text.toString()
            val newNotes = edtCttn.text.toString()
            val newJumlahOrang = edtJumlahOrang.text.toString().toIntOrNull() ?: 0
            val newHargaTotal = tvHargaTotal.text.toString().toIntOrNull() ?: 0
            val newHargaDP = tvHargaDP.text.toString().toIntOrNull() ?: 0
            val newHargaSisa = newHargaTotal - newHargaDP
            val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
            val uniqueCode = generateUniqueCode()

            if (newJumlahOrang > maxJumlahOrang) {
                Toast.makeText(
                    requireContext(),
                    "Jumlah orang yang diinputkan tidak boleh lebih dari $maxJumlahOrang.",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }


            if (newName.isNotEmpty() && newDate.isNotEmpty() && newTime.isNotEmpty() && newPhone.isNotEmpty() && newNotes.isNotEmpty()) {
                if (bookingId != null && restoid != "" && paketid !="") {
                    // Update existing booking
                    val bookingInfo = BookingInfo(
                        bookingId!!,
                        tvRestoName.text.toString(),
                        paketName?:"",
                        newName,
                        tvAddress.text.toString(),
                        newDate,
                        newTime,
                        newPhone,
                        newNotes,
                        false,
                        true,
                        userId,
                        uniqueCode,
                        hargaTotal = newHargaTotal,
                        hargaDP = newHargaDP,
                        hargaSisa = newHargaSisa,
                        jumlahOrang = newJumlahOrang,
                        idResto = restoid,
                        idPaket = paketid
                    )
                    db.collection("bookings").document(bookingId.toString())
                        .set(bookingInfo)
                        .addOnSuccessListener {
                            navigateToBookingList()
                        }
                        .addOnFailureListener { e ->
                            Log.e("Firebase", "Error updating document", e)
                        }
                } else {
                    Toast.makeText(requireContext(), "Booking ID tidak ditemukan!", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(requireContext(), "Semua field harus diisi!", Toast.LENGTH_SHORT).show()
            }
        }

        return view
    }

    private fun fetchPaketData(idRestoran: String, idPaket: String) {
        db.collection("restoran").document(idRestoran).collection("paket").document(idPaket)
            .get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    maxJumlahOrang = document.getString("kapasitas")?.toInt() ?: 0
                    Log.d("AddBookingFragment", "Max Jumlah Orang: $maxJumlahOrang")
                } else {
                    Log.e("AddBookingFragment", "Paket tidak ditemukan untuk ID: $idPaket")
                }
            }
            .addOnFailureListener { exception ->
                Log.e("AddBookingFragment", "Error mengambil data paket: ${exception.message}", exception)
            }
    }

    private fun fetchRestoranData(idRestoran: String) {
        db.collection("restoran").document(idRestoran)
            .get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    val restoName = document.getString("namaResto") ?: "Nama Restoran Tidak Ditemukan"
                    val address = document.getString("lokasi") ?: "Alamat Tidak Ditemukan"
                    jamBuka = document.getString("jambuka") ?: "00:00"
                    jamTutup = document.getString("jamtutup") ?: "23:59"
                    val paketName = arguments?.getString("paketName")
                    val idPaket = arguments?.getString("idPaket")

                    tvRestoName.text = restoName
                    tvAddress.text = address
                    tvPaketName.text = paketName

                    if (!idPaket.isNullOrEmpty()) {
                        fetchPaketData(idRestoran, idPaket)
                    }

                } else {
                    Log.e("AddBookingFragment", "Document tidak ditemukan untuk ID: $idRestoran")
                }
            }
            .addOnFailureListener { exception ->
                Log.e("AddBookingFragment", "Error mengambil data restoran: ${exception.message}", exception)
            }
    }

    private fun fetchBookingData(bookingId: Int) {
        db.collection("bookings").document(bookingId.toString())
            .get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    val oldResto = document.getString("resto")
                    val oldPaket = document.getString("paket")
                    val oldAddress = document.getString("address")
                    val oldName = document.getString("name")
                    val oldDate = document.getString("date")
                    val oldTime = document.getString("time")
                    val oldPhone = document.getString("phone")
                    val oldNotes = document.getString("notes")
                    val oldHargaTotal = document.getLong("hargaTotal")?.toInt() ?: 0
                    val oldHargaDP = document.getLong("hargaDP")?.toInt() ?: 0
                    val oldHargaSisa = document.getLong("hargaSisa")?.toInt() ?: 0
                    val oldJumlahOrang = document.getLong("jumlahOrang")?.toInt() ?: 0

                    tvRestoName.text = oldResto
                    tvPaketName.text = oldPaket
                    tvAddress.text = oldAddress
                    edtNama.setText(oldName)
                    edtTanggal.setText(oldDate)
                    edtJam.setText(oldTime)
                    edtNotelp.setText(oldPhone)
                    edtCttn.setText(oldNotes)
                    tvHargaTotal.text = oldHargaTotal.toString()
                    tvHargaDP.text = oldHargaDP.toString()
                    edtJumlahOrang.setText(oldJumlahOrang.toString())


                    if (!paketid.isNullOrEmpty()) {
                        fetchPaketData(restoid, paketid)
                    }

                } else {
                    Log.e("AddBookingFragment", "Document tidak ditemukan untuk ID: $bookingId")
                }
            }
            .addOnFailureListener { exception ->
                Log.e("AddBookingFragment", "Error mengambil data booking: ${exception.message}", exception)
            }
    }

    private fun navigateToBookingList() {
        parentFragmentManager.beginTransaction()
            .replace(R.id.frameContainer, BookingListFragment())
            .addToBackStack(null)
            .commit()
    }

    private fun rangeWaktu(selectedTime: String, selectedDate: String): Boolean {
        val formatter = java.text.SimpleDateFormat("HH:mm", Locale.getDefault())
        val dateFormatter = java.text.SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        return try {
            val selected = formatter.parse(selectedTime)!!
            val buka = formatter.parse(jamBuka)!!
            val tutup = formatter.parse(jamTutup)!!

            // Waktu saat ini
            val now = Calendar.getInstance()
            val currentTime = formatter.parse(formatter.format(now.time))!!
            val oneHourLater = Calendar.getInstance().apply {
                add(Calendar.HOUR_OF_DAY, 1)
            }.time
            val oneHourLaterTime = formatter.parse(formatter.format(oneHourLater))!!

            // Periksa tanggal yang dipilih
            val selectedDateParsed = dateFormatter.parse(selectedDate)
            val todayDate = dateFormatter.parse(dateFormatter.format(now.time))

            if (selectedDateParsed != null && selectedDateParsed.compareTo(todayDate) == 0) {
                // Jika tanggal adalah hari ini, waktu yang dipilih harus lebih besar dari waktu saat ini + 1 jam
                return selected >= buka && selected <= tutup && selected >= oneHourLaterTime
            } else {
                // Jika tanggal bukan hari ini, cukup periksa rentang jam buka dan tutup
                return selected >= buka && selected <= tutup
            }
        } catch (e: Exception) {
            Log.e("AddBookingFragment", "Error parsing time: ${e.message}", e)
            false
        }
    }



    private fun rangeDate(selectedCalendar: Calendar): Boolean {
        // Kalender hari ini
        val todayCalendar = Calendar.getInstance()
        // set 00:00:00
        todayCalendar.set(Calendar.HOUR_OF_DAY, 0)
        todayCalendar.set(Calendar.MINUTE, 0)
        todayCalendar.set(Calendar.SECOND, 0)
        todayCalendar.set(Calendar.MILLISECOND, 0)
        return selectedCalendar.timeInMillis >= todayCalendar.timeInMillis
    }




    private fun getNextId(onComplete: (Int) -> Unit) {
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

    private fun generateUniqueCode(): String {
        return "BOOK-" + UUID.randomUUID().toString()
    }
}