package tsuyogoro.sugorokuon.setting

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.TextView
import tsuyogoro.sugorokuon.base.R
import tsuyogoro.sugorokuon.constant.SearchSongMethod

class SearchSongMethodListAdapter(
        private val methods: MutableList<SearchSongMethod> = mutableListOf(),
        private var selectedMethod: SearchSongMethod? = null,
        private val listener: SearchSongMethodListListener
) : androidx.recyclerview.widget.RecyclerView.Adapter<SearchSongMethodListAdapter.ViewHolder>() {

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
                     private val listener: SearchSongMethodListListener) : androidx.recyclerview.widget.RecyclerView.ViewHolder(
        LayoutInflater
                .from(parent.context)
                .inflate(R.layout.item_song_search_method, parent, false)
    ) {
        private val name: TextView
            get() = itemView.findViewById(R.id.name)

        private val checkBox: RadioButton
            get() = itemView.findViewById(R.id.checkbox)

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