package woowacourse.shopping.feature.order.detail

import com.example.domain.datasource.productsDatasource
import com.example.domain.model.OrderDetail
import com.example.domain.model.OrderProduct
import com.example.domain.model.Price
import com.example.domain.repository.OrderRepository
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import org.junit.Before
import org.junit.Test
import woowacourse.shopping.mapper.toPresentation
import woowacourse.shopping.model.OrderProductUiModel
import java.time.LocalDateTime

internal class OrderDetailPresenterTest {
    private lateinit var view: OrderDetailContract.View
    private lateinit var orderRepository: OrderRepository
    private lateinit var presenter: OrderDetailContract.Presenter

    @Before
    fun init() {
        view = mockk()
        orderRepository = mockk()
    }

    @Test
    fun `주문 id로 상세 내역을 불러오면, 구입한 상품들 정보가 보이고, 주문날짜가 보이고, 할인적용금액에 대한 정보가 보인다`() {
        // given
        val orderId = 3L
        presenter = OrderDetailPresenter(view, orderRepository, orderId)
        val mockOrderDetail = OrderDetail(
            Price(50000), Price(45000), LocalDateTime.now(),
            listOf(
                OrderProduct(productsDatasource[0], 1),
                OrderProduct(productsDatasource[1], 1)
            )
        )
        val successSlot = slot<(OrderDetail) -> Unit>()
        every { orderRepository.getOrderDetailById(orderId, capture(successSlot), any()) } answers {
            successSlot.captured(mockOrderDetail)
        }
        val slot = slot<List<OrderProductUiModel>>()
        every { view.setOrderProductsInfo(capture(slot)) } just Runs
        every { view.setOrderDate(any()) } just Runs
        every { view.setOrderPaymentInfo(any(), any(), any()) } just Runs

        // when
        presenter.loadOrderInfo()

        // then
        val actual = slot.captured
        val expected = mockOrderDetail.toPresentation().orderItems
        assert(actual == expected)
        verify { view.setOrderProductsInfo(any()) }

        // and
        verify { view.setOrderDate(any()) }

        // and
        verify { view.setOrderPaymentInfo(any(), any(), any()) }
    }
}
