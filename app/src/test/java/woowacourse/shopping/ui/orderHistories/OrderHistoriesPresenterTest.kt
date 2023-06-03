package woowacourse.shopping.ui.orderHistories

import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.slot
import org.junit.Before
import org.junit.Test
import woowacourse.shopping.data.repository.OrderRepository
import woowacourse.shopping.mapper.toUIModel
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
        val fakeOrderHistory = mockk<OrderHistory>()
        val fakeOrderHistoryUIModel = mockk<OrderHistoryUIModel>()

        mockkStatic("woowacourse.shopping.mapper.OrderHistoryMapperKt")
        every { fakeOrderHistory.toUIModel() } answers { fakeOrderHistoryUIModel }

        val successSlot = slot<(Result<List<OrderHistory>>) -> Unit>()
        every {
            orderRepository.getOrderHistoriesNext(capture(successSlot))
        } answers {
            successSlot.captured.invoke(Result.success(listOf(fakeOrderHistory)))
        }

        every { view.showOrderHistories(any()) } answers { }

        // when
        presenter.getOrderHistories()

        // then
        view.showOrderHistories(listOf(fakeOrderHistoryUIModel))
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
