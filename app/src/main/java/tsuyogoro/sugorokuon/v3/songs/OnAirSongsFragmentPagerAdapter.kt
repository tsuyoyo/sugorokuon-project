package tsuyogoro.sugorokuon.v3.songs

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import tsuyogoro.sugorokuon.v3.api.response.StationResponse

class OnAirSongsFragmentPagerAdapter(fragmentManager: FragmentManager)
    : FragmentPagerAdapter(fragmentManager) {

    private var feedAvailableStations = mutableListOf<StationResponse.Station>()

    fun setOnAirSongsAvailableStations(stations: List<StationResponse.Station>) {
        this.feedAvailableStations.clear()
        this.feedAvailableStations.addAll(stations)
    }

    override fun getItem(position: Int): Fragment =
            OnAirSongsFragment.createInstance(feedAvailableStations[position].id)

    override fun getCount(): Int = feedAvailableStations.size

    override fun getPageTitle(position: Int): CharSequence =
            feedAvailableStations[position].name


}