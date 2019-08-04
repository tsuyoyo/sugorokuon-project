package tsuyogoro.sugorokuon.timetable

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.nhaarman.mockito_kotlin.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.junit.Rule
import tsuyogoro.sugorokuon.rx.SchedulerProvider
import tsuyogoro.sugorokuon.test.util.SchedulerProviderForTest
import tsuyogoro.sugorokuon.repository.TimeTableRepository


@RunWith(JUnit4::class)
class OneDayTimeTableViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var timeTableRepository: TimeTableRepository

    private lateinit var target: ProgramTableViewModel

    private lateinit var testSchedulerProvider: SchedulerProvider

    @Before
    fun setup() {
        timeTableRepository = mock {  }
        testSchedulerProvider = SchedulerProviderForTest()
    }

    @Test
    fun `trial liveData test`() {
        target = ProgramTableViewModel(timeTableRepository)

        val mockObserver = mock<Observer<String>> {  }
        target.observeText().observeForever(mockObserver)

        verify(mockObserver, never()).onChanged(any())

        target.updateTest("aaa")
        verify(mockObserver, times(1)).onChanged("aaa")

        target.updateTest("bbb")
        verify(mockObserver, times(1)).onChanged("bbb")
    }
}