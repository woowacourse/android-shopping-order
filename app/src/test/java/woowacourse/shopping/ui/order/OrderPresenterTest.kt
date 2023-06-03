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

    private val fakeOrder: Order = mockk()
    private val fakeOrderUIModel: OrderUIModel = mockk()

    @Before
    fun setUp() {
        view = mockk()
        orderRepository = mockk()
        val cartIds = listOf(1, 2, 3)
        presenter = OrderPresenter(view, cartIds, orderRepository)
    }

    @Test
    fun `주문 내역을 가져온다`() {
        // given
        mockkStatic("woowacourse.shopping.mapper.OrderMapperKt")

        mockOrderGetOrder()
        every { fakeOrder.toUIModel() } answers { fakeOrderUIModel }
        every { fakeOrder.cartItems } returns listOf()
        justRun { view.showOrder(any()) }

        // when
        presenter.getOrder()

        // then
        verify(exactly = 1) { view.showOrder(fakeOrderUIModel) }
    }

    @Test
    fun `주문을 하면 `() {
        // given
        mockkStatic("woowacourse.shopping.mapper.OrderMapperKt")

        mockOrderPostOrder()
        justRun { view.navigateOrder() }

        // when
        presenter.confirmOrder(1000)

        // then
        verify(exactly = 1) { view.navigateOrder() }
    }

    private fun mockOrderGetOrder() {
        val successSlot = slot<(Result<Order>) -> Unit>()
        every {
            orderRepository.getOrder(any(), capture(successSlot))
        } answers {
            successSlot.captured.invoke(Result.success(fakeOrder))
        }
    }

    private fun mockOrderPostOrder() {
        val successSlot = slot<(Result<Long>) -> Unit>()
        every {
            orderRepository.postOrder(any(), any(), capture(successSlot))
        } answers {
            successSlot.captured.invoke(Result.success(1))
        }
    }
}
