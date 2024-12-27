package project.paba.app

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class detrestaurant : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_det_restaurants)

        val _restoranImage = findViewById<ImageView>(R.id.restaurantImage)
        val _namaResto = findViewById<TextView>(R.id.namaResto)
        val _namaResto2 = findViewById<TextView>(R.id.namaResto2)
        val _jambuka = findViewById<TextView>(R.id.jambuka)
        val _jamtutup = findViewById<TextView>(R.id.jamtutup)
        val _lokasi = findViewById<TextView>(R.id.lokasi)
        val _noTelp = findViewById<TextView>(R.id.noTelp)
        val _deskripsi = findViewById<TextView>(R.id.deskripsi)
        val _btnreservasi = findViewById<Button>(R.id.btnreservasi)
        val _btnLihatpaket = findViewById<Button>(R.id.btnLihatpaket)

        val dataIntent = intent.getParcelableExtra<dataRestoran>("kirimData")
        dataIntent?.let {
            _namaResto.text = it.namaResto
            _namaResto2.text = it.namaResto2
            _jambuka.text = it.jambuka
            _jamtutup.text = it.jamtutup
            _lokasi.text = it.lokasi
            _noTelp.text = it.noTelp
            _deskripsi.text = it.deskripsi
        }

        // Tombol untuk pindah ke Booking
        _btnreservasi.setOnClickListener {
            val intent = Intent(this, addBooking::class.java)
            intent.putExtra("namaResto", dataIntent?.namaResto)
            startActivity(intent)
        }

        // Tombol untuk pindah ke Paket (Fragment)
        _btnLihatpaket.setOnClickListener {
            val restoranId = dataIntent?.namaResto // Atau gunakan ID restoran jika ada

            // Membuat Fragment baru
            val paketFragment = paket().apply {
                arguments = Bundle().apply {
                    putString("restoranId", restoranId) // Kirimkan ID restoran ke Fragment
                }
            }

            // Check if the fragment is already added
            val existingFragment = supportFragmentManager.findFragmentByTag(paket::class.java.simpleName)
            if (existingFragment == null) {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.frameContainer, paketFragment, paket::class.java.simpleName) // Ganti dengan ID container yang sesuai
                    .addToBackStack(null) // Agar bisa kembali ke fragment sebelumnya
                    .commit() // Commit the transaction
            }
        }
    }
}
