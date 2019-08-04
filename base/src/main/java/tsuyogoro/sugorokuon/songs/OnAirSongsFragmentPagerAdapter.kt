package tsuyogoro.sugorokuon.songs

import android.content.res.Resources
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import tsuyogoro.sugorokuon.base.R
import tsuyogoro.sugorokuon.station.Station

class OnAirSongsFragmentPagerAdapter(
    fragmentManager: androidx.fragment.app.FragmentManager,
    private val resources: Resources
) : androidx.fragment.app.FragmentPagerAdapter(fragmentManager) {

    private var feedAvailableStations = mutableListOf<Station>()

    fun setOnAirSongsAvailableStations(stations: List<Station>) {
        this.feedAvailableStations.clear()
        this.feedAvailableStations.addAll(stations)
    }

    override fun getItem(position: Int): androidx.fragment.app.Fragment =
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