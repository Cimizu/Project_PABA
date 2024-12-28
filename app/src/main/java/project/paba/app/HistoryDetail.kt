package project.paba.app

import BookingInfo
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.squareup.picasso.Picasso

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [HistoryDetail.newInstance] factory method to
 * create an instance of this fragment.
 */
class HistoryDetail : Fragment() {

    private lateinit var _iv_gambarResto: ImageView
    private lateinit var _tv_resto: TextView
    private lateinit var _tv_alamatResto: TextView
    private lateinit var _tv_tanggal: TextView
    private lateinit var _tv_jam: TextView
    private lateinit var _tv_notes: TextView
    private lateinit var _tv_status: TextView
    private lateinit var _tv_kodeUnik: TextView
    private lateinit var _btnCheckIn: Button
    private lateinit var _btnDelete: Button

    private var booking: BookingInfo? = null
    private var restaurant: dataRestoran? = null

    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            booking = it.getParcelable("booking")
            restaurant = it.getParcelable("restaurant")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_history_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _iv_gambarResto = view.findViewById(R.id.iv_gambarResto)
        _tv_resto = view.findViewById(R.id.tv_resto)
        _tv_alamatResto = view.findViewById(R.id.tv_alamatResto)
        _tv_tanggal = view.findViewById(R.id.tv_tanggal)
        _tv_jam = view.findViewById(R.id.tv_jam)
        _tv_notes = view.findViewById(R.id.tv_notes)
        _tv_status = view.findViewById(R.id.tv_status)
//        _tv_kodeUnik = view.findViewById(R.id.tv_kodeUnik)
        _btnCheckIn = view.findViewById(R.id.btnCheckIn)
        _btnDelete = view.findViewById(R.id.btnDelete)

        // Load data into views
        restaurant?.let {
            Picasso.get().load(it.foto).into(_iv_gambarResto)
            _tv_resto.text = it.namaResto
            _tv_alamatResto.text = it.lokasi
        }

        booking?.let {
            _tv_tanggal.text = it.date
            _tv_jam.text = it.time
            _tv_status.text = it.status
            _tv_notes.text = it.notes
        }

        // Set up button actions
        _btnDelete.setOnClickListener {
            // Handle delete booking logic (if any)
        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment HistoryDetail.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(booking: BookingInfo, restaurant: dataRestoran): HistoryDetail {
            val fragment = HistoryDetail()
            val bundle = Bundle().apply {
                putParcelable("booking", booking)
                putParcelable("restaurant", restaurant)
            }
            fragment.arguments = bundle
            return fragment
        }
    }
}