package project.paba.app

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class paketRestoran(
    val namaPaket: String = "",
    val deskripsi: String = "",
    val kapasitas: String = "",
    val harga: String = "",
    val uangDp: String = "",
    val idRestoran : String ="",
    val idPaket : String="",
    val foto : String=""
): Parcelable
