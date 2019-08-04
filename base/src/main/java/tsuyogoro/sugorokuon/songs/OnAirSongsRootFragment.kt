package tsuyogoro.sugorokuon.songs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.ContentLoadingProgressBar
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.viewpager.widget.PagerTabStrip
import androidx.viewpager.widget.ViewPager
import tsuyogoro.sugorokuon.SugorokuonApplication
import tsuyogoro.sugorokuon.base.R
import tsuyogoro.sugorokuon.extension.getFocusedFragment
import tsuyogoro.sugorokuon.station.Station
import javax.inject.Inject

class OnAirSongsRootFragment : androidx.fragment.app.Fragment() {

    private val viewPager: ViewPager
        get() = view!!.findViewById(R.id.view_pager)

    private val pagerTabStrip: PagerTabStrip
        get() = view!!.findViewById(R.id.pager_tab_strip)

    private val loading: ContentLoadingProgressBar
        get() = view!!.findViewById(R.id.loading)

    @Inject
    lateinit var viewModelFactory: OnAirSongsRootViewModel.Factory

    private lateinit var viewModel: OnAirSongsRootViewModel
    private lateinit var fragmentPagerAdapter: OnAirSongsFragmentPagerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        SugorokuonApplication.application(context)
                .appComponent()
                .onAirSongsRootSubComponent(OnAirSongsRootModule())
                .inject(this)

        viewModel = ViewModelProviders
                .of(this, viewModelFactory)
                .get(OnAirSongsRootViewModel::class.java)

        // Ref : http://wasnot.hatenablog.com/entry/2013/04/20/220534
        fragmentPagerAdapter = OnAirSongsFragmentPagerAdapter(childFragmentManager, resources)
    }

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? =
            inflater.inflate(R.layout.fragment_on_air_songs_root, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewPager.adapter = fragmentPagerAdapter
        pagerTabStrip.setTabIndicatorColorResource(R.color.app_primary)

        viewModel.observeFeedAvailableStations()
                .observe(this, onAvailableStationsFetched())

        viewModel.observeIsLoading()
                .observe(this, Observer {
                    if (it != null) {
                        loading.visibility = if (it) {
                            View.VISIBLE
                        } else {
                            View.GONE
                        }
                    }
                })

        viewPager.addOnPageChangeListener(object : androidx.viewpager.widget.ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {
            }

            override fun onPageScrolled(
                    position: Int, positionOffset: Float, positionOffsetPixels: Int) {
            }

            override fun onPageSelected(position: Int) {
                if (position == 0) {
                    childFragmentManager
                            .getFocusedFragment<OnAirSongsSearchTutorialFragment>(position, R.id.view_pager)
                            ?.let { it.showAd() }
                }
            }
        })
    }

    private fun onAvailableStationsFetched() = Observer<List<Station>> {
        if (it != null) {
            fragmentPagerAdapter.apply {
                setOnAirSongsAvailableStations(it)
                notifyDataSetChanged()
            }
            if (it.isNotEmpty()) {
                viewPager.setCurrentItem(1, false)
            }
        } else {
            // TODO : nullだったら何かメッセージ出してもいいかもしれん
        }
    }
}