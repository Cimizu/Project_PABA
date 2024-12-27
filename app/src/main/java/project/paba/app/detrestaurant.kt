package project.paba.app

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class detrestaurant : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_det_restaurants)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.detrestoran)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val _restoranImage = findViewById<ImageView>(R.id.restaurantImage)
        val _namaResto = findViewById<TextView>(R.id.namaResto)
        val _namaResto2 = findViewById<TextView>(R.id.namaResto2)
        val _jambuka = findViewById<TextView>(R.id.jambuka)
        val _jamtutup = findViewById<TextView>(R.id.jamtutup)
        val _lokasi = findViewById<TextView>(R.id.lokasi)
        val _noTelp = findViewById<TextView>(R.id.noTelp)
        val _deskripsi = findViewById<TextView>(R.id.deskripsi)
        val _btnreservasi = findViewById<Button>(R.id.btnreservasi)
        val _btnLihatpaket= findViewById<Button>(R.id.btnLihatpaket)

        val dataIntent = intent.getParcelableExtra("kirimData",dataRestoran::class.java)
        if (dataIntent != null){
//            Picasso.get().load(dataIntent.foto).into(_restoranImage)
            _namaResto.text = dataIntent.namaResto
            _namaResto2.text = dataIntent.namaResto2
            _jambuka.text = dataIntent.jambuka
            _jamtutup.text = dataIntent.jamtutup
            _lokasi.text = dataIntent.lokasi
            _noTelp.text = dataIntent.noTelp
            _deskripsi.text = dataIntent.deskripsi
        }

        // Mengatur listener untuk tombol btnreservasi
        _btnreservasi.setOnClickListener {
            // Pindah ke Activity Booking
            val intent = Intent(this, addBooking::class.java)
            intent.putExtra("namaResto", dataIntent?.namaResto)
            intent.putExtra("imageResto", dataIntent?.foto)
            intent.putExtra("alamatResto", dataIntent?.lokasi)
            startActivity(intent)
        }

        // Mengatur listener untuk tombol btnLihatpaket
        _btnLihatpaket.setOnClickListener {
            // Pindah ke Activity Paket Recycle
            val intent = Intent(this, paket::class.java)
            startActivity(intent)
        }
    }
}