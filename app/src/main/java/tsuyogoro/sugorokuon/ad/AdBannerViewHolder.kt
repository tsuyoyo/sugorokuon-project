package tsuyogoro.sugorokuon.ad

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import butterknife.BindView
import butterknife.ButterKnife
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import tsuyogoro.sugorokuon.R

class AdBannerViewHolder(parent: ViewGroup?) : RecyclerView.ViewHolder(
        LayoutInflater
                .from(parent!!.context)
                .inflate(R.layout.item_ad_banner, parent, false)
) {
    @BindView(R.id.ad_view)
    lateinit var adView: AdView

    init {
        ButterKnife.bind(this, itemView)
        Runnable { adView.loadAd(AdRequest.Builder().build()) }.run()
    }
}