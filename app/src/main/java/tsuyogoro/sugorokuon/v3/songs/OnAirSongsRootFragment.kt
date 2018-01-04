package tsuyogoro.sugorokuon.v3.songs

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.view.PagerTabStrip
import android.support.v4.view.ViewPager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import butterknife.BindView
import butterknife.ButterKnife
import tsuyogoro.sugorokuon.R
import tsuyogoro.sugorokuon.SugorokuonApplication
import tsuyogoro.sugorokuon.v3.api.response.StationResponse
import javax.inject.Inject

class OnAirSongsRootFragment : Fragment() {

    @BindView(R.id.view_pager)
    lateinit var viewPager: ViewPager

    @BindView(R.id.pager_tab_strip)
    lateinit var pagerTabStrip: PagerTabStrip

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

        fragmentPagerAdapter = OnAirSongsFragmentPagerAdapter(fragmentManager)
    }

    override fun onCreateView(inflater: LayoutInflater?,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? =
            inflater?.inflate(R.layout.fragment_on_air_songs_root, container, false)

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        ButterKnife.bind(this, view!!)

        viewPager.adapter = fragmentPagerAdapter
        pagerTabStrip.setTabIndicatorColorResource(R.color.app_primary)

        viewModel.observeFeedAvailableStations()
                .observe(this, onAvailableStationsFetched())
    }

    private fun onAvailableStationsFetched() = Observer<List<OnAirSongsData>> {
        if (it != null) {
            fragmentPagerAdapter.apply {
                setOnAirSongsAvailableStations(it)
                notifyDataSetChanged()
            }
        } else {
            // TODO : nullだったら何かメッセージ出してもいいかもしれん
        }
    }
}