package project.paba.app

data class dataRestoran(
    val id: String = "",
    val namaResto: String = "",
    val deskripsi: String = "",
    val alamat: String = "",
    val foto : String = "",
    val jamBuka :String = "",
    val jamTutup : String = "",
    val paket: List<paketRestoran> = listOf()
)
