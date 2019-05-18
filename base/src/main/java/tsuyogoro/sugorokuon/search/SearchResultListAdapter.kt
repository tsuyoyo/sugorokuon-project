package tsuyogoro.sugorokuon.search

import androidx.recyclerview.widget.RecyclerView
import android.view.ViewGroup
import tsuyogoro.sugorokuon.ad.AdBannerViewHolder

class SearchResultListAdapter(
        private val listener: SearchResultListAdapterListener,
        private var searchResults: List<SearchViewModel.SearchResultData> = emptyList()
) : androidx.recyclerview.widget.RecyclerView.Adapter<androidx.recyclerview.widget.RecyclerView.ViewHolder>() {

    private object Type {
        const val PROGRAM = 0
        const val AD = 1
    }

    interface SearchResultListAdapterListener {
        fun onSearchResultClicked(searchResult: SearchViewModel.SearchResultData)
    }

    fun setSearchResults(searchResults: List<SearchViewModel.SearchResultData>) {
        this.searchResults = searchResults
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): androidx.recyclerview.widget.RecyclerView.ViewHolder =
            when (viewType) {
                Type.AD -> AdBannerViewHolder(parent)
                else -> SearchResultViewHolder(parent, listener)
            }

    override fun getItemCount(): Int = searchResults.size + 1

    override fun getItemViewType(position: Int): Int =
            if (position == 0) {
                Type.AD
            } else {
                Type.PROGRAM
            }

    override fun onBindViewHolder(holder: androidx.recyclerview.widget.RecyclerView.ViewHolder, position: Int) {
        if (position > 0) {
            // Ad is on first index.
            (holder as SearchResultViewHolder).setSearchResult(searchResults[position - 1])
        }
    }


}