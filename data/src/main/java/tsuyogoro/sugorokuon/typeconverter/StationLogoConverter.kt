package tsuyogoro.sugorokuon.typeconverter

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import tsuyogoro.sugorokuon.station.Station

internal class StationLogoConverter {
    @TypeConverter
    fun stationLogosToString(logos: List<Station.Logo>?): String? =
        Gson().toJson(logos)

    @TypeConverter
    fun jsonToStationLogo(json: String?): List<Station.Logo>? =
        Gson().fromJson(json, object : TypeToken<List<Station.Logo>>(){ }.type)
}