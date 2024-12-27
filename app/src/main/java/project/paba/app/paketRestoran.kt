package project.paba.app

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class paketRestoran(
    val idResto: String = "",
    val namaPaket: String = "",
    val deskripsi: String = "",
    val kapasitas: String = "",
    val foto : String = "",
    val harga: String = "",
    val uangDp: String = ""
): Parcelable
