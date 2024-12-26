package project.paba.app

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class navbar : AppCompatActivity() {
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

        btnhome.setOnClickListener {
            val mFragmentManager1 = supportFragmentManager
            val mfSatu1 = restaurants()

            mFragmentManager1.findFragmentByTag(restaurants::class.java.simpleName)
            mFragmentManager1.beginTransaction().apply {
                replace(R.id.frameContainer, mfSatu1, restaurants::class.java.simpleName).commit()
            }
        }

        btnprofile.setOnClickListener {
            val mFragmentManager2 = supportFragmentManager
            val mfDua = profile()

            mFragmentManager2.findFragmentByTag(profile::class.java.simpleName)
            mFragmentManager2.beginTransaction().apply {
                replace(R.id.frameContainer, mfDua, profile::class.java.simpleName).commit()
            }
        }

        btnhistory.setOnClickListener {
            val mFragmentManager3 = supportFragmentManager
            val mfTiga = history()

            mFragmentManager3.findFragmentByTag(history::class.java.simpleName)
            mFragmentManager3.beginTransaction().apply {
                replace(R.id.frameContainer, mfTiga, history::class.java.simpleName).commit()
            }

        }
    }
}