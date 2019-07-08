package tsuyogoro.sugorokuon.recommend

import android.content.Context
import io.reactivex.Completable
import io.reactivex.Flowable
import tsuyogoro.sugorokuon.SugorokuonLog
import tsuyogoro.sugorokuon.dynamicfeature.RecommendModuleDependencyResolver
import tsuyogoro.sugorokuon.recommend.settings.RecommendSettingsRepository
import tsuyogoro.sugorokuon.service.SettingsService
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class RecommendUpdaterImpl(
    context: Context
) : RecommendModuleDependencyResolver.RecommendUpdater {

    @Inject
    lateinit var recommendSettingsRepository: RecommendSettingsRepository

    @Inject
    lateinit var settingsService: SettingsService

    @Inject
    lateinit var recommendTimerService: RecommendTimerService

    @Inject
    lateinit var recommendSearchService: RecommendSearchService

    init {
        DaggerRecommendComponent.builder()
            .recommendInternalModule(RecommendInternalModule(context))
            .build()
            .inject(this)
    }

    override fun observeRecommendConditionAndUpdate(): Completable =
        Flowable
            .merge(
                recommendSettingsRepository.observeRecommendKeywords().doOnNext { SugorokuonLog.d("SugorokuonTopViewModel detected change") },
                recommendSettingsRepository.observeReminderTiming(),
                settingsService.observeAreas()
            )
            .throttleLast(3, TimeUnit.SECONDS)
            .doOnNext {
                recommendTimerService.cancelUpdateRecommendTimer()
            }
            .flatMapSingle {
                recommendSearchService
                    .fetchRecommendPrograms()
                    .doOnSuccess(
                        recommendSearchService::updateRecommendProgramsInDatabase
                    )
            }
            .doOnNext {
                recommendTimerService.setUpdateRecommendTimer()
            }
            .ignoreElements()
}