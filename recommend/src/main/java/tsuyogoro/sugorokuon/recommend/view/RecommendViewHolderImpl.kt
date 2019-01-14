package tsuyogoro.sugorokuon.recommend.view

import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.rxkotlin.subscribeBy
import tsuyogoro.sugorokuon.base.R
import tsuyogoro.sugorokuon.dynamicfeature.RecommendModuleDependencyResolver
import tsuyogoro.sugorokuon.recommend.DaggerRecommendComponent
import tsuyogoro.sugorokuon.recommend.RecommendInternalModule
import tsuyogoro.sugorokuon.timetable.ProgramTableAdapter
import javax.inject.Inject

class RecommendViewHolderImpl(
    parent: ViewGroup,
    private val programTableAdapterListener: ProgramTableAdapter.ProgramTableAdapterListener
) : RecyclerView.ViewHolder(
    LayoutInflater.from(parent.context)
        .inflate(R.layout.item_recommend_carousel, parent, false)
), RecommendModuleDependencyResolver.RecommendViewHolder {
    private val recommendTitle: TextView
        get() = itemView.findViewById(R.id.recommend_title)

    private val noRecommend: TextView
        get() = itemView.findViewById(R.id.no_recommend)

    private val recommendPrograms: RecyclerView
        get() = itemView.findViewById(R.id.recommend_programs)

    private val gotoKeywordSettings: Button
        get() = itemView.findViewById(R.id.goto_keyword_settings_button)

    private val carouselAdapter: RecommendProgramsCarouselAdapter =
        RecommendProgramsCarouselAdapter(programTableAdapterListener)

    @Inject
    lateinit var viewModel: RecommendViewHolderViewModel

    private var disposables =  CompositeDisposable()

    init {
        recommendPrograms.apply {
            adapter = carouselAdapter
            layoutManager = LinearLayoutManager(
                itemView.context, LinearLayoutManager.HORIZONTAL, false)
        }
        recommendPrograms.addItemDecoration(ProgramTableAdapter.ProgramListItemDecoration())

        gotoKeywordSettings.setOnClickListener {
            programTableAdapterListener.onGotoKeywordSettingsClicked()
        }

        DaggerRecommendComponent.builder()
            .recommendInternalModule(RecommendInternalModule(parent.context))
            .build()
            .inject(this)
    }

    override fun onBoundWithAdapter() {
        viewModel.observeRecommendPrograms()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy {
                carouselAdapter.setRecommendPrograms(it)
                carouselAdapter.notifyDataSetChanged()
            }
            .addTo(disposables)

        viewModel.observeNoRecommendLabelVisibility()
            .map { if (it) { View.VISIBLE } else { View.GONE } }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy { noRecommend.visibility = it }
            .addTo(disposables)

        viewModel.observeRecommendProgramsVisibility()
            .map { if (it) { View.VISIBLE } else { View.GONE } }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy { recommendPrograms.visibility = it }
            .addTo(disposables)

        viewModel.observeGotoKeywordSettingsVisibility()
            .map { if (it) { View.VISIBLE } else { View.GONE } }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy { gotoKeywordSettings.visibility = it }
            .addTo(disposables)
    }

    override fun onUnboundFromAdapter() {
        disposables.clear()
    }
}