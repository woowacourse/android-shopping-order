package woowacourse.shopping.ui.order

import io.mockk.every
import io.mockk.justRun
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.verify
import org.junit.Before
import org.junit.Test
import woowacourse.shopping.data.repository.OrderRepository
import woowacourse.shopping.mapper.toUIModel
import woowacourse.shopping.model.Order
import woowacourse.shopping.model.OrderUIModel

class OrderPresenterTest {
    private lateinit var orderRepository: OrderRepository
    private lateinit var view: OrderContract.View

    private lateinit var presenter: OrderPresenter

    @Before
    fun setUp() {
        view = mockk()
        orderRepository = mockk()
        presenter = OrderPresenter(view, mockk(), orderRepository)
    }

    @Test
    fun `주문 내역을 가져온다`() {
        // given
        val mockOrder: Order = mockk()
        val mockOrderUIModel: OrderUIModel = mockk()

        mockkStatic("woowacourse.shopping.mapper.OrderMapperKt")
        every { mockOrder.toUIModel() } answers { mockOrderUIModel }
        every { orderRepository.getOrder(any()) } answers { Result.success(mockOrder) }
        every { mockOrder.cartItems } returns listOf()
        justRun { view.setOrder(any()) }

        // when
        presenter.fetchOrder()

        // then
        verify(exactly = 1) { view.setOrder(mockOrderUIModel) }
    }

    @Test
    fun `주문을 하면 메인 화면으로 간다`() {
        // given
        every { orderRepository.postOrder(any(), any()) } answers { Result.success(1) }
        justRun { view.navigateToOrderConfirmation() }

        // when
        presenter.processToOrderConfirmation(1000)

        // then
        verify(exactly = 1) { view.navigateToOrderConfirmation() }
    }
}
