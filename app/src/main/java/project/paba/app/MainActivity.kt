package project.paba.app

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.frameContainer)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        auth = FirebaseAuth.getInstance()

        val _isiEmail = findViewById<EditText>(R.id.inputEmail)
        val _isiPassword = findViewById<EditText>(R.id.inputPassword)

        val _btnSignIn = findViewById<TextView>(R.id.inputSignIn)
        val _btnLogin = findViewById<Button>(R.id.btnStart)
        _btnLogin.setOnClickListener {
//            val intent = Intent(this, addBooking::class.java)
//            startActivity(intent)


            val email = _isiEmail.text.toString().trim()
            val password = _isiPassword.text.toString().trim()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                loginUser(email, password)
            } else {
                Toast.makeText(this, "Lengkapi semua data!", Toast.LENGTH_SHORT).show()
            }


        }

        _btnSignIn.setOnClickListener {
            val intent = Intent(this@MainActivity, register::class.java)
            startActivity(intent)
        }
    }

    private fun loginUser(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Login berhasil!", Toast.LENGTH_SHORT).show()
                    // Navigasi ke HomeActivity
                    startActivity(Intent(this, navbar::class.java))
                    finish()
                } else {
                    Toast.makeText(
                        this,
                        "Login gagal",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }
}