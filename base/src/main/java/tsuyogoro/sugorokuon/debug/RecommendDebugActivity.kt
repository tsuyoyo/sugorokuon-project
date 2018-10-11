package tsuyogoro.sugorokuon.debug

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.format.DateFormat
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.Toast
import io.reactivex.schedulers.Schedulers
import tsuyogoro.sugorokuon.SugorokuonApplication
import tsuyogoro.sugorokuon.notification.RecommendRemindTimerSubmitter
import tsuyogoro.sugorokuon.recommend.R
import tsuyogoro.sugorokuon.recommend.RecommendModule
import tsuyogoro.sugorokuon.recommend.RecommendSearchService
import tsuyogoro.sugorokuon.recommend.RecommendProgramsDao
import tsuyogoro.sugorokuon.recommend.reminder.RecommendRemindNotifier
import java.util.*
import javax.inject.Inject

class RecommendDebugActivity : AppCompatActivity() {

    @Inject
    lateinit var recommendSearchService: RecommendSearchService

    @Inject
    lateinit var recommendProgramsDao: RecommendProgramsDao

    @Inject
    lateinit var recommendRemindNotifier: RecommendRemindNotifier

    @Inject
    lateinit var remindTimerSubmitter: RecommendRemindTimerSubmitter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recommend_debug)

        SugorokuonApplication.application(this)
            .appComponent()
            .debugSubComponent(RecommendModule())
            .inject(this)

        findViewById<View>(R.id.fetch_recommend).setOnClickListener {
            recommendSearchService.fetchRecommendPrograms()
                .subscribeOn(Schedulers.io())
                .doOnComplete { Log.d("TestTestTest", "Complete") }
                .doOnError { Log.d("TestTestTest", "Error - ${it.message}") }
                .subscribe( { }, { })
        }


        findViewById<View>(R.id.dump_db).setOnClickListener {
            Log.d("TestTestTest", "Dump recommend database")
            recommendProgramsDao.getAll().forEach {
                val startDate = DateFormat.format("yyyy/MM/dd hh:mm", it.start)
                Log.d("TestTestTest", "$startDate - ${it.title} (${it.personality})")
            }
            Log.d("TestTestTest", "-----------------------")
        }

        findViewById<View>(R.id.clear_db).setOnClickListener {
            recommendProgramsDao.clearTable()
            Toast.makeText(this@RecommendDebugActivity, "Cleaned DB", Toast.LENGTH_SHORT)
                .show()
        }

        findViewById<View>(R.id.notify_reminder).setOnClickListener {
            val programs = recommendProgramsDao.getAll()
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


        setupNotifyTimerDebug()
    }

    private fun setupNotifyTimerDebug() {
        findViewById<View>(R.id.notify_set).setOnClickListener {
            val secInput = findViewById<EditText>(R.id.notify_after_sec)
            try {
                val sec = Integer.parseInt(secInput.text.toString())
                val setTime = Calendar.getInstance().timeInMillis + sec * 1000

                remindTimerSubmitter.setTimer(setTime)

                Toast.makeText(
                    this@RecommendDebugActivity,
                    "Set timer after $sec seconds",
                    Toast.LENGTH_SHORT
                ).show()

            } catch (e: Exception) {
                Toast.makeText(
                    this@RecommendDebugActivity,
                    "enter seconds (e.g. 100)",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
}