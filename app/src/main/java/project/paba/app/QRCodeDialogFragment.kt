package project.paba.app

import android.app.Dialog
import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.google.firebase.firestore.FirebaseFirestore
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter

class QRCodeDialogFragment : DialogFragment() {

    private lateinit var qrCodeImageView: ImageView
    private var uniqueCode: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        uniqueCode = arguments?.getString("uniqueCode")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_q_r_code_dialog, container, false)
        qrCodeImageView = view.findViewById(R.id.qrCodeImageView)
        checkCodeValidity(uniqueCode)
        return view
    }

    private fun checkCodeValidity(uniqueCode: String?) {
        if (uniqueCode.isNullOrEmpty()) return

        val db = FirebaseFirestore.getInstance()
        db.collection("bookings").whereEqualTo("uniqueCode", uniqueCode)
            .get()
            .addOnSuccessListener { documents ->
                if (documents.isEmpty) {
                    Toast.makeText(context, "Invalid QR Code", Toast.LENGTH_SHORT).show()
                    dismiss()
                    return@addOnSuccessListener
                }

                val document = documents.first()
                val timestamp = document.getLong("timestamp") ?: 0
                val currentTime = System.currentTimeMillis()
                val validityPeriod = 3 * 60 * 1000 // 3 minutes in milliseconds

                if (currentTime - timestamp <= validityPeriod) {
                    generateQRCode(uniqueCode)
                } else {
                    Toast.makeText(context, "QR Code expired", Toast.LENGTH_SHORT).show()
                    dismiss()
                }
            }
            .addOnFailureListener { e ->
                Log.e("QRCodeDialogFragment", "Error checking code validity", e)
                dismiss()
            }
    }

    private fun generateQRCode(text: String?) {
        if (text.isNullOrEmpty()) return
        val writer = QRCodeWriter()
        val bitMatrix = writer.encode(text, BarcodeFormat.QR_CODE, 512, 512)
        val width = bitMatrix.width
        val height = bitMatrix.height
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)
        for (x in 0 until width) {
            for (y in 0 until height) {
                bitmap.setPixel(x, y, if (bitMatrix.get(x, y)) -0x1000000 else -0x1)
            }
        }
        qrCodeImageView.setImageBitmap(bitmap)
    }

    companion object {
        fun newInstance(uniqueCode: String): QRCodeDialogFragment {
            val fragment = QRCodeDialogFragment()
            val args = Bundle()
            args.putString("uniqueCode", uniqueCode)
            fragment.arguments = args
            return fragment
        }
    }
}