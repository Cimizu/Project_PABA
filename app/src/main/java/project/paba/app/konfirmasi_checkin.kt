package project.paba.app

import BookingInfo
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso
import org.w3c.dom.Text

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [konfirmasi_checkin.newInstance] factory method to
 * create an instance of this fragment.
 */
class konfirmasi_checkin : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private val db = FirebaseFirestore.getInstance()
    private var idRestoran: String? = null
    private lateinit var metode: TextView

    private lateinit var kotakPutih: ImageView
    private lateinit var pemesanName: TextView
    private lateinit var nomorPesenan: TextView
    private lateinit var namaResto: TextView
    private lateinit var alamatResto: TextView
    private lateinit var nomorTelfon: TextView
    private lateinit var jamTanggal: TextView
    private lateinit var reservasi_paket : TextView
    private lateinit var reservasi_detail :TextView
    private lateinit var reservasi_dp : TextView
    private lateinit var harga : TextView
    private lateinit var hargaDP : TextView
    private lateinit var uangsisa : TextView
    private lateinit var statusReservasi : TextView

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
        return inflater.inflate(R.layout.fragment_konfirmasi_checkin, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
         kotakPutih = view.findViewById(R.id.reservasi_image)
        pemesanName = view.findViewById(R.id.pemesan_name) //
        nomorPesenan = view.findViewById(R.id.nomorPesenan) //
        namaResto = view.findViewById(R.id.namaResto) //
        alamatResto = view.findViewById(R.id.alamatResto) //
        nomorTelfon = view.findViewById(R.id.nomorTelfon) //
        jamTanggal = view.findViewById(R.id.jamTanggal) //
        reservasi_paket = view.findViewById(R.id.reservasi_paket) //
        reservasi_detail = view.findViewById(R.id.reservasi_detail) //
        reservasi_dp = view.findViewById(R.id.reservasi_dp) //
        harga = view.findViewById(R.id.harga) //
        hargaDP = view.findViewById(R.id.hargaDP)
        uangsisa = view.findViewById(R.id.uangsisa)
        statusReservasi = view.findViewById(R.id.statusReservasi)
        metode = view.findViewById(R.id.metode)

        val dataIntent = arguments?.getParcelable<BookingInfo>("kirimData")
        dataIntent?.let {
            pemesanName.text=it.name
            namaResto.text =it.resto
            alamatResto.text=it.address
            harga.text=String.format("Rp %,03d", it.hargaTotal)
            hargaDP.text=String.format("Rp %,03d", it.hargaDP)
            val formatting = "${it.date} | ${it.time}"
            jamTanggal.text=formatting
            val formatting2= "Total Harga Reservasi \n(${it.jumlahOrang} Orang)"
            val formatting3= "Total DP Uang \n(${it.jumlahOrang} Orang)"
            reservasi_detail.text=formatting2
            reservasi_dp.text=formatting3
            reservasi_paket.text=it.paket
            idRestoran = it.idResto
            fetchRestoranData(idRestoran!!)
            fetchfoto(idRestoran!!, it.idPaket)
            uangsisa.text=String.format("Rp %,03d", it.hargaSisa)
            statusReservasi.text = it.statusString

            metode.text=it.metode_pembayaran

            val statusBayar = it.status_bayar

            if (statusBayar){
                nomorPesenan.text=it.uniqueCode
            }else{
                nomorPesenan.text="Belum melakukan pembayaran"

            }

        }

    }
    private fun fetchRestoranData(idRestoran: String) {
        db.collection("restoran").document(idRestoran)
            .get()
            .addOnSuccessListener { document ->
                if (document != null) {

                    val no_telfon = document.getString("noTelp") ?: "Nomor Tidak Ditemukan"
//                    val foto = document.getString("foto")?:""
                    nomorTelfon.text = no_telfon

                } else {
                    Log.e("AddBookingFragment", "Document tidak ditemukan untuk ID: $idRestoran")
                }
            }
            .addOnFailureListener { exception ->
                Log.e("AddBookingFragment", "Error mengambil data restoran: ${exception.message}", exception)
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
                            .placeholder(R.drawable.resto) // Placeholder image while loading
                            .error(R.drawable.resto) // Error image if loading fails
                            .into(kotakPutih) // Set the image into the ImageView
                    } else {
                        // Fallback if no photo URL is provided
                        kotakPutih.setImageResource(R.drawable.resto)
                    }


                } else {
                    Log.e("Konfirmasi check in", "Document tidak ditemukan untuk ID: $idRestoran")
                }
            }
            .addOnFailureListener { exception ->
                Log.e("Konfirmasi check in", "Error mengambil data restoran: ${exception.message}", exception)
            }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment konfirmasi_checkin.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            konfirmasi_checkin().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}