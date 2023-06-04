package woowacourse.shopping.feature.orderdetail

import com.example.domain.model.OrderDetail
import com.example.domain.repository.OrderRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.Before
import org.junit.Test
import woowacourse.shopping.mapper.toPresentation

class OrderDetailPresenterTest {
    private lateinit var view: OrderDetailContract.View
    private lateinit var presenter: OrderDetailContract.Presenter
    private lateinit var orderRepository: OrderRepository

    @Before
    fun setUp() {
        view = mockk(relaxed = true)
        orderRepository = mockk(relaxed = true)
        presenter = OrderDetailPresenter(view, orderRepository)
    }

    @Test
    fun `화면에 주문 상세 정보이 나타난다`() {
        // Given: orderId 를 이용해 상품을 받아올 수 있는 상태이다.
        val orderId: Long = 1
        val fakeOrderDetail = OrderDetail(0, 0, "", listOf())
        every {
            orderRepository.getOrderDetail(
                orderId = 1,
                onSuccess = any(),
                onFailure = any(),
            )
        } answers {
            secondArg<(OrderDetail) -> Unit>().invoke(fakeOrderDetail)
        }

        // When: orderId 를 이용해 주문 상세 정보를 받아온다.
        presenter.requestOrderDetail(orderId)

        // Then: 주문 상세 정보를 띄운다.
        verify { view.showOrderDetail(fakeOrderDetail.toPresentation()) }
    }
}
