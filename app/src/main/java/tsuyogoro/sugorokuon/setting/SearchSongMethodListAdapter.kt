package tsuyogoro.sugorokuon.setting

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.TextView
import butterknife.BindView
import butterknife.ButterKnife
import tsuyogoro.sugorokuon.R
import tsuyogoro.sugorokuon.constant.SearchSongMethod

class SearchSongMethodListAdapter(
        private val methods: MutableList<SearchSongMethod> = mutableListOf(),
        private var selectedMethod: SearchSongMethod? = null,
        private val listener: SearchSongMethodListListener
) : RecyclerView.Adapter<SearchSongMethodListAdapter.ViewHolder>() {

    interface SearchSongMethodListListener {
        fun onSearchSongMethodSelected(selectedMethod: SearchSongMethod)
    }

    fun setSearchSongMethods(methods: List<SearchSongMethod>) {
        this.methods.clear()
        this.methods.addAll(methods)
    }

    fun setSelectedMethod(method: SearchSongMethod) {
        this.selectedMethod = method
    }

    override fun getItemCount(): Int = methods.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
            ViewHolder(parent, listener)

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        methods[position].let {
            holder.setData(it, selectedMethod == it)
        }
    }

    class ViewHolder(parent: ViewGroup,
                     private val listener: SearchSongMethodListListener) : RecyclerView.ViewHolder(
        LayoutInflater
                .from(parent.context)
                .inflate(R.layout.item_song_search_method, parent, false)
    ) {
        @BindView(R.id.name)
        lateinit var name: TextView

        @BindView(R.id.checkbox)
        lateinit var checkBox: RadioButton

        init {
            ButterKnife.bind(this, itemView)
        }

        fun setData(method: SearchSongMethod, isSelected: Boolean) {
            name.text = method.getDisplayName(itemView.resources)
            checkBox.isChecked = isSelected
            itemView.setOnClickListener { _ ->
                if (!isSelected) {
                    listener.onSearchSongMethodSelected(method)
                }
            }
        }
    }
}