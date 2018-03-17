package tsuyogoro.sugorokuon.setting

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import butterknife.BindView
import butterknife.ButterKnife
import tsuyogoro.sugorokuon.R
import tsuyogoro.sugorokuon.constant.Area

class AreaSettingsListAdapter(
        private val areas: List<Area>,
        private val listener: OnAreaSelectedListener,
        private val selectedAreas: MutableList<Area> = mutableListOf()
) : RecyclerView.Adapter<AreaSettingsListAdapter.ViewHolder>() {

    interface OnAreaSelectedListener {
        fun onAreaSelected(area: Area)

        fun onAreaDeselected(area: Area)
    }

    fun setSelectedAreas(selectedAreas: Set<Area>) {
        this.selectedAreas.clear()
        this.selectedAreas.addAll(selectedAreas)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        areas[position].let {
            holder.setData(it, selectedAreas.contains(it))
        }
    }

    override fun getItemCount(): Int = areas.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(parent, listener)

    class ViewHolder(parent: ViewGroup,
                     private val listener: OnAreaSelectedListener) : RecyclerView.ViewHolder(
            LayoutInflater
                    .from(parent.context)
                    .inflate(R.layout.item_area_settings, parent, false)
    ) {

        @BindView(R.id.name)
        lateinit var name: TextView

        @BindView(R.id.checkbox)
        lateinit var checkBox: CheckBox

        init {
            ButterKnife.bind(this, itemView)
        }

        fun setData(area: Area, isChecked: Boolean) {
            name.text = itemView.resources.getString(area.strId)
            checkBox.isChecked = isChecked
            itemView.setOnClickListener { _ ->
                if (isChecked) {
                    listener.onAreaDeselected(area)
                } else {
                    listener.onAreaSelected(area)
                }
            }
        }
    }

}