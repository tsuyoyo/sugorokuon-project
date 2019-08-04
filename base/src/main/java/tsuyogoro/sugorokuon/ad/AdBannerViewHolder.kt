package tsuyogoro.sugorokuon.ad

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import tsuyogoro.sugorokuon.base.R

class AdBannerViewHolder(parent: ViewGroup?) : androidx.recyclerview.widget.RecyclerView.ViewHolder(
        LayoutInflater
                .from(parent!!.context)
                .inflate(R.layout.item_ad_banner, parent, false)
) {
    private val adView: AdView
        get() = itemView.findViewById(R.id.ad_view)

    init {
        adView.loadAd(AdRequest.Builder().build())
    }
}