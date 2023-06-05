package woowacourse.shopping.presentation.orderdetail

import io.mockk.every
import io.mockk.justRun
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import org.junit.Before
import org.junit.Test
import woowacourse.shopping.presentation.OrderDetailFixture
import woowacourse.shopping.presentation.mapper.toModel
import woowacourse.shopping.presentation.mapper.toUIModel
import woowacourse.shopping.presentation.view.orderdetail.OrderDetailContract
import woowacourse.shopping.presentation.view.orderdetail.OrderDetailPresenter
import woowacouse.shopping.data.repository.order.OrderRepository
import woowacouse.shopping.model.order.OrderDetail

class OrderDetailPresenterTest {
    private lateinit var presenter: OrderDetailContract.Presenter
    private lateinit var view: OrderDetailContract.View
    private lateinit var orderRepository: OrderRepository

    @Before
    fun setUp() {
        view = mockk(relaxed = true)

        orderRepository = mockk()

        presenter = OrderDetailPresenter(
            view,
            orderRepository
        )
    }

    @Test
    fun `주문 정보를 확인할 수 있다`() {
        // given
        val orderId = 1L
        val orderDetail = OrderDetailFixture.getFixture().toModel()
        val totalPrice = orderDetail.products.sumOf { it.count * it.product.price }

        // 주문 정보를 조회한다
        val loadOnSuccess = slot<(OrderDetail) -> Unit>()
        every {
            orderRepository.loadOrder(
                orderId,
                onFailure = any(),
                onSuccess = capture(loadOnSuccess)
            )
        } answers {
            loadOnSuccess.captured(orderDetail)
        }

        justRun { view.showOrderProductItemView(orderDetail.products.map { it.toUIModel() }) }
        justRun { view.showOrderDateView(orderDetail.orderDateTime) }
        justRun { view.showOrderPriceView(totalPrice) }
        justRun { view.showUsedPointView(orderDetail.usedPoint) }
        justRun { view.showSavedPointView(orderDetail.savedPoint) }
        justRun { view.showTotalPriceView(totalPrice - orderDetail.usedPoint) }

        // when
        presenter.loadOrderDetail(1L)

        // then
        verify { view.showOrderProductItemView(orderDetail.products.map { it.toUIModel() }) }
        verify { view.showOrderDateView(orderDetail.orderDateTime) }
        verify { view.showOrderPriceView(totalPrice) }
        verify { view.showUsedPointView(orderDetail.usedPoint) }
        verify { view.showSavedPointView(orderDetail.savedPoint) }
        verify { view.showTotalPriceView(totalPrice - orderDetail.usedPoint) }
    }
}
