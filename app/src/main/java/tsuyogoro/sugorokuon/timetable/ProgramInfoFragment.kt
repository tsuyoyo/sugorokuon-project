package tsuyogoro.sugorokuon.timetable

import android.animation.Animator
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.CalendarContract
import android.support.v4.app.Fragment
import android.support.v7.app.AlertDialog
import android.view.*
import android.webkit.WebView
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import tsuyogoro.sugorokuon.base.R
import tsuyogoro.sugorokuon.radiko.api.response.SearchResponse
import tsuyogoro.sugorokuon.radiko.api.response.TimeTableResponse
import tsuyogoro.sugorokuon.recommend.RecommendProgram
import tsuyogoro.sugorokuon.utils.SugorokuonUtils
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
                           transitionParams: TransitionParameters?) =
                ProgramInfoFragment().apply {
                    arguments = Bundle().apply {
                        putSerializable(KEY_PROGRAM, program)
                        putSerializable(KEY_TRANSITION_PARAMS, transitionParams)
                    }
                }

        fun createInstance(program: SearchResponse.Program,
                           transitionParams: TransitionParameters? = null) =
                ProgramInfoFragment().apply {
                    arguments = Bundle().apply {
                        putSerializable(KEY_PROGRAM, TimeTableResponse.Program().apply {
                            start = Calendar.getInstance().apply {
                                timeInMillis = program.start.time
                            }
                            end = Calendar.getInstance().apply {
                                timeInMillis = program.end.time
                            }
                            title = program.title
                            url = program.url
                            description = program.description
                            info = program.info
                            perfonality = program.personality
                            image = program.image
                        })
                        putSerializable(KEY_TRANSITION_PARAMS, transitionParams)
                    }
                }

        fun createInstance(program: RecommendProgram,
                           transitionParams: TransitionParameters? = null) =
            ProgramInfoFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(KEY_PROGRAM, TimeTableResponse.Program().apply {
                        start = Calendar.getInstance().apply {
                            timeInMillis = program.start
                        }
                        end = Calendar.getInstance().apply {
                            timeInMillis = program.end
                        }
                        title = program.title
                        url = program.url
                        description = program.description
                        info = program.info
                        perfonality = program.personality
                        image = program.image
                    })
                    putSerializable(KEY_TRANSITION_PARAMS, transitionParams)
                }
            }
    }

    private val thumbnail: ImageView
        get() = view!!.findViewById(R.id.thumbnail)

    private val title: TextView
        get() = view!!.findViewById(R.id.title)

    private val personalities: TextView
        get() = view!!.findViewById(R.id.personalities)

    private val onAirStart: TextView
        get() = view!!.findViewById(R.id.on_air_time_start)

    private val onAirEnd: TextView
        get() = view!!.findViewById(R.id.on_air_time_end)

    private val buttonOpenSite: View
        get() = view!!.findViewById(R.id.open_web_site)

    private val buttonRegisterCalendar: View
        get() = view!!.findViewById(R.id.register_calendar)

    private val contentWebView: WebView
        get() = view!!.findViewById(R.id.content)

    private val adArea: View
        get() = view!!.findViewById(R.id.ad_area)

    private val adView: AdView
        get() = view!!.findViewById(R.id.ad_view)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? =
            inflater.inflate(R.layout.fragment_program_info, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (view.isAttachedToWindow) {
            makeEnterAnimation()?.start()
        }

        (arguments?.get(KEY_PROGRAM) as? TimeTableResponse.Program)?.let {
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

            buttonRegisterCalendar.setOnClickListener { _ ->
                showRegisterCalendarDialog(it)
            }
        }

        // Handle back key to make animation
        view.setOnKeyListener { _, keyCode, keyEvent ->
            val fm = fragmentManager ?: return@setOnKeyListener false

            val isTop = fm.getBackStackEntryAt(
                    fm.backStackEntryCount - 1)?.name == FRAGMENT_TAG

            if (isTop && keyCode == KeyEvent.KEYCODE_BACK && keyEvent.action == KeyEvent.ACTION_DOWN) {
                dismiss()
                true
            } else {
                false
            }
        }
        view.isFocusableInTouchMode = true
        view.requestFocus()

        // For AdMob
        adView.loadAd(AdRequest.Builder().build())

        view.findViewById<Button>(R.id.close_ad)
            .setOnClickListener { adArea.visibility = View.GONE }
    }

    private fun showRegisterCalendarDialog(program: TimeTableResponse.Program) {
        AlertDialog.Builder(context ?: return)
                .setMessage(getString(R.string.register_calendar_confirm))
                .setCancelable(true)
                .setPositiveButton(android.R.string.ok) { _, _ ->
                    val beginTime = Calendar.getInstance()
                    beginTime.time = Date().apply { time = program.start.timeInMillis }

                    val endTime = Calendar.getInstance()
                    endTime.time = Date().apply { time = program.end.timeInMillis }

                    val intent = Intent(Intent.ACTION_INSERT)
                            .setData(CalendarContract.Events.CONTENT_URI)
                            .putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, beginTime.timeInMillis)
                            .putExtra(CalendarContract.EXTRA_EVENT_END_TIME, endTime.timeInMillis)
                            .putExtra(CalendarContract.Events.TITLE, program.title)
                            .putExtra(CalendarContract.Events.AVAILABILITY, CalendarContract.Events.AVAILABILITY_BUSY)
                    startActivity(intent)
                }
                .setNegativeButton(android.R.string.no, null)
                .create()
                .show()
    }

    private fun dismiss() {
        makeExitAnimator()
                ?.apply {
                    addListener(
                            object : Animator.AnimatorListener {
                                override fun onAnimationEnd(p0: Animator?) {
                                    view?.visibility = View.GONE
                                    fragmentManager?.popBackStack()
                                }

                                override fun onAnimationRepeat(p0: Animator?) {}
                                override fun onAnimationCancel(p0: Animator?) {}
                                override fun onAnimationStart(p0: Animator?) {}
                            }
                    )
                }
                ?.start()
                ?: fragmentManager?.popBackStack()
    }

    private fun makeEnterAnimation(): Animator? =
            (arguments?.getSerializable(KEY_TRANSITION_PARAMS) as? TransitionParameters)
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
            (arguments?.getSerializable(KEY_TRANSITION_PARAMS) as? TransitionParameters)
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