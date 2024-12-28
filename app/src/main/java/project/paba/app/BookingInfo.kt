import android.os.Parcel
import android.os.Parcelable
import com.google.firebase.Timestamp

data class BookingInfo(
    val id: Int = 0,
    val resto: String = "",
    val paket: String = "",
    val name: String = "",
    val address: String = "",
    val date: String = "",
    val time: String = "",
    val phone: String = "",
    val notes: String = "",
    val status_bayar : Boolean = false,
    val status_aktif : Boolean = true,
    val userId: String = "",
    val uniqueCode: String = "",
    val timestamp: Long? = null
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readByte() != 0.toByte(),
        parcel.readByte() != 0.toByte(),
        parcel.readString() ?: "",
        parcel.readString() ?: ""
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeString(resto)
        parcel.writeString(paket)
        parcel.writeString(name)
        parcel.writeString(address)
        parcel.writeString(date)
        parcel.writeString(time)
        parcel.writeString(phone)
        parcel.writeString(notes)
        parcel.writeByte(if (status_bayar) 1 else 0)
        parcel.writeByte(if (status_aktif) 1 else 0)
        parcel.writeString(userId)
        parcel.writeString(uniqueCode)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<BookingInfo> {
        override fun createFromParcel(parcel: Parcel): BookingInfo {
            return BookingInfo(parcel)
        }

        override fun newArray(size: Int): Array<BookingInfo?> {
            return arrayOfNulls(size)
        }
    }
}