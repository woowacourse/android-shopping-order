package woowacourse.shopping.presentation.orderlist

import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import io.mockk.verify
import org.junit.Before
import org.junit.Test
import woowacourse.shopping.data.order.OrderRepository
import woowacourse.shopping.model.Order
import woowacourse.shopping.presentation.fixture.OrderFixture

class OrderListTest {
    private lateinit var presenter: OrderListContract.Presenter
    private lateinit var view: OrderListContract.View
    private lateinit var orderRepository: OrderRepository

    @Before
    fun setUp() {
        view = mockk()
        orderRepository = mockk()
        presenter = OrderListPresenter(view, orderRepository)
    }

    @Test
    fun `주문 목록을 불러온다`() {
        // given : 상품 상세를 불러올 수 있는 상태다.
        every {
            view.showOrderList(
                orderModels = OrderFixture.getOrderModels(1, 2, 3),
            )
        } just runs

        every {
            orderRepository.loadOrders(
                callback = any(),
            )
        } answers {
            val callback = args[0] as (List<Order>) -> Unit
            callback(OrderFixture.getOrders(1, 2, 3))
        }

        // when : 상품 상세 불러오기 요청을 보낸다.
        presenter.loadOrderList()

        // then : 상품 상세가 화면에 노출된다.
        verify {
            view.showOrderList(
                orderModels = OrderFixture.getOrderModels(1, 2, 3),
            )
        }
    }
}
