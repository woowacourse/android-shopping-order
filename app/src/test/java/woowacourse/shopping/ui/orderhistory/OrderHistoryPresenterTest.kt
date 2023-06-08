package woowacourse.shopping.ui.orderhistory

import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import io.mockk.verify
import org.junit.Before
import org.junit.Test
import woowacourse.shopping.domain.Order
import woowacourse.shopping.domain.repository.OrderRepository

class OrderHistoryPresenterTest {
    private lateinit var view: OrderHistoryContract.View
    private lateinit var orderRepository: OrderRepository

    @Before
    fun setUp() {
        view = mockk(relaxed = true)
        orderRepository = mockk(relaxed = true)
    }

    @Test
    fun `OrderHistoryPresenter를 생성하면 누적 주문정보를 가져오고 데이터를 화면에 띄워준다`() {
        // given
        every { view.updateOrdersInfo(any()) } just runs
        every { orderRepository.getOrdersInfo(any()) } answers {
            val callback: (List<Order>) -> Unit = arg(0)
            callback(listOf())
        }

        // when
        OrderHistoryPresenter(view, orderRepository)

        // then
        verify(exactly = 1) { view.updateOrdersInfo(any()) }
        verify(exactly = 1) { orderRepository.getOrdersInfo(any()) }
    }
}
