package woowacourse.shopping.ui.orderHistories

import io.mockk.every
import io.mockk.mockk
import org.junit.Before
import org.junit.Test
import woowacourse.shopping.data.repository.OrderRepository
import woowacourse.shopping.model.OrderHistory
import woowacourse.shopping.model.OrderHistoryUIModel

class OrderHistoriesPresenterTest {
    private lateinit var orderRepository: OrderRepository
    private lateinit var presenter: OrderHistoriesContract.Presenter
    private lateinit var view: OrderHistoriesContract.View

    @Before
    fun setUp() {
        orderRepository = mockk()
        view = mockk()
        presenter = OrderHistoriesPresenter(view, orderRepository)
    }

    @Test
    fun `주문 내역 조회`() {
        // given
        val mockOrderHistory = mockk<OrderHistory>()
        val mockOrderHistoryUIModel = mockk<OrderHistoryUIModel>()

        every { orderRepository.getOrderHistoriesNext() }
            .answers { Result.success(listOf(mockOrderHistory)) }

        every { view.showOrderHistories(any()) } answers { }

        // when
        presenter.getOrderHistories()

        // then
        view.showOrderHistories(listOf(mockOrderHistoryUIModel))
    }

    @Test
    fun `주문 상세 페이지로 이동`() {
        // given
        val orderId = 1L
        every { view.navigateToOrderHistory(orderId) } answers { }

        // when
        presenter.navigateToOrderHistory(orderId)

        // then
        view.navigateToOrderHistory(orderId)
    }

    @Test
    fun `상품 상세 페이지로 이동`() {
        // given
        val productId = 1
        every { view.navigateToProductDetail(productId) } answers { }

        // when
        presenter.navigateToProductDetail(productId)

        // then
        view.navigateToProductDetail(productId)
    }
}
