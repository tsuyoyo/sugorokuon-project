package tsuyogoro.sugorokuon.recommend.debug

import androidx.lifecycle.Observer
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.text.format.DateFormat
import android.view.View
import android.widget.EditText
import android.widget.Toast
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import tsuyogoro.sugorokuon.SugorokuonLog
import tsuyogoro.sugorokuon.recommend.*
import tsuyogoro.sugorokuon.recommend.reminder.RecommendRemindNotifier
import tsuyogoro.sugorokuon.recommend.settings.RecommendSettingsRepository
import java.util.*
import javax.inject.Inject

class RecommendDebugActivity : AppCompatActivity() {

    @Inject
    lateinit var recommendSearchService: RecommendSearchService

    @Inject
    lateinit var recommendProgramRepository: RecommendProgramRepository

    @Inject
    internal lateinit var recommendRemindNotifier: RecommendRemindNotifier

    @Inject
    internal lateinit var recommendTimerService: RecommendTimerService

    @Inject
    internal lateinit var recommendSettingsRepository: RecommendSettingsRepository

    internal lateinit var recommendConfigPrefs: RecommendConfigPrefs

    private val disposables = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        recommendConfigPrefs = RecommendConfigPrefs.get(this)

        DaggerRecommendComponent.builder()
            .recommendInternalModule(RecommendInternalModule(this))
            .build()
            .inject(this)

        setContentView(R.layout.activity_recommend_debug)

        setupFetchLatestRecommend()
        setupDumpRecommendDatabase()
        setupClearRecommendDatabase()
        setupReminderTest()
        setupRecommendRemindTimerDebug()
        setupRecommendUpdateTimerDebug()
    }

    private fun setupFetchLatestRecommend() {
        findViewById<View>(R.id.fetch_recommend).setOnClickListener {
            recommendSearchService
                .fetchRecommendPrograms()
                .doOnSuccess { SugorokuonLog.d("Success to fetch programs") }
                .doOnSuccess { recommendSearchService.updateRecommendProgramsInDatabase(it) }
                .ignoreElement()
                .subscribeOn(Schedulers.io())
                .doOnComplete { SugorokuonLog.d("Done store DB") }
                .subscribeBy(
                    onError = {
                        SugorokuonLog.d("Failed to fetch latest recommend : ${it.message}")
                    })
                .addTo(disposables)
        }
    }

    private fun setupDumpRecommendDatabase() {
        findViewById<View>(R.id.dump_db).setOnClickListener {

            SugorokuonLog.d("${recommendSettingsRepository.getRecommentKeywords().size}")

            SugorokuonLog.d("Dump recommend database")
            recommendProgramRepository.getRecommendPrograms().forEach {
                val startDate = DateFormat.format("yyyy/MM/dd hh:mm", it.start)
                SugorokuonLog.d("$startDate - ${it.title} (${it.personality})")
            }
            SugorokuonLog.d("-----------------------")
        }
    }

    private fun setupClearRecommendDatabase() {
        findViewById<View>(R.id.clear_db).setOnClickListener {
            recommendProgramRepository.clear()
            Toast.makeText(this@RecommendDebugActivity, "Cleaned DB", Toast.LENGTH_SHORT)
                .show()
        }
    }

    private fun setupReminderTest() {
        findViewById<View>(R.id.notify_reminder).setOnClickListener {
            val programs = recommendProgramRepository.getRecommendPrograms()
            if (programs.isEmpty()) {
                Toast.makeText(this@RecommendDebugActivity, "No program is in DB", Toast.LENGTH_SHORT)
                    .show()
            } else {
                recommendRemindNotifier.notifyReminder(this@RecommendDebugActivity, programs[0])
                    .subscribeOn(Schedulers.io())
                    .observeOn(Schedulers.io())
                    .subscribe()
            }
        }
    }

    // Register some dummy programs on database in specific interval.
    // Then, set timer for the next coming one.
    private fun setupRecommendRemindTimerDebug() {
        val showErrorToast: () -> Unit = {
            Toast.makeText(
                this@RecommendDebugActivity,
                "enter seconds (e.g. 100)",
                Toast.LENGTH_SHORT
            ).show()
        }
        val readIntervalSec: () -> Int = {
            val secInput = findViewById<EditText>(R.id.notify_interval_sec)
            Integer.parseInt(secInput.text.toString())
        }
        val registerDummyPrograms: (Int, RecommendProgram) -> Unit = { intervalSec, referenceProgram ->

            val dummyPrograms = mutableListOf<RecommendProgram>()
            for (i in 1..5) {
                val startTime = Calendar.getInstance().apply {
                    add(Calendar.SECOND, intervalSec * i)
                }.timeInMillis / 1000

                dummyPrograms.add(
                    RecommendProgram(
                        "dummy$i",
                        startTime,
                        startTime + 1000,
                        referenceProgram.stationId,
                        "Dummy program $i",
                        "personality -- dummy $i",
                        referenceProgram.image,
                        referenceProgram.url,
                        "(Description) this is a dummy program on debug view",
                        "(Info) this is a dummy program on debug view"
                    )
                )
            }
            recommendProgramRepository.clear()
            recommendProgramRepository.setRecommendPrograms(dummyPrograms)
        }


        disposables.add(
            recommendProgramRepository.observeRecommendPrograms()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    var message: String = ""
                    if (it != null && it.isNotEmpty()) {
                        SugorokuonLog.d("Detect recommend programs change : this instance = ${this}")
                        message = "Set timer for next"

                        recommendTimerService.setNextRemindTimer(
                            RecommendBroadCastReceiver.REQUEST_CODE_REMIND_ON_AIR)
                    } else {
                        SugorokuonLog.d("Detect recommend programs empty : this instance = ${this}")
                        message = "Repository has been empty"
                    }
                    Toast.makeText(
                        this@RecommendDebugActivity,
                        message,
                        Toast.LENGTH_SHORT
                    ).show()
                }
        )

        findViewById<View>(R.id.notify_set).setOnClickListener {
            if (recommendProgramRepository.getRecommendPrograms().isEmpty()) {
                Toast.makeText(
                    this@RecommendDebugActivity,
                    "Try after fetching program",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            try {
                val interval = readIntervalSec.invoke()
                registerDummyPrograms.invoke(
                    interval,
                    recommendProgramRepository.getRecommendPrograms()[0]
                )
            } catch (e: Exception) {
                showErrorToast.invoke()
            }
        }
    }

    fun setupRecommendUpdateTimerDebug() {
        val showErrorToast: () -> Unit = {
            Toast.makeText(
                this@RecommendDebugActivity,
                "enter seconds (e.g. 100)",
                Toast.LENGTH_SHORT
            ).show()
        }
        val readIntervalSec: () -> Int = {
            val secInput = findViewById<EditText>(R.id.recommend_update_interval)
            Integer.parseInt(secInput.text.toString())
        }
        findViewById<View>(R.id.recommend_update_set).setOnClickListener {
            try {
                val interval = readIntervalSec.invoke()
                if (interval > 10) {
                    recommendConfigPrefs.putUpdateIntervalForDebugInSec(interval.toLong())
                    recommendTimerService.setUpdateRecommendTimer(
                        RecommendBroadCastReceiver.REQUEST_CODE_UPDATE_RECOMMEND)

                    Toast.makeText(
                        this@RecommendDebugActivity,
                        "Set recommend update timer",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    Toast.makeText(
                        this@RecommendDebugActivity,
                        "Set longer than 10 sec for interval",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } catch (e: Exception) {
                showErrorToast.invoke()
            }
        }
        findViewById<View>(R.id.recommend_update_cancel).setOnClickListener {
            recommendTimerService.cancelUpdateRecommendTimer(
                RecommendBroadCastReceiver.REQUEST_CODE_UPDATE_RECOMMEND)
        }
        findViewById<View>(R.id.recommend_update_configure_reset).setOnClickListener {
            recommendConfigPrefs.removeUpdateIntervalForDebugInSec()
        }
    }
}