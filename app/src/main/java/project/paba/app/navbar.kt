package project.paba.app

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class navbar : AppCompatActivity() {
    private var activeButton: LinearLayout? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_navbar)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.frameContainer)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val btnhome = findViewById<LinearLayout>(R.id.btnhome)
        val btnhistory = findViewById<LinearLayout>(R.id.btnhistory)
        val btnprofile = findViewById<LinearLayout>(R.id.btnprofile)

        val mfFragmentManager = supportFragmentManager
        val mfResto = restaurants()

        mfFragmentManager.findFragmentByTag(restaurants::class.java.simpleName)
        mfFragmentManager.beginTransaction()
            .add(R.id.frameContainer, mfResto, restaurants::class.java.simpleName).commit()

        updateButtonWarna(btnhome)

        btnhome.setOnClickListener {
            val mFragmentManager1 = supportFragmentManager
            val mfSatu1 = restaurants()

            mFragmentManager1.findFragmentByTag(restaurants::class.java.simpleName)
            mFragmentManager1.beginTransaction().apply {
                replace(R.id.frameContainer, mfSatu1, restaurants::class.java.simpleName).commit()
            }
            updateButtonWarna(btnhome)
        }

        btnprofile.setOnClickListener {
            val mFragmentManager2 = supportFragmentManager
            val mfDua = profile()

            mFragmentManager2.findFragmentByTag(profile::class.java.simpleName)
            mFragmentManager2.beginTransaction().apply {
                replace(R.id.frameContainer, mfDua, profile::class.java.simpleName).commit()
            }
            updateButtonWarna(btnprofile)

        }

        btnhistory.setOnClickListener {
            val mFragmentManager3 = supportFragmentManager
            val mfTiga = history()

            mFragmentManager3.findFragmentByTag(history::class.java.simpleName)
            mFragmentManager3.beginTransaction().apply {
                replace(R.id.frameContainer, mfTiga, history::class.java.simpleName).commit()
            }
            updateButtonWarna(btnhistory)

        }
    }
    private fun updateButtonWarna(selectedButton: LinearLayout) {
        // Warna default dan aktif
        val defaultColor = resources.getColor(R.color.abu, null)
        val activeColor = resources.getColor(R.color.hijau2, null)

        // Reset warna tombol jadi defaultnya
        activeButton?.let { button ->
            val imageView = button.getChildAt(0) as ImageView
            val textView = button.getChildAt(1) as TextView
            imageView.setColorFilter(defaultColor)
            textView.setTextColor(defaultColor)
        }

        // Set warna untuk tombol yang ditekan terus nanti bakal keubah jadi hijau
        val selectedImageView = selectedButton.getChildAt(0) as ImageView
        val selectedTextView = selectedButton.getChildAt(1) as TextView
        selectedImageView.setColorFilter(activeColor)
        selectedTextView.setTextColor(activeColor)
        activeButton = selectedButton
    }

}