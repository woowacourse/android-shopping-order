package woowacourse.shopping.ui.order.history

import io.mockk.every
import io.mockk.just
import io.mockk.justRun
import io.mockk.mockk
import io.mockk.runs
import io.mockk.verify
import org.junit.Before
import org.junit.Test
import woowacourse.shopping.domain.model.OrderResponse
import woowacourse.shopping.domain.repository.OrderProductRepository

internal class OrderHistoryPresenterTest {
    private lateinit var presenter: OrderHistoryContract.Presenter
    private lateinit var view: OrderHistoryContract.View
    private lateinit var orderRepository: OrderProductRepository

    @Before
    fun setUp() {
        view = mockk(relaxed = true)
        orderRepository = mockk(relaxed = true)
        presenter = OrderHistoryPresenter(view, orderRepository)
    }

    @Test
    fun 주문했던_주문_내역들을_확인할_수_있다() {
        // given
        every { view.showOrderedProducts(any()) } just runs
        every {
            orderRepository.requestOrders(any(), any())
        } answers {
            val callback = args[0] as (List<OrderResponse>) -> Unit
            callback(listOf())
        }

        // when
        presenter.loadOrderedProducts()

        // then
        verify(exactly = 1) { view.showOrderedProducts(any()) }
    }

    @Test
    fun 사용자가_뒤로가기를_누르면_메인_화면으로_돌아간다() {
        // given
        justRun { view.navigateToHome() }

        // when
        presenter.navigateToHome(android.R.id.home)

        // then
        verify(exactly = 1) { view.navigateToHome() }
    }
}
