package tsuyogoro.sugorokuon.timetable

import android.app.DatePickerDialog
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.graphics.Point
import android.graphics.Rect
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.widget.ContentLoadingProgressBar
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import tsuyogoro.sugorokuon.R
import tsuyogoro.sugorokuon.SugorokuonApplication
import tsuyogoro.sugorokuon.SugorokuonTopActivity
import tsuyogoro.sugorokuon.api.response.StationResponse
import tsuyogoro.sugorokuon.api.response.TimeTableResponse
import tsuyogoro.sugorokuon.utils.SugorokuonUtils
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

class ProgramTableFragment : Fragment(),
        DateSelectorAdapter.DateSelectorListener,
        ProgramTableAdapter.ProgramTableAdapterListener {

    @Inject
    lateinit var viewModelFactory: ProgramTableViewModel.Factory

    private val programTable: RecyclerView
        get() = view!!.findViewById(R.id.program_table)

    private val loading: ContentLoadingProgressBar
        get() = view!!.findViewById(R.id.loading)

    private val date: TextView
        get() = view!!.findViewById(R.id.date)

    private val buttonDateSelect: Button
        get() = view!!.findViewById(R.id.btn_date_select)

    private lateinit var viewModel: ProgramTableViewModel
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

        viewModel = ViewModelProviders
                .of(this, viewModelFactory)
                .get(ProgramTableViewModel::class.java)

        viewModel.observeSelectedDate()
                .observe(this, Observer { it ->
                    if (it != null) {
                        date.text = SimpleDateFormat(
                                resources.getString(R.string.date_label),
                                Locale.JAPAN
                        ).format(Date(it.timeInMillis)).toString()
                    }
                })

        programTableAdapter = ProgramTableAdapter(this)

        programTable.apply {
            adapter = programTableAdapter
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            addItemDecoration(ProgramTableItemDecoration())
        }

        buttonDateSelect.setOnClickListener { onDateSelectionClicked() }

        viewModel.observeTimeTable()
                .observe(this, Observer {
                    if (it != null) {
                        programTableAdapter.setTimeTables(it)
                        programTableAdapter.notifyDataSetChanged()
                    }
                })

        viewModel.observeIsLoading()
                .observe(this, Observer {
                    loading.visibility = if (it != null && it) { View.VISIBLE } else { View.GONE }
                })
    }

    override fun onDateSelected(date: Calendar) {
        viewModel.selectDate(date)
    }

    override fun onStationSiteClicked(station: StationResponse.Station) {
        SugorokuonUtils.launchChromeTab(activity, Uri.parse(station.webSite))
    }

    override fun onProgramClicked(program: TimeTableResponse.Program, clickedPosition: Point) {
        (activity as? SugorokuonTopActivity)?.pushFragment(
                ProgramInfoFragment.createInstance(
                        program,
                        ProgramInfoFragment.TransitionParameters(
                                clickedPosition.x,
                                clickedPosition.y,
                                Math.max(view?.width ?: 0, view?.height ?: 0)
                        )
                ),
                ProgramInfoFragment.FRAGMENT_TAG
        )
    }

    private fun onDateSelectionClicked() {
        val today = Calendar.getInstance()

        val datePickerDialog = DatePickerDialog(context,
                DatePickerDialog.OnDateSetListener { _, year, month, day ->
                    viewModel.selectDate(Calendar.getInstance().apply {
                        set(Calendar.YEAR, year)
                        set(Calendar.MONTH, month)
                        set(Calendar.DAY_OF_MONTH, day)
                    })
                },
                today.get(Calendar.YEAR), today.get(Calendar.MONTH), today.get(Calendar.DAY_OF_MONTH)
        )
        datePickerDialog.datePicker.let {
            it.maxDate = Calendar.getInstance()
                    .apply {
                        add(Calendar.DAY_OF_MONTH, 7)
                    }
                    .timeInMillis
            it.minDate = Calendar.getInstance()
                    .apply {
                        add(Calendar.DAY_OF_MONTH, -6)
                    }
                    .timeInMillis
        }
        datePickerDialog.show()

        // TODO : ジョニーのEventRegisterFragmentを参考にして、回転時の対応を
        // TODO : 縦横回転時にdatePickerDialog閉じないとリークするっぽいのでそれも対応
    }


    class ProgramTableItemDecoration: RecyclerView.ItemDecoration() {

        override fun getItemOffsets(outRect: Rect?,
                                    view: View?,
                                    parent: RecyclerView?,
                                    state: RecyclerView.State?) {
            super.getItemOffsets(outRect, view, parent, state)
            if (view != null) {
                val resources = view.context.resources
                val density = resources.displayMetrics.density

                outRect?.top = (density * 4).toInt()
                outRect?.bottom = (density * 4).toInt()

                if (parent?.getChildAdapterPosition(view) == 0) {
                    outRect?.top = resources.getDimensionPixelSize(R.dimen.date_label_height) +
                            (density * 16).toInt()
                }
            }
        }
    }

}