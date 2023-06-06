package woowacourse.shopping.presentation.orderdetail

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

class OrderDetailPresenterTest {
    private lateinit var presenter: OrderDetailContract.Presenter
    private lateinit var view: OrderDetailContract.View
    private lateinit var orderRepository: OrderRepository

    @Before
    fun setUp() {
        view = mockk()
        orderRepository = mockk()
        presenter = OrderDetailPresenter(view, orderRepository)
    }

    @Test
    fun `주문 상세를 불러온다`() {
        // given : 상품 상세를 불러올 수 있는 상태다.
        every {
            view.showOrderInfo(
                orderModel = OrderFixture.getOrderModel(1),
            )
        } just runs

        every {
            orderRepository.loadOrder(
                orderId = 1,
                callback = any(),
            )
        } answers {
            val callback = args[1] as (Order) -> Unit
            callback(OrderFixture.getOrder(1))
        }

        // when : 상품 상세 불러오기 요청을 보낸다.
        presenter.loadOrderInfo(1)

        // then : 상품 상세가 화면에 노출된다.
        verify {
            view.showOrderInfo(
                orderModel = OrderFixture.getOrderModel(1),
            )
        }
    }
}
