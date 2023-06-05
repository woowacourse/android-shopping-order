package woowacourse.shopping.ui.orderhistory.contract.presenter

import com.example.domain.model.Order
import com.example.domain.repository.OrderHistoryRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import junit.framework.TestCase.assertEquals
import org.junit.Before
import org.junit.Test
import woowacourse.shopping.ui.orderhistory.contract.OrderHistoryContract

internal class OrderHistoryPresenterTest {

    private lateinit var view: OrderHistoryContract.View
    private lateinit var presenter: OrderHistoryContract.Presenter
    private lateinit var orderHistoryRepository: OrderHistoryRepository
    private lateinit var order: Order

    @Before
    fun setUp() {
        view = mockk(relaxed = true)
        orderHistoryRepository = mockk(relaxed = true)
        presenter = OrderHistoryPresenter(
            view,
            orderHistoryRepository,
        )
        order = Order(
            1L,
            listOf(),
            "",
            "",
            1000,
            100,
            900,
        )
    }

    @Test
    fun `주문 목록들을 볼 수 있다`() {
        // given
        every { orderHistoryRepository.getOrderHistory() } returns Result.success(listOf(order))
        every { view.setOrderHistory(any()) } answers { nothing }
        // when
        presenter.getOrderHistory()

        // then
        assertEquals(listOf(order), orderHistoryRepository.getOrderHistory().getOrNull())
        verify { view.setOrderHistory(any()) }
    }
}
