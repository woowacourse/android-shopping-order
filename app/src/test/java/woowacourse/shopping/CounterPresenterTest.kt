package woowacourse.shopping

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.Before
import org.junit.Test
import woowacourse.shopping.presentation.common.CounterContract
import woowacourse.shopping.presentation.common.CounterListener
import woowacourse.shopping.presentation.common.CounterPresenter

class CounterPresenterTest {
    private lateinit var view: CounterContract.View
    private lateinit var presenter: CounterContract.Presenter

    @Before
    fun setUp() {
        view = mockk(relaxed = true)
        presenter = CounterPresenter(view, counterListener)
    }

    private val counterListener: CounterListener = object : CounterListener {
        override fun onPlus(count: Int) {
        }

        override fun onMinus(count: Int) {
        }
    }

    @Test
    fun `+버튼을_누르면_1을_더한_값을_표시한다`() {
        // given
        presenter = CounterPresenter(view, counterListener)
        // when
        presenter.plusCount()
        // then
        view.setCountText(2)
    }

    @Test
    fun `-버튼을_누르면_1을_감소한_값을_표시한다`() {
        // given
        presenter = CounterPresenter(view, counterListener)
        // when
        presenter.minusCount()
        // then
        view.setCountText(0)
    }

    @Test
    fun `Counter 에 특정 값을 표시할 수 있다`() {
        // given
        presenter = CounterPresenter(view, counterListener)
        // when
        presenter.updateCount(3)
        // then
        view.setCountText(3)
    }

    @Test
    fun `Counter의 값이 0이라면 Counter가 안보인다`() {
        // given
        presenter = CounterPresenter(view, counterListener, initCount = 0, minimumCount = 0)
        // when
        presenter.checkCounterVisibility()
        // then
        verify { view.setCounterVisibility(false) }
    }

    @Test
    fun `Counter의 값이 0이 아니라면 Counter가 보인다`() {
        // given
        presenter = CounterPresenter(view, counterListener, initCount = 1)
        // when
        presenter.checkCounterVisibility()
        // then
        every { view.setCounterVisibility(true) }
    }
}
