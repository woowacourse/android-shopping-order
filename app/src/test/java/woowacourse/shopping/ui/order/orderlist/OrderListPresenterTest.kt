package woowacourse.shopping.ui.order.orderlist

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.Before
import org.junit.Test
import woowacourse.shopping.Cart
import woowacourse.shopping.User
import woowacourse.shopping.async
import woowacourse.shopping.domain.order.Order
import woowacourse.shopping.repository.OrderRepository
import woowacourse.shopping.repository.UserRepository
import woowacourse.shopping.ui.order.uistate.OrderUIState

class OrderListPresenterTest {
    private lateinit var view: OrderListContract.View
    private lateinit var presenter: OrderListPresenter
    private lateinit var orderRepository: OrderRepository
    private lateinit var userRepository: UserRepository

    @Before
    fun setUp() {
        view = mockk(relaxed = true)
        orderRepository = mockk()
        userRepository = mockk()
        presenter = OrderListPresenter(view, orderRepository, userRepository)
    }

    @Test
    fun 주문_목록을_불러오고_표시한다() {
        // given
        val orders = listOf(Order(0, Cart(), 0))

        every { userRepository.findCurrent() } returns async(User())
        every { orderRepository.findAll(any()) } returns async(orders)

        // when
        presenter.loadOrders()

        // then
        val expect = listOf(OrderUIState(0, emptyList(), 0))
        verify { view.showOrders(expect) }
    }
}