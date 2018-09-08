package tsuyogoro.sugorokuon.songs

import android.content.res.Resources
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import tsuyogoro.sugorokuon.base.R
import tsuyogoro.sugorokuon.api.response.StationResponse

class OnAirSongsFragmentPagerAdapter(
        fragmentManager: FragmentManager,
        private val resources: Resources
) : FragmentPagerAdapter(fragmentManager) {

    private var feedAvailableStations = mutableListOf<StationResponse.Station>()

    fun setOnAirSongsAvailableStations(stations: List<StationResponse.Station>) {
        this.feedAvailableStations.clear()
        this.feedAvailableStations.addAll(stations)
    }

    override fun getItem(position: Int): Fragment =
            if (position == 0) {
                OnAirSongsSearchTutorialFragment()
            } else {
                OnAirSongsFragment.createInstance(feedAvailableStations[position - 1].id)
            }


    override fun getCount(): Int =
        if (feedAvailableStations.isEmpty()) {
            0
        } else {
            feedAvailableStations.size + 1
        }

    override fun getPageTitle(position: Int): CharSequence =
            if (position == 0) {
                resources.getString(R.string.song_search_tutorial_search)
            } else {
                feedAvailableStations[position - 1].name
            }

}