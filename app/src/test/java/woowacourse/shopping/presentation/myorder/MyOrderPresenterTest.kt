package woowacourse.shopping.presentation.myorder

import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import org.junit.Before
import org.junit.Test
import woowacourse.shopping.Order
import woowacourse.shopping.Price
import woowacourse.shopping.presentation.mapper.toPresentation
import woowacourse.shopping.repository.OrderRepository
import java.time.LocalDateTime

class MyOrderPresenterTest {
    private lateinit var view: MyOrderContract.View
    private lateinit var orderRepository: OrderRepository
    private lateinit var presenter: MyOrderPresenter

    @Before
    fun setUp() {
        view = mockk(relaxed = true)
        orderRepository = mockk(relaxed = true)
        presenter = MyOrderPresenter(view, orderRepository)
    }

    private fun makeOrder(id: Int): Order {
        return Order(
            firstProductName = "",
            totalCount = 1,
            orderId = id,
            imageUrl = "",
            orderDate = LocalDateTime.MAX,
            spendPrice = Price(value = 1000)
        )
    }

    @Test
    fun `모든 나의 주문 목록을 보여준다`() {
        // given
        val slot = slot<(List<Order>) -> Unit>()
        val orders = List(10) { makeOrder(it) }
        every { orderRepository.getAllOrders(capture(slot)) } answers {
            slot.captured.invoke(orders)
        }
        // when
        presenter.loadOrders()
        // then
        verify { view.setOrders(orders.map { it.toPresentation() }) }
    }

    @Test
    fun `주문을 클릭하면, 주문 상세를 보여준다`() {
        // given
        val orderModel = makeOrder(1).toPresentation()
        // when
        presenter.showOrderDetail(orderModel)
        // then
        verify { view.navigateToOrderDetail(1) }
    }
}
