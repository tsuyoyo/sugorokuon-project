package tsuyogoro.sugorokuon.recommend.view

import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import tsuyogoro.sugorokuon.base.R
import tsuyogoro.sugorokuon.recommend.keyword.RecommendKeyword
import tsuyogoro.sugorokuon.timetable.ProgramTableAdapter
import tsuyogoro.sugorokuon.timetable.RecommendProgramData

class RecommendViewHolder(
    parent: ViewGroup,
    private val programTableAdapterListener: ProgramTableAdapter.ProgramTableAdapterListener
) : RecyclerView.ViewHolder(
    LayoutInflater.from(parent.context)
        .inflate(R.layout.item_recommend_carousel, parent, false)
) {
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
    }

    fun setRecommendPrograms(
        recommends: List<RecommendProgramData>,
        keywords: List<RecommendKeyword>
    ) {
        noRecommend.visibility =
            if (keywords.any { it.keyword.isNotEmpty() } && recommends.isEmpty()) {
                View.VISIBLE
            } else {
                View.GONE
            }
        recommendPrograms.visibility = if (keywords.isNotEmpty() && recommends.isNotEmpty()) {
            View.VISIBLE
        } else {
            View.GONE
        }
        gotoKeywordSettings.visibility =
            if (!keywords.any { it.keyword.isNotEmpty() }) {
                View.VISIBLE
            } else {
                View.GONE
            }
        recommendTitle.visibility = View.VISIBLE

        carouselAdapter.setRecommendPrograms(recommends)
        carouselAdapter.notifyDataSetChanged()
    }
}