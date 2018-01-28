package tsuyogoro.sugorokuon.v3.timetable

import android.animation.Animator
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.annotation.Nullable
import android.support.v4.app.Fragment
import android.view.*
import android.webkit.WebView
import android.widget.ImageView
import android.widget.TextView
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import com.bumptech.glide.Glide
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import tsuyogoro.sugorokuon.R
import tsuyogoro.sugorokuon.utils.SugorokuonUtils
import tsuyogoro.sugorokuon.v3.api.response.TimeTableResponse
import java.io.Serializable
import java.text.SimpleDateFormat
import java.util.*

class ProgramInfoFragment : Fragment() {

    data class TransitionParameters(
            val tappedPositionX: Int,
            val tappedPositionY: Int,
            val rippleRadius: Int
    ) : Serializable

    companion object {
        val KEY_PROGRAM = "program"
        val KEY_TRANSITION_PARAMS = "transition_params"

        val FRAGMENT_TAG = "programInfo"

        fun createInstance(program: TimeTableResponse.Program,
                           @Nullable transitionParams: TransitionParameters) =
                ProgramInfoFragment().apply {
                    arguments = Bundle().apply {
                        putSerializable(KEY_PROGRAM, program)
                        putSerializable(KEY_TRANSITION_PARAMS, transitionParams)
                    }
                }
    }

    @BindView(R.id.thumbnail)
    lateinit var thumbnail: ImageView

    @BindView(R.id.title)
    lateinit var title: TextView

    @BindView(R.id.personalities)
    lateinit var personalities: TextView

    @BindView(R.id.on_air_time_start)
    lateinit var onAirStart: TextView

    @BindView(R.id.on_air_time_end)
    lateinit var onAirEnd: TextView

    @BindView(R.id.open_web_site)
    lateinit var buttonOpenSite: TextView

    @BindView(R.id.share_program)
    lateinit var buttonShare: TextView

    @BindView(R.id.content)
    lateinit var contentWebView: WebView

    @BindView(R.id.ad_area)
    lateinit var adArea: View

    @BindView(R.id.ad_view)
    lateinit var adView: AdView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onCreateView(inflater: LayoutInflater?,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? =
            inflater?.inflate(R.layout.fragment_program_info, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        ButterKnife.bind(this, view)

        if (view.isAttachedToWindow) {
            makeEnterAnimation()?.start()
        }

        (arguments.get(KEY_PROGRAM) as TimeTableResponse.Program).let {
            Glide.with(thumbnail).load(it.image).into(thumbnail)

            title.text = it.title
            personalities.text = it.perfonality

            setupContentWebView(it)
            setupOnAirTime(it)

            if (it.url == null) {
                buttonOpenSite.isEnabled = false
                buttonOpenSite.isFocusableInTouchMode = false
            } else {
                buttonOpenSite.apply {
                    isEnabled = true
                    setOnClickListener { _ ->
                        SugorokuonUtils.launchChromeTab(activity, Uri.parse(it.url))
                    }
                }
            }

            buttonShare.setOnClickListener { v ->
                val shareIntent = Intent(Intent.ACTION_SEND)
                shareIntent.type = "text/plain"

                val startTime = SimpleDateFormat(
                        resources.getString(R.string.date_hhmm),
                        Locale.JAPAN).format(Date(it.start.timeInMillis))
                shareIntent.putExtra(Intent.EXTRA_TEXT,
                        "${it.title} ${startTime} よりonAir : ${it.url ?: ""}"
                )
                startActivity(shareIntent)
            }
        }

        // Handle back key to make animation
        view.setOnKeyListener { _, keyCode, keyEvent ->
            if (keyCode == KeyEvent.KEYCODE_BACK && keyEvent.action == KeyEvent.ACTION_DOWN) {
                dismiss()
                true
            } else {
                false
            }
        }
        view.isFocusableInTouchMode = true
        view.requestFocus()

        // For AdMob
        Runnable { adView.loadAd(AdRequest.Builder().build()) }.run()
    }

    @OnClick(R.id.close_ad)
    fun onAdCloseClicked() {
        adArea.visibility = View.GONE
    }

//    @OnClick(R.id.close)
    fun dismiss() {
        makeExitAnimator()
                ?.apply {
                    addListener(
                            object : Animator.AnimatorListener {
                                override fun onAnimationEnd(p0: Animator?) {
                                    view?.visibility = View.GONE
                                    fragmentManager.popBackStack()
                                }

                                override fun onAnimationRepeat(p0: Animator?) {}
                                override fun onAnimationCancel(p0: Animator?) {}
                                override fun onAnimationStart(p0: Animator?) {}
                            }
                    )
                }
                ?.start()
                ?: fragmentManager.popBackStack()
    }

    private fun makeEnterAnimation(): Animator? =
            (arguments.getSerializable(KEY_TRANSITION_PARAMS) as? TransitionParameters)
                    ?.let {
                        ViewAnimationUtils.createCircularReveal(
                                view,
                                it.tappedPositionX,
                                it.tappedPositionY,
                                0f,
                                it.rippleRadius.toFloat()
                        )

                    }

    private fun makeExitAnimator(): Animator? =
            (arguments.getSerializable(KEY_TRANSITION_PARAMS) as? TransitionParameters)
                    ?.let {
                        ViewAnimationUtils.createCircularReveal(
                                view,
                                it.tappedPositionX,
                                it.tappedPositionY,
                                it.rippleRadius.toFloat(),
                                0f
                        )
                    }

    private fun setupContentWebView(program: TimeTableResponse.Program) {
        val htmlData =
                if (program.description != null) {
                    "${program.description}<BR><BR><BR>"
                } else {
                    ""
                } + (program.info ?: "")

        contentWebView.loadDataWithBaseURL(
                null, htmlData, "text/html", "UTF-8", null
        )
    }

    private fun setupOnAirTime(program: TimeTableResponse.Program) {
        SimpleDateFormat(resources.getString(R.string.date_hhmm), Locale.JAPAN).let {
            onAirStart.text = it.format(Date(program.start.timeInMillis))
            onAirEnd.text = it.format(Date(program.end.timeInMillis))
        }
    }

}