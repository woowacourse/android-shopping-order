package woowacourse.shopping.ui.orderHistory

import io.mockk.every
import io.mockk.justRun
import io.mockk.mockk
import io.mockk.mockkStatic
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
        mockkStatic("woowacourse.shopping.mapper.OrderHistoryMapperKt")
        every { mockOrderHistory.toUIModel() } answers { mockOrderHistoryUIModel }
        every { orderRepository.getOrderHistory(any()) }
            .answers { Result.success(mockOrderHistory) }
        justRun { view.setOrderHistory(any()) }

        // when
        presenter.fetchOrderDetail()

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
