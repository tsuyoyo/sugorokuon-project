package tsuyogoro.sugorokuon.station

import androidx.room.Entity
import androidx.room.PrimaryKey
import tsuyogoro.sugorokuon.constant.Area

@Entity(tableName = "stations")
data class Station(
    @PrimaryKey val id: String,
    val area: Area,
    val name: String,
    val asciiName: String,
    val url: String,
    val logo: List<Logo>,
    val banner: String
) {
    data class Logo(
        val width: Int,
        val height: Int,
        val url: String
    )
}

