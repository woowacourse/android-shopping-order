package woowacourse.shopping.ui.orderdetail.contract.presenter

import com.example.domain.model.Order
import com.example.domain.repository.OrderDetailRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import junit.framework.TestCase.assertEquals
import org.junit.Before
import org.junit.Test
import woowacourse.shopping.mapper.toUIModel
import woowacourse.shopping.ui.orderdetail.contract.OrderDetailContract

internal class OrderDetailPresenterTest {

    private lateinit var view: OrderDetailContract.View
    private lateinit var presenter: OrderDetailContract.Presenter
    private lateinit var orderDetailRepository: OrderDetailRepository
    private lateinit var order: Order

    @Before
    fun setUp() {
        view = mockk(relaxed = true)
        orderDetailRepository = mockk(relaxed = true)
        presenter = OrderDetailPresenter(
            view,
            orderDetailRepository,
        )
        order = Order(1L, listOf(), "", "", 1000, 100, 900)
    }

    @Test
    fun `주문 상세 내역을 볼 수 있다`() {
        // given
        every { orderDetailRepository.getById(any()) } returns Result.success(order)
        // when
        presenter.getOrderDetail(1L)
        // then
        assertEquals(order, orderDetailRepository.getById(1L).getOrNull())
        verify { view.setOrderDetail(order.toUIModel()) }
    }
}
