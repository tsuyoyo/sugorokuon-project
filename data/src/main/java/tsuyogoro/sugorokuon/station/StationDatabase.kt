package tsuyogoro.sugorokuon.station

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import tsuyogoro.sugorokuon.typeconverter.AreaConverter
import tsuyogoro.sugorokuon.typeconverter.StationLogoConverter

@Database(entities = [Station::class], version = 1)
@TypeConverters(StationLogoConverter::class, AreaConverter::class)
internal abstract class StationDatabase : RoomDatabase() {

    abstract fun stationDao(): StationDao

}