package project.paba.app

import BookingInfo
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso
import org.w3c.dom.Text

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [payment.newInstance] factory method to
 * create an instance of this fragment.
 */
class payment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private val db = FirebaseFirestore.getInstance()
    private var idRestoran: String? = null
    private lateinit var pemesananTelefon: TextView
    private lateinit var sisaPembayaran: TextView
    private lateinit var gambar: ImageView


        override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_payment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Akses komponen dari layout menggunakan ID
        val paymentMethodSpinner: Spinner = view.findViewById(R.id.payment_methods_spinner)
        ArrayAdapter.createFromResource(
            requireContext(),
            R.array.payment_methods,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            paymentMethodSpinner.adapter = adapter
        }
        val title: TextView = view.findViewById(R.id.title)
        val infoPemesanLabel: TextView = view.findViewById(R.id.info_pemesan_label)
        val pemesanName: TextView = view.findViewById(R.id.pemesan_name)
        val pesananNumber: TextView = view.findViewById(R.id.nomorPesenan)
        val pemesanRestoran: TextView = view.findViewById(R.id.namaResto)
        val pemesanAlamat: TextView = view.findViewById(R.id.alamatResto)
        pemesananTelefon = view.findViewById(R.id.nomorTelfon)
        val pemesanWaktu: TextView = view.findViewById(R.id.jamTanggal)
        val reservasiDetail: TextView = view.findViewById(R.id.reservasi_detail)
        val reservasi_dp : TextView = view.findViewById(R.id.reservasi_dp)
        val harga: TextView = view.findViewById(R.id.harga)
        val hargaDP: TextView = view.findViewById(R.id.hargaDP)
        val reseverasi_paket : TextView = view.findViewById(R.id.reservasi_paket)
        val paymentLabel: TextView = view.findViewById(R.id.payment_label)
        val paymentDpButton: Button = view.findViewById(R.id.payment_dp)
        val paymentFullButton: Button = view.findViewById(R.id.payment_full)
        val payment_sisa: Button = view.findViewById(R.id.payment_sisa)
        gambar = view.findViewById(R.id.reservasi_image)


        sisaPembayaran = view.findViewById(R.id.sisaPembayaran)

        val dataIntent = arguments?.getParcelable<BookingInfo>("kirimData")
        dataIntent?.let {
            pemesanName.text=it.name
            pemesanRestoran.text =it.resto
            pemesanAlamat.text=it.address
            harga.text=String.format("Rp %,03d", it.hargaTotal)
            hargaDP.text=String.format("Rp %,03d", it.hargaDP)
            val formatting = "${it.date} | ${it.time}"
            pemesanWaktu.text=formatting
            val formatting2= "Total Harga Reservasi \n(${it.jumlahOrang} Orang)"
            val formatting3= "Total DP Uang \n(${it.jumlahOrang} Orang)"
            reservasiDetail.text=formatting2
            reservasi_dp.text=formatting3
            reseverasi_paket.text=it.paket
            idRestoran = it.idResto
            fetchRestoranData(idRestoran!!)
            fetchfoto(idRestoran!!,it.idPaket)
            sisaPembayaran.text=String.format("Rp %,03d", it.hargaSisa)
//            if (it.foto.isNotEmpty()) {
//                Picasso.get()
//                    .load(it.foto) // Load the image from the URL
//                    .placeholder(R.drawable.restoran) // Placeholder image while loading
//                    .error(R.drawable.restoran) // Error image if loading fails
//                    .into(_restoranImage) // Set the image into the ImageView
//            } else {
//                // Fallback if no photo URL is provided
//                _restoranImage.setImageResource(R.drawable.restoran)
//            }

            val statusBayar = it.status_bayar

            if (statusBayar){
                pesananNumber.text=it.uniqueCode
            }else{
                pesananNumber.text="Belum melakukan pembayaran"

            }

        }

        payment_sisa.visibility = View.GONE  // Mulai dengan tombol pembayaran sisa disembunyikan
        paymentDpButton.visibility = View.VISIBLE  // Tombol DP dan Full yang terlihat pada awalnya
        paymentFullButton.visibility = View.VISIBLE
        dataIntent?.let { bookingInfo ->
            // Cek status_bayar
            val statusBayar = bookingInfo.status_bayar

            if (statusBayar) {
                // Jika status_bayar true, tampilkan tombol pembayaran sisa
                payment_sisa.visibility = View.VISIBLE
                paymentDpButton.visibility = View.GONE  // Sembunyikan tombol DP
                paymentFullButton.visibility = View.GONE // Sembunyikan tombol Full
            } else {
                // Jika status_bayar false, tombol pembayaran DP dan Full tetap terlihat
                payment_sisa.visibility = View.GONE
                paymentDpButton.visibility = View.VISIBLE
                paymentFullButton.visibility = View.VISIBLE
            }
        }


        payment_sisa.setOnClickListener {
            dataIntent?.let { bookingInfo ->
                val selectedMethod = getSelectedPaymentMethod(paymentMethodSpinner)
                val sisaPembayaran = 0
                val updatedData: MutableMap<String, Any> = hashMapOf(
                    "statusDP" to true,
                    "statusSisa" to true,
                    "status_bayar" to true,
                    "hargaSisa" to sisaPembayaran,
                    "metode_pembayaran" to selectedMethod

                )

                // Perbarui data di Firestore
                db.collection("bookings").document(bookingInfo.id.toString())
                    .update(updatedData)
                    .addOnSuccessListener {
                        Log.d("PaymentFragment", "Status pembayaran berhasil diperbarui.")
                        this@payment.sisaPembayaran.text = String.format("Rp %,03d", sisaPembayaran)
                        Toast.makeText(requireContext(), "Pembayaran Sisa berhasil!", Toast.LENGTH_SHORT).show()
                        parentFragmentManager.popBackStack() // Untuk kembali ke fragment sebelumnya
                    }
                    .addOnFailureListener { exception ->
                        Log.e("PaymentFragment", "Gagal memperbarui status pembayaran: ${exception.message}")
                        Toast.makeText(requireContext(), "Gagal memperbarui pembayaran sisa.", Toast.LENGTH_SHORT).show()
                    }
            }
        }


        // Tambahkan fungsi pada tombol pembayaran
        paymentDpButton.setOnClickListener {
            // Cek jika data ada
            val selectedMethod = getSelectedPaymentMethod(paymentMethodSpinner)
            dataIntent?.let { bookingInfo ->
                val sisaPembayaran = bookingInfo.hargaTotal - bookingInfo.hargaDP
                val updatedData: MutableMap<String, Any> = hashMapOf(
                    "statusDP" to true,
                    "status_bayar" to true,
                    "hargaSisa" to sisaPembayaran,
                    "metode_pembayaran" to selectedMethod

                )


                // Perbarui data di Firestore
                db.collection("bookings").document(bookingInfo.id.toString())
                    .update(updatedData)
                    .addOnSuccessListener {
                        Log.d("PaymentFragment", "Status pembayaran berhasil diperbarui.")
                        this@payment.sisaPembayaran.text = String.format("Rp %,03d", sisaPembayaran)
                        Toast.makeText(requireContext(), "Pembayaran DP berhasil!", Toast.LENGTH_SHORT).show()

                        if (sisaPembayaran == 0) {
                            Log.d("PaymentFragment", "Pembayaran selesai.")
                        }

                        // Navigasi ke fragment booking list
                        parentFragmentManager.popBackStack() // Untuk kembali ke fragment sebelumnya
                    }
                    .addOnFailureListener { exception ->
                        Log.e("PaymentFragment", "Gagal memperbarui status pembayaran: ${exception.message}")
                        Toast.makeText(requireContext(), "Gagal memperbarui pembayaran DP.", Toast.LENGTH_SHORT).show()
                    }
            }
        }


        paymentFullButton.setOnClickListener {
            dataIntent?.let { bookingInfo ->
                val sisaPembayaran = 0
                val selectedMethod = getSelectedPaymentMethod(paymentMethodSpinner)
                val updatedData: MutableMap<String, Any> = hashMapOf(
                    "statusDP" to true,
                    "statusSisa" to true,
                    "status_bayar" to true,
                    "hargaSisa" to sisaPembayaran,
                    "metode_pembayaran" to selectedMethod

                )


                // Perbarui data di Firestore
                db.collection("bookings").document(bookingInfo.id.toString())
                    .update(updatedData)
                    .addOnSuccessListener {
                        Log.d("PaymentFragment", "Status pembayaran berhasil diperbarui.")
                        this@payment.sisaPembayaran.text = String.format("Rp %,03d", sisaPembayaran)
                        Toast.makeText(requireContext(), "Pembayaran Full berhasil!", Toast.LENGTH_SHORT).show()
                        parentFragmentManager.popBackStack() // Untuk kembali ke fragment sebelumnya
                    }
                    .addOnFailureListener { exception ->
                        Log.e("PaymentFragment", "Gagal memperbarui status pembayaran: ${exception.message}")
                        Toast.makeText(requireContext(), "Gagal memperbarui pembayaran Full.", Toast.LENGTH_SHORT).show()
                    }
            }
        }
    }

    private fun getSelectedPaymentMethod(paymentMethodSpinner: Spinner): String {
        return paymentMethodSpinner.selectedItem.toString()
    }




    private fun fetchRestoranData(idRestoran: String) {
        db.collection("restoran").document(idRestoran)
            .get()
            .addOnSuccessListener { document ->
                if (document != null) {

                    val no_telfon = document.getString("noTelp") ?: "Nomor Tidak Ditemukan"
                    pemesananTelefon.text = no_telfon.toString()
//                    val fotoo = document.getString("foto") ?:""
//                    if (fotoo.isNotEmpty()) {
//                        Picasso.get()
//                            .load(fotoo) // Load the image from the URL
//                            .placeholder(R.drawable.restoran) // Placeholder image while loading
//                            .error(R.drawable.restoran) // Error image if loading fails
//                            .into(gambar) // Set the image into the ImageView
//                    } else {
//                        // Fallback if no photo URL is provided
//                        gambar.setImageResource(R.drawable.restoran)
//                    }


                } else {
                    Log.e("Payment", "Document tidak ditemukan untuk ID: $idRestoran")
                }
            }
            .addOnFailureListener { exception ->
                Log.e("Payment", "Error mengambil data restoran: ${exception.message}", exception)
            }
    }

    private fun fetchfoto(idRestoran: String, idPaket: String) {
        db.collection("restoran").document(idRestoran).collection("paket").document(idPaket)
            .get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    val fotoo = document.getString("foto") ?:""
                    if (fotoo.isNotEmpty()) {
                        Picasso.get()
                            .load(fotoo) // Load the image from the URL
                            .placeholder(R.drawable.restoran) // Placeholder image while loading
                            .error(R.drawable.restoran) // Error image if loading fails
                            .into(gambar) // Set the image into the ImageView
                    } else {
                        // Fallback if no photo URL is provided
                        gambar.setImageResource(R.drawable.restoran)
                    }


                } else {
                    Log.e("payment", "Document tidak ditemukan untuk ID: $idRestoran")
                }
            }
            .addOnFailureListener { exception ->
                Log.e("payment", "Error mengambil data paket: ${exception.message}", exception)
            }
    }



    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment payment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            payment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}