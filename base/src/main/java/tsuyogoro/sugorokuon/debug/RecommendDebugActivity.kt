package tsuyogoro.sugorokuon.debug

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.format.DateFormat
import android.util.Log
import android.view.View
import android.widget.Toast
import io.reactivex.schedulers.Schedulers
import tsuyogoro.sugorokuon.SugorokuonApplication
import tsuyogoro.sugorokuon.recommend.R
import tsuyogoro.sugorokuon.recommend.RecommendModule
import tsuyogoro.sugorokuon.recommend.RecommendSearchService
import tsuyogoro.sugorokuon.recommend.database.RecommendProgramsDao
import javax.inject.Inject

class RecommendDebugActivity : AppCompatActivity() {

    @Inject
    lateinit var recommendSearchService: RecommendSearchService

    @Inject
    lateinit var recommendProgramsDao: RecommendProgramsDao

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
    }
}