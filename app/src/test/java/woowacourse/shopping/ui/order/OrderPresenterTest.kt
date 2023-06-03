package woowacourse.shopping.ui.order

import io.mockk.every
import io.mockk.justRun
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.slot
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

        val successSlot = slot<(Result<Order>) -> Unit>()
        every {
            orderRepository.getOrder(any(), capture(successSlot))
        } answers {
            successSlot.captured.invoke(Result.success(mockOrder))
        }

        every { mockOrder.cartItems } returns listOf()
        justRun { view.showOrder(any()) }

        // when
        presenter.getOrder()

        // then
        verify(exactly = 1) { view.showOrder(mockOrderUIModel) }
    }

    @Test
    fun `주문을 하면 `() {
        // given
        mockkStatic("woowacourse.shopping.mapper.OrderMapperKt")

        val successSlot = slot<(Result<Long>) -> Unit>()
        every {
            orderRepository.postOrder(any(), any(), capture(successSlot))
        } answers {
            successSlot.captured.invoke(Result.success(1))
        }
        justRun { view.navigateOrder() }

        // when
        presenter.confirmOrder(1000)

        // then
        verify(exactly = 1) { view.navigateOrder() }
    }
}
