package tsuyogoro.sugorokuon.station

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
internal interface StationDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(station: Station)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(stations: List<Station>)

    @Query("SELECT * FROM stations")
    fun getAll(): List<Station>

    @Query("DELETE From stations")
    fun clearTable()
}