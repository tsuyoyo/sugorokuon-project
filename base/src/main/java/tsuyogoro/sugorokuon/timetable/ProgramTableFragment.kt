package tsuyogoro.sugorokuon.timetable

import android.app.DatePickerDialog
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import android.graphics.Point
import android.graphics.Rect
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.core.widget.ContentLoadingProgressBar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import tsuyogoro.sugorokuon.SugorokuonApplication
import tsuyogoro.sugorokuon.SugorokuonTopActivity
import tsuyogoro.sugorokuon.base.R
import tsuyogoro.sugorokuon.radiko.api.response.TimeTableResponse
import tsuyogoro.sugorokuon.recommend.RecommendProgram
import tsuyogoro.sugorokuon.recommend.keyword.RecommendKeywordFragment
import tsuyogoro.sugorokuon.recommend.settings.RecommendSettingsRepository
import tsuyogoro.sugorokuon.setting.SettingsTopFragment
import tsuyogoro.sugorokuon.station.Station
import tsuyogoro.sugorokuon.utils.SugorokuonUtils
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

class ProgramTableFragment : androidx.fragment.app.Fragment(),
        DateSelectorAdapter.DateSelectorListener,
        ProgramTableAdapter.ProgramTableAdapterListener {

    @Inject
    lateinit var viewModelFactory: ProgramTableViewModel.Factory

    @Inject
    lateinit var recommendSettingsRepository: RecommendSettingsRepository

    private val programTable: androidx.recyclerview.widget.RecyclerView
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

        programTableAdapter = ProgramTableAdapter(this, recommendSettingsRepository)

        programTable.apply {
            adapter = programTableAdapter
            layoutManager = androidx.recyclerview.widget.LinearLayoutManager(context, androidx.recyclerview.widget.LinearLayoutManager.VERTICAL, false)
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

        viewModel.observeRecommendPrograms()
            .observe(this, Observer {
                programTableAdapter.setRecommend(it ?: emptyList())
                programTableAdapter.notifyDataSetChanged()
            })
    }

    override fun onDateSelected(date: Calendar) {
        viewModel.selectDate(date)
    }

    override fun onStationSiteClicked(station: Station) {
        SugorokuonUtils.launchChromeTab(activity, Uri.parse(station.url))
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

    override fun onRecommendProgramClicked(program: RecommendProgram) {
        (activity as? SugorokuonTopActivity)?.pushFragment(
            ProgramInfoFragment.createInstance(program),
            ProgramInfoFragment.FRAGMENT_TAG
        )
    }

    override fun onGotoKeywordSettingsClicked() {
        (activity as? SugorokuonTopActivity)?.gotoRecoomendKeywordSettings()
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

    class ProgramTableItemDecoration: androidx.recyclerview.widget.RecyclerView.ItemDecoration() {

        override fun getItemOffsets(outRect: Rect,
                                    view: View,
                                    parent: androidx.recyclerview.widget.RecyclerView,
                                    state: androidx.recyclerview.widget.RecyclerView.State) {
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