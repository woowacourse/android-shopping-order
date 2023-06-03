package woowacourse.shopping.feature.order.detail

import com.example.domain.repository.OrderRepository
import io.mockk.justRun
import io.mockk.mockk
import io.mockk.verify
import org.junit.Before
import org.junit.Test

class OrderDetailPresenterTest {

    private lateinit var view: OrderDetailContract.View
    private lateinit var orderRepository: OrderRepository
    private lateinit var presenter: OrderDetailContract.Presenter

    @Before
    fun setUp() {
        view = mockk()
        orderRepository = mockk()
        presenter = OrderDetailPresenter(view, orderId = 1, orderRepository)
    }

    @Test
    fun `간단한 상품 명과 개수와 할인 전 가격 총합을 표시한다`() {
        // given
        val firstProductName = "피자"
        val productsCount = 3
        val totalPrice = 30000
        justRun { view.setProductsSummary(firstProductName, productsCount, totalPrice) }

        // when
        presenter.loadProductsSummary()

        // then
        verify { view.setProductsSummary(firstProductName, productsCount, totalPrice) }
    }

    @Test
    fun `주문일시를 표시한다`() {
        // given
        val orderDate = "2023-05-26 13:30:40"
        justRun { view.setOrderDate(orderDate) }

        // when
        presenter.loadOrderDate()

        // then
        verify { view.setOrderDate(orderDate) }
    }

    @Test
    fun `주문번호를 표시한다`() {
        // given
        val orderId = 1L
        justRun { view.setOrderId(orderId) }

        // when
        presenter.loadOrderId()

        // then
        verify { view.setOrderId(orderId) }
    }

    @Test
    fun `주문한 상품 목록을 표시한다`() {
        // given
        justRun { view.setOrderProducts(orderProducts) }

        // when
        presenter.loadOrderProducts()

        // then
        verify { view.setOrderProducts(orderProducts) }
    }

    @Test
    fun `결제금액들을 표시한다`() {
        // given
        val productsSum = 30000
        val discountPrice = 2000
        val finalPrice = 28000
        justRun { view.setPriceSummary(productsSum, discountPrice, finalPrice) }

        // when
        presenter.loadPriceSummary()

        // then
        verify { view.setPriceSummary(productsSum, discountPrice, finalPrice) }
    }
}
