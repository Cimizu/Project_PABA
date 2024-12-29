import android.os.Parcel
import android.os.Parcelable
import com.google.firebase.Timestamp

data class BookingInfo(
    val id: Int = 0, //1
    val resto: String = "", //2
    val paket: String = "", //3
    val name: String = "", //4
    val address: String = "", //5
    val date: String = "", //6
    val time: String = "", //7
    val phone: String = "", //8
    val notes: String = "", //9
    val status_bayar : Boolean = false, //10
    val status_aktif : Boolean = true, //11
    val userId: String = "", //12
    val uniqueCode: String = "", //13
    val timestamp: Long? = null, //14
    val hargaTotal: Int = 0, //15
    val hargaDP: Int = 0, //16
    val hargaSisa: Int = 0, //17
    val statusDP: Boolean = false, //18
    val statusSisa: Boolean = false, //19
    val jumlahOrang: Int = 0 // 20
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(), //1
        parcel.readString() ?: "", //2
        parcel.readString() ?: "", //3
        parcel.readString() ?: "", //4
        parcel.readString() ?: "", //5
        parcel.readString() ?: "", //6
        parcel.readString() ?: "", //7
        parcel.readString() ?: "", //8
        parcel.readString() ?: "", //9
        parcel.readByte() != 0.toByte(), //10
        parcel.readByte() != 0.toByte(), //11
        parcel.readString() ?: "", //12
        parcel.readString() ?: "", //13
        parcel.readValue(Long::class.java.classLoader) as? Long, //14
        parcel.readInt(), //15
        parcel.readInt(), //16
        parcel.readInt(), //17
        parcel.readByte() != 0.toByte(), //18
        parcel.readByte() != 0.toByte(), //19
        parcel.readInt() //20
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id) //1
        parcel.writeString(resto) //2
        parcel.writeString(paket) //3
        parcel.writeString(name) //4
        parcel.writeString(address) //5
        parcel.writeString(date) //6
        parcel.writeString(time) //7
        parcel.writeString(phone) //8
        parcel.writeString(notes) //9
        parcel.writeByte(if (status_bayar) 1 else 0) //10
        parcel.writeByte(if (status_aktif) 1 else 0) //11
        parcel.writeString(userId) //12
        parcel.writeString(uniqueCode) //13
        parcel.writeValue(timestamp) //14
        parcel.writeInt(hargaTotal) //15
        parcel.writeInt(hargaDP) //16
        parcel.writeInt(hargaSisa) //17
        parcel.writeByte(if (statusDP) 1 else 0) //18
        parcel.writeByte(if (statusSisa) 1 else 0) //19
        parcel.writeInt(jumlahOrang) //20
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