package woowacourse.shopping

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import io.mockk.mockk
import io.mockk.verify
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import woowacourse.shopping.presentation.common.CounterContract
import woowacourse.shopping.presentation.common.CounterListener
import woowacourse.shopping.presentation.common.CounterPresenter

class CounterPresenterTest {
    private lateinit var view: CounterContract.View
    private lateinit var presenter: CounterContract.Presenter
    private val counterListener = object : CounterListener {
        override fun onPlus(count: Int) {
        }

        override fun onMinus(count: Int) {
        }
    }

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setUp() {
        view = mockk(relaxed = true)
        presenter = CounterPresenter(view, counterListener)
    }

    @Test
    fun `+버튼을_누르면_1을_더한_값을_표시한다`() {
        // given
        presenter = CounterPresenter(view, counterListener)
        // when
        presenter.plusCount()
        // then
        verify {
            view.setCountText(2)
        }
    }

    @Test
    fun `-버튼을_누르면_1을_감소한_값을_표시한다`() {
        // given
        presenter = CounterPresenter(view, counterListener, minimumCount = 0)
        // when
        presenter.minusCount()
        // then
        verify {
            view.setCountText(0)
        }
    }

    @Test
    fun `Counter 에 특정 값을 표시할 수 있다`() {
        // given
        presenter = CounterPresenter(view, counterListener)
        // when
        presenter.updateCount(3)
        // then
        verify {
            view.setCountText(3)
        }
    }

    @Test
    fun `Counter의 값이 0이라면 Counter가 안보인다`() {
        // given
        presenter = CounterPresenter(view, counterListener, minimumCount = 0)
        // when
        presenter.updateCount(0)
        presenter.checkCounterVisibility()
        // then
        verify { view.setCounterVisibility(false) }
    }
//
    @Test
    fun `Counter의 값이 0이 아니라면 Counter가 보인다`() {
        // given
        presenter = CounterPresenter(view, counterListener)
        // when
        presenter.checkCounterVisibility()
        // then
        verify { view.setCounterVisibility(true) }
    }
}
