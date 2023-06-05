package woowacourse.shopping.presentation.orderlist

import io.mockk.every
import io.mockk.justRun
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import org.junit.Before
import org.junit.Test
import woowacourse.shopping.presentation.OrderDetailFixture
import woowacourse.shopping.presentation.mapper.toModel
import woowacourse.shopping.presentation.mapper.toUIModel
import woowacourse.shopping.presentation.view.orderlist.OrderListContract
import woowacourse.shopping.presentation.view.orderlist.OrderListPresenter
import woowacouse.shopping.data.repository.order.OrderRepository
import woowacouse.shopping.model.order.OrderDetail

class OrderListPresenterTest {
    private lateinit var view: OrderListContract.View
    private lateinit var presenter: OrderListContract.Presenter
    private lateinit var orderRepository: OrderRepository

    @Before
    fun setUp() {
        view = mockk(relaxed = true)

        orderRepository = mockk()

        presenter = OrderListPresenter(view, orderRepository)
    }

    @Test
    fun `주문 목록을 불러온다`() {
        // given
        val orders = listOf(OrderDetailFixture.getFixture().toModel())

        val orderListOnSuccess = slot<(List<OrderDetail>) -> Unit>()
        every {
            orderRepository.loadOrderList(
                onFailure = any(),
                onSuccess = capture(orderListOnSuccess)
            )
        } answers {
            orderListOnSuccess.captured(orders)
        }

        justRun { view.showOrderListItemView(orders.map { it.toUIModel() }) }

        // when
        presenter.loadOrderList()

        // then
        verify { view.showOrderListItemView(orders.map { it.toUIModel() }) }
    }
}
