package woowacourse.shopping.presentation.myorder.detail

import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import org.junit.Before
import org.junit.Test
import woowacourse.shopping.OrderDetail
import woowacourse.shopping.OrderProduct
import woowacourse.shopping.OrderProducts
import woowacourse.shopping.Point
import woowacourse.shopping.Price
import woowacourse.shopping.Product
import woowacourse.shopping.presentation.mapper.toPresentation
import woowacourse.shopping.presentation.model.OrderProductModel
import woowacourse.shopping.presentation.model.ProductModel
import woowacourse.shopping.repository.OrderRepository
import java.time.LocalDateTime

class MyOrderDetailPresenterTest {
    private lateinit var view: MyOrderDetailContract.View
    private lateinit var orderRepository: OrderRepository
    private lateinit var presenter: MyOrderDetailPresenter

    @Before
    fun setUp() {
        view = mockk(relaxed = true)
        orderRepository = mockk(relaxed = true)
        presenter = MyOrderDetailPresenter(view, orderRepository, 1)
    }

    private fun makeProduct(id: Int): Product = Product(id, "", "", Price(100))

    private fun makeOrderDetail(id: Int): OrderDetail {
        return OrderDetail(
            orderId = id,
            totalPrice = Price(value = 100),
            spendPoint = Point(value = 100),
            spendPrice = Price(value = 100),
            orderDate = LocalDateTime.MAX,
            orderItems = OrderProducts(list = listOf(OrderProduct(makeProduct(id), 1)))
        )
    }

    @Test
    fun `주문 상세를 불러와, 주문한 상품들과, 결제 정보를 보여준다`() {
        // given
        val slot = slot<(OrderDetail?) -> Unit>()
        val orderDetail = makeOrderDetail(1)
        every { orderRepository.getOrderDetail(1, capture(slot)) } answers {
            slot.captured.invoke(orderDetail)
        }
        // when
        presenter.loadOrderDetail()
        // then
        verify { view.setOrderProducts(orderDetail.orderItems.toPresentation().list) }
        verify { view.setPaymentInfo(orderDetail.toPresentation()) }
    }

    @Test
    fun `각 주문 상품의 금액을 보여준다`() {
        // given
        val orderProductModel = OrderProductModel(
            product = ProductModel(
                id = 0,
                imageUrl = "",
                name = "",
                price = 1000
            ),
            count = 3
        )
        // when
        presenter.updateProductPrice(orderProductModel)
        // then
        view.setProductPrice(3000)
    }
}
