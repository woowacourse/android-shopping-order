package woowacourse.shopping.ui.order.orderdetail

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.Before
import org.junit.Test
import woowacourse.shopping.Cart
import woowacourse.shopping.User
import woowacourse.shopping.async
import woowacourse.shopping.domain.order.Order
import woowacourse.shopping.domain.order.Payment
import woowacourse.shopping.repository.OrderRepository
import woowacourse.shopping.repository.UserRepository
import woowacourse.shopping.ui.order.uistate.OrderUIState
import woowacourse.shopping.ui.order.uistate.PaymentUIState

class OrderDetailPresenterTest {
    private lateinit var view: OrderDetailContract.View
    private lateinit var presenter: OrderDetailPresenter
    private lateinit var orderRepository: OrderRepository
    private lateinit var userRepository: UserRepository

    @Before
    fun setUp() {
        view = mockk(relaxed = true)
        orderRepository = mockk()
        userRepository = mockk()
        presenter = OrderDetailPresenter(view, orderRepository, userRepository)
    }

    @Test
    fun 주문을_불러오고_표시한다() {
        // given
        val order = Order(0, Cart(), 0)
        val payment = Payment(emptyList())

        every { userRepository.findCurrent() } returns async(User())
        every { orderRepository.findById(any(), any()) } returns async(order)
        every { orderRepository.findDiscountPolicy(any(), any()) } returns async(payment)

        // when
        presenter.loadOrder(0)

        // then
        val orderExpect = OrderUIState(0, emptyList(), 0)
        val paymentExpect = PaymentUIState(emptyList())

        verify { view.showOrder(orderExpect, paymentExpect) }
    }

}