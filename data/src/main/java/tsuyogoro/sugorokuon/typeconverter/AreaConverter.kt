package tsuyogoro.sugorokuon.typeconverter

import androidx.room.TypeConverter
import tsuyogoro.sugorokuon.constant.Area

internal class AreaConverter {

    @TypeConverter
    fun areaToEnumIndex(area: Area): Int = area.ordinal

    @TypeConverter
    fun enumIndexToArea(index: Int): Area? = Area.values()[index]
}