
package woowacourse.shopping.feature.order.detail

import com.example.domain.Product
import com.example.domain.order.Order
import com.example.domain.order.OrderProduct
import com.example.domain.repository.OrderRepository
import io.mockk.justRun
import io.mockk.mockk
import org.junit.Before
import org.junit.Test
import woowacourse.shopping.model.mapper.toUi
import woowacourse.shopping.util.AbstractTest

class OrderDetailPresenterTest : AbstractTest() {

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
    fun `상품 아이디를 이용해 상품의 정보를 가져와 화면에 표시한다`() {
        val order: Order = createOrder()
        // given
        justRun { orderRepository.requestFetchOrderById(any(), any(), any()) }
        justRun { view.setViewFixedContents(any()) }
        justRun { view.setProductsSummary(any(), any(), any()) }
        justRun { view.setOrderDate(any()) }
        justRun { view.setOrderNumber(any()) }
        justRun { view.setOrderProducts(any()) }

        // when
        presenter.loadOrderInformation()

        // then
        view.setViewFixedContents(order.toUi())
        view.setProductsSummary(
            firstProductName = order.orderProducts[0].product.name,
            productsCount = order.orderProducts.count(), originalPrice = order.originalPrice
        )
        view.setOrderDate(order.orderDate)
        view.setOrderNumber(order.id)
        view.setOrderProducts(order.orderProducts)
    }

//    @Test
//    fun `상품 아이디를 받아오지 못하였다면 오류 문구를 띄우고 화면을 종료한다`() {
//        // given
//        presenter = OrderDetailPresenter(view, null, orderRepository)
//
//        // when
//        presenter.loadOrderInformation()
//
//        // then
//        view.showAccessError()
//        view.closeOrderDetail()
//    }

    @Test
    fun `상품 정보를 받아오지 못하였다면 오류 문구를 띄우고 화면을 종료한다`() {
        // given
        justRun { orderRepository.requestFetchOrderById(any(), any(), any()) }
        justRun { view.showAccessError() }
        justRun { view.closeOrderDetail() }

        // when
        presenter.loadOrderInformation()

        // then
        view.showAccessError()
        view.closeOrderDetail()
    }

    private fun createOrder(
        id: Long = 1,
        originalPrice: Int = 1_000,
        discountPrice: Int = 0,
        finalPrice: Int = 1_000,
        orderDate: String = "2023-05-26 13:30:40",
        orderProducts: List<OrderProduct> = createOrderProducts()
    ): Order {
        return Order(
            id = id,
            originalPrice = originalPrice,
            discountPrice = discountPrice,
            finalPrice = finalPrice,
            orderDate = orderDate,
            orderProducts = orderProducts
        )
    }

    private fun createOrderProducts(): List<OrderProduct> {
        return listOf(
            OrderProduct(
                id = 1,
                quantity = 1,
                product = Product(id = 1, imageUrl = "", name = "aaa", price = 1_000)
            )
        )
    }
}
