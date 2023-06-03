package woowacourse.shopping.ui.orderHistory

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
import woowacourse.shopping.model.OrderHistory
import woowacourse.shopping.model.OrderHistoryUIModel

class OrderHistoryPresenterTest {
    private lateinit var orderRepository: OrderRepository
    private lateinit var presenter: OrderHistoryPresenter
    private lateinit var view: OrderHistoryContract.View

    @Before
    fun setUp() {
        orderRepository = mockk()
        view = mockk()
        presenter = OrderHistoryPresenter(view, orderRepository, 1)
    }

    @Test
    fun `주문 내역 상세 조회`() {
        // given
        val mockOrderHistory: OrderHistory = mockk()
        val mockOrderHistoryUIModel: OrderHistoryUIModel = mockk()

        val successSlot = slot<(Result<OrderHistory>) -> Unit>()
        every {
            orderRepository.getOrderHistory(any(), capture(successSlot))
        } answers {
            successSlot.captured.invoke(Result.success(mockOrderHistory))
        }

        mockkStatic("woowacourse.shopping.mapper.OrderHistoryMapperKt")
        every { mockOrderHistory.toUIModel() } answers { mockOrderHistoryUIModel }

        justRun { view.setOrderHistory(any()) }

        // when
        presenter.getOrderDetail()

        // then
        verify(exactly = 1) { view.setOrderHistory(mockOrderHistoryUIModel) }
    }

    @Test
    fun `상품 상세 페이지로 이동`() {
        // given
        val productId = 1
        justRun { view.navigateToProductDetail(productId) }

        // when
        presenter.navigateToProductDetail(productId)

        // then
        verify(exactly = 1) { view.navigateToProductDetail(productId) }
    }
}
