package project.paba.app

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore

class paket : Fragment() {

    private lateinit var paketAdapter: PaketAdapter
    private val db = FirebaseFirestore.getInstance()
    private val paketList = mutableListOf<paketRestoran>()
    private var restaurantId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            restaurantId = it.getString(ARG_PARAM1)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_paket, container, false)

        val recyclerView: RecyclerView = view.findViewById(R.id.rvPaket)
        recyclerView.layoutManager = LinearLayoutManager(context)

        paketAdapter = PaketAdapter(paketList)
        recyclerView.adapter = paketAdapter

        fetchPaket()

        return view
    }

    private fun fetchPaket() {
        restaurantId?.let { id ->
            db.collection("resto").document(id).collection("paket").get()
                .addOnSuccessListener { result ->
                    paketList.clear()
                    for (document in result) {
                        val paket = document.toObject(paketRestoran::class.java)
                        paketList.add(paket)
                    }
                    paketAdapter.notifyDataSetChanged()
                }
                .addOnFailureListener { e ->
                    // Handle error
                }
        }
    }

    companion object {
        private const val ARG_PARAM1 = "param1"

        @JvmStatic
        fun newInstance(restaurantId: String) =
            paket().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, restaurantId)
                }
            }
    }
}