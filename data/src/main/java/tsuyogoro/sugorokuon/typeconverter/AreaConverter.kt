package tsuyogoro.sugorokuon.typeconverter

import android.arch.persistence.room.TypeConverter
import tsuyogoro.sugorokuon.constant.Area

internal class AreaConverter {

    @TypeConverter
    fun areaToEnumIndex(area: Area): Int = area.ordinal

    @TypeConverter
    fun enumIndexToArea(index: Int): Area? = Area.values()[index]
}