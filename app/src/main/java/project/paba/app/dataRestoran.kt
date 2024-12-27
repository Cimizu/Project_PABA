package project.paba.app

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class dataRestoran(
    val namaResto : String = "",
    val namaResto2: String = "",
    val deskripsi : String = "",
    val lokasi : String = "",
    val foto : String = "",
    val noTelp : String = "",
    val jambuka :String = "",
    val jamtutup : String = ""
): Parcelable

