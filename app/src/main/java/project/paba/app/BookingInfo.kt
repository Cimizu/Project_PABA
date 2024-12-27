import android.os.Parcel
import android.os.Parcelable

data class BookingInfo(
    val id: Int = 0,
    val resto: String = "",
    val name: String = "",
    val address: String = "",
    val date: String = "",
    val time: String = "",
    val phone: String = "",
    val notes: String = "",
    val status: String = "Active"
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
        parcel.readString() ?: "Active"
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeString(resto)
        parcel.writeString(name)
        parcel.writeString(address)
        parcel.writeString(date)
        parcel.writeString(time)
        parcel.writeString(phone)
        parcel.writeString(notes)
        parcel.writeString(status)
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