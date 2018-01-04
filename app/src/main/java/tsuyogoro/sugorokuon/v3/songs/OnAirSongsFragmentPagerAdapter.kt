package tsuyogoro.sugorokuon.v3.songs

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter

class OnAirSongsFragmentPagerAdapter(fragmentManager: FragmentManager)
    : FragmentPagerAdapter(fragmentManager) {

    private var onAirSongsDataList = mutableListOf<OnAirSongsData>()

    fun setOnAirSongsAvailableStations(onAirSongsDataList: List<OnAirSongsData>) {
        this.onAirSongsDataList.addAll(onAirSongsDataList)
    }

    override fun getItem(position: Int): Fragment =
            OnAirSongsFragment().apply {
                setOnAirSongsData(onAirSongsDataList[position])
            }

    override fun getCount(): Int = onAirSongsDataList.size

    override fun getPageTitle(position: Int): CharSequence =
            onAirSongsDataList[position].station.name
}