package tsuyogoro.sugorokuon.songs

import android.os.Bundle
import android.os.Handler
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.InterstitialAd
import com.google.android.gms.ads.MobileAds
import tsuyogoro.sugorokuon.R

class OnAirSongsSearchTutorialFragment : Fragment() {

    companion object {
        const val INTERVAL_BEFORE_SHOWING_AD_IN_MILLI_SEC = 3000L
    }

    private lateinit var interstitialAd: InterstitialAd

    private var hasAdBeenDisplayed = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        MobileAds.initialize(context, resources.getString(R.string.adUnitId))

        interstitialAd = InterstitialAd(context)
        interstitialAd.adUnitId = resources.getString(R.string.radibangInterstitialAd01)
    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?): View? =
            inflater.inflate(R.layout.fragment_on_air_songs_search_tutorial, container, false)

    fun showAd() {
        if (!hasAdBeenDisplayed) {
            Handler().postDelayed({
                interstitialAd.loadAd(AdRequest.Builder().build())
                interstitialAd.adListener = object : AdListener() {
                    override fun onAdLoaded() {
                        super.onAdLoaded()
                        interstitialAd.show()
                        Log.e("TestTestTest", "show()")
                    }

                    override fun onAdFailedToLoad(p0: Int) {
                        super.onAdFailedToLoad(p0)
                        Log.e("TestTestTest", "Failed to load")
                    }
                }
                hasAdBeenDisplayed = true
            }, INTERVAL_BEFORE_SHOWING_AD_IN_MILLI_SEC)
//            Toast.makeText(context, R.string.song_search_tutorial_note_show_ad, Toast.LENGTH_SHORT)
//                    .show()
        }
    }

}