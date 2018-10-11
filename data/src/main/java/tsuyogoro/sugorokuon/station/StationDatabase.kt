package tsuyogoro.sugorokuon.station

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase
import android.arch.persistence.room.TypeConverters
import tsuyogoro.sugorokuon.typeconverter.AreaConverter
import tsuyogoro.sugorokuon.typeconverter.StationLogoConverter

@Database(entities = [Station::class], version = 1)
@TypeConverters(StationLogoConverter::class, AreaConverter::class)
abstract class StationDatabase : RoomDatabase() {

    abstract fun stationDao(): StationDao

}