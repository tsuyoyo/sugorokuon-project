package tsuyogoro.sugorokuon.v3.timetable

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.graphics.Rect
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import butterknife.BindView
import butterknife.ButterKnife
import tsuyogoro.sugorokuon.R
import tsuyogoro.sugorokuon.SugorokuonApplication
import tsuyogoro.sugorokuon.utils.SugorokuonUtils
import tsuyogoro.sugorokuon.v3.api.response.StationResponse
import tsuyogoro.sugorokuon.v3.api.response.TimeTableResponse
import java.util.*
import javax.inject.Inject

class ProgramTableFragment : Fragment(),
        DateSelectorAdapter.DateSelectorListener,
        ProgramTableAdapter.ProgramTableAdapterListener {

    @Inject
    lateinit var viewModelFactory: ProgramTableViewModel.Factory

    @BindView(R.id.program_table)
    lateinit var programTable: RecyclerView

    @BindView(R.id.date_selector)
    lateinit var dateSelector: RecyclerView

    private lateinit var viewModel: ProgramTableViewModel
    private lateinit var dateSelectorAdapter: DateSelectorAdapter
    private lateinit var programTableAdapter: ProgramTableAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        SugorokuonApplication.application(context)
                .appComponent()
                .programTableSubComponent(ProgramTableModule())
                .inject(this)
    }

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? =
            inflater.inflate(R.layout.fragment_program_table, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        ButterKnife.bind(this, view)
        viewModel = ViewModelProviders
                .of(this, viewModelFactory)
                .get(ProgramTableViewModel::class.java)

        dateSelectorAdapter = DateSelectorAdapter(this)
        dateSelector.apply {
            adapter = dateSelectorAdapter
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        }

        viewModel.observeSelectedDate()
                .observe(this, Observer { it ->
                    if (it != null) {
                        dateSelectorAdapter.setSelectedDate(it)
                        dateSelector.scrollToPosition(dateSelectorAdapter.getPosition(it))
                    }
                })

        programTableAdapter = ProgramTableAdapter(this)
        programTable.apply {
            adapter = programTableAdapter
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            addItemDecoration(ProgramTableItemDecoration())
        }
        viewModel.observeTimeTable()
                .observe(this, Observer {
                    if (it != null) {
                        programTableAdapter.setTimeTables(it)
                        programTableAdapter.notifyDataSetChanged()
                    }
                })
    }

    override fun onDateSelected(date: Calendar) {
        viewModel.selectDate(date)
    }

    override fun onStationSiteClicked(station: StationResponse.Station) {
        SugorokuonUtils.launchChromeTab(activity, Uri.parse(station.webSite))
    }

    override fun onProgramClicked(program: TimeTableResponse.Program) {
        // TODO : 番組情報の表示を
        Log.d("TestTestTest", "onProgramClicked : ${program.title}")
    }

    class ProgramTableItemDecoration: RecyclerView.ItemDecoration() {

        override fun getItemOffsets(outRect: Rect?,
                                    view: View?,
                                    parent: RecyclerView?,
                                    state: RecyclerView.State?) {
            super.getItemOffsets(outRect, view, parent, state)
            if (view != null) {
                outRect?.top = (view.context.resources.displayMetrics.density * 4).toInt()
                outRect?.bottom = (view.context.resources.displayMetrics.density * 4).toInt()
            }
        }
    }

}