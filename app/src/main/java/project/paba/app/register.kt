package project.paba.app

import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class register : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_register)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }



        val _btnResgiter = findViewById<Button>(R.id.btnRegister)
        val _btnBack = findViewById<ImageView>(R.id.btnBack)
        val _isiNama = findViewById<EditText>(R.id.inputNama)
        val _isiTelfon = findViewById<EditText>(R.id.inputTelfon)
        val _isiPassword1 = findViewById<EditText>(R.id.inputPass1)
        val _isiPassword2 = findViewById<EditText>(R.id.inputPass2)
        val _isiEmail = findViewById<EditText>(R.id.inputEmail)
        val _kembali = findViewById<TextView>(R.id.kembaliKelohgin)



        val auth = FirebaseAuth.getInstance()
        val db = FirebaseFirestore.getInstance()

        fun registerUser(
            auth: FirebaseAuth,
            db: FirebaseFirestore,
            user: Customer,
            onSuccess: () -> Unit,
            onFailure: (String) -> Unit
        ) {
            auth.createUserWithEmailAndPassword(user.email, user.password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val uid = auth.currentUser?.uid
                        if (uid != null) {
                            db.collection("Customer").document(uid)
                                .set(user)
                                .addOnSuccessListener { onSuccess() }
                                .addOnFailureListener { onFailure("Failed to save customer data") }
                        } else {
                            onFailure("Failed to retrieve user UID")
                        }
                    } else {
                        // Tangani error dari Firebase
                        val exception = task.exception
                        when {
                            exception?.message?.contains("email address is already in use") == true -> {
                                onFailure("Email sudah terdaftar. Gunakan email lain.")
                            }
                            else -> {
                                onFailure(exception?.message ?: "Registration failed")
                            }
                        }
                    }
                }
        }

        _kembali.setOnClickListener {
            val intent = Intent(this@register, MainActivity::class.java)
            startActivity(intent)
        }

        _btnBack.setOnClickListener {
            onBackPressed()
        }

        _btnResgiter.setOnClickListener {

            val nama = _isiNama.text.toString().trim()
            val nomorTelepon = _isiTelfon.text.toString().trim()
            val email = _isiEmail.text.toString().trim()
            val password1 = _isiPassword1.text.toString()
            val password2 = _isiPassword2.text.toString()

            // Validasi input
            var isValid = true

            if (nama.isEmpty()) {
                _isiNama.error = "Nama harus diisi"
                isValid = false
            }
            if (nomorTelepon.isEmpty()) {
                _isiTelfon.error = "Nomor telepon harus diisi"
                isValid = false
            }
            if (email.isEmpty()) {
                _isiEmail.error = "Email harus diisi"
                isValid = false
            }
            if (password1.isEmpty()) {
                _isiPassword1.error = "Password harus diisi"
                isValid = false
            }
            if (password2.isEmpty()) {
                _isiPassword2.error = "Konfirmasi password harus diisi"
                isValid = false
            }
            if (password1 != password2) {
                _isiPassword2.error = "Passwords tidak cocok"
                isValid = false
            }

            if (isValid) {
                val cust = Customer(
                    nama = nama,
                    nomorTelepon = nomorTelepon,
                    email = email,
                    password = password2
                )
                registerUser(auth, db, cust, {
                    Toast.makeText(this, "Registrasi Sukses!", Toast.LENGTH_SHORT).show()
                    // Pindah ke halaman login
                    val intent = Intent(this@register, MainActivity::class.java)
                    startActivity(intent)
                    finish() // Menutup halaman sign-up
                }, { error ->
                    Toast.makeText(this, "Error: $error", Toast.LENGTH_SHORT).show()
                })
            }
        }

    }
}