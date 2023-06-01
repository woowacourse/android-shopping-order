package woowacourse.shopping.feature.order.list

import com.example.domain.model.OrderMinInfoItem
import com.example.domain.repository.OrderRepository
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import org.junit.Before
import org.junit.Test
import woowacourse.shopping.feature.order.OrderFixture
import woowacourse.shopping.model.OrderMinInfoItemUiModel

internal class OrderListPresenterTest {
    private lateinit var view: OrderListContract.View
    private lateinit var orderRepository: OrderRepository
    private lateinit var presenter: OrderListContract.Presenter

    @Before
    fun init() {
        view = mockk()
        orderRepository = mockk()
        presenter = OrderListPresenter(view, orderRepository)
    }

    @Test
    fun `주문 목록 내역을 불러올 수 있다`() {
        // given
        val mockOrderMinInfoItems =
            OrderFixture.getOrderMinInfoItems(listOf(1L, 2L, 3L, 4L, 5L, 6L, 7L, 8L, 9L, 10L))
        val successSlot = slot<(List<OrderMinInfoItem>) -> Unit>()
        every { orderRepository.getAllOrders(capture(successSlot), any()) } answers {
            successSlot.captured(mockOrderMinInfoItems)
        }

        val ordersSlot = slot<List<OrderMinInfoItemUiModel>>()
        every { view.setOrderListItems(capture(ordersSlot)) } just Runs

        // when
        presenter.loadOrderItems()

        // then
        val actual = ordersSlot.captured.map { it.id }
        val expected = listOf(1L, 2L, 3L, 4L, 5L, 6L, 7L, 8L, 9L, 10L)
        assert(actual == expected)
        verify { view.setOrderListItems(any()) }
    }

    @Test
    fun `주문 목록 중 하나를 선택하면 상세 내역을 볼 수 있다`() {
        // given
        val orderId = 3L
        val orderIdSlot = slot<Long>()
        every { view.showOrderDetail(capture(orderIdSlot)) } just Runs

        // when
        presenter.requestOrderDetail(orderId)

        // then
        val actual = orderIdSlot.captured
        val expected = 3L
        assert(actual == expected)
    }
}
