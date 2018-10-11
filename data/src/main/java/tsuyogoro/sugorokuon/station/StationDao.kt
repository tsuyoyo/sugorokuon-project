package tsuyogoro.sugorokuon.station

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query

@Dao
interface StationDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(station: Station)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(stations: List<Station>)

    @Query("SELECT * FROM stations")
    fun getAll(): List<Station>

    @Query("DELETE From stations")
    fun clearTable()
}