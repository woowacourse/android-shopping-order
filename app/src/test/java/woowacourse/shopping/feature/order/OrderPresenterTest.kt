package woowacourse.shopping.feature.order

import com.example.domain.FixedDiscountPolicy
import com.example.domain.cart.Cart
import com.example.domain.cart.CartProduct
import com.example.domain.order.OrderRepository
import io.mockk.every
import io.mockk.justRun
import io.mockk.mockk
import io.mockk.verify
import org.junit.Before
import org.junit.Test

class OrderPresenterTest {

    private lateinit var view: OrderContract.View
    private lateinit var orderPendingCart: Cart
    private lateinit var orderRepository: OrderRepository
    private lateinit var presenter: OrderContract.Presenter

    @Before
    fun setup() {
        view = mockk()
        orderPendingCart = mockk(relaxed = true)
        orderRepository = mockk()
        presenter = OrderPresenter(
            view = view, orderPendingCart = orderPendingCart, orderRepository = orderRepository
        )
    }

    @Test
    fun `주문할 상품들을 화면에 표시한다`() {
        // given
        justRun { view.setOrderPendingCart(any()) }

        // when
        presenter.loadOrderPendingCart()

        // then
        verify { view.setOrderPendingCart(any()) }
    }

    @Test
    fun `주문할 상품들의 가격 총합을 계산하고 화면을 표시한다`() {
        // given
        val productsSum = 100000
        val discountPrice = 5000
        val finalPrice = 95000

        val fixedDiscountPolicy = mockk<FixedDiscountPolicy>()
        every { orderRepository.requestFetchDiscountPolicy(captureLambda(), any()) } answers {
            val onSuccess = lambda<(FixedDiscountPolicy) -> Unit>().captured
            onSuccess.invoke(fixedDiscountPolicy)
        }

        every { orderPendingCart.getPickedProductsTotalPrice() } returns productsSum
        every { fixedDiscountPolicy.getDiscountPrice(productsSum) } returns 5000
        every { fixedDiscountPolicy.getFinalPrice(productsSum) } returns 95000

        justRun { view.setProductsSum(productsSum) }
        justRun { view.setDiscountPrice(discountPrice) }
        justRun { view.setFinalPrice(finalPrice) }

        // when
        presenter.calculatePrice()

        // then
        verify(exactly = 1) { view.setProductsSum(productsSum) }
        verify(exactly = 1) { view.setDiscountPrice(discountPrice) }
        verify(exactly = 1) { view.setFinalPrice(finalPrice) }
    }

    @Test
    fun `주문상세내역 화면으로 이동한다`() {
        // given
        val orderId = 1L
        every { orderPendingCart.products } returns listOf(
            CartProduct(
                id = 1L,
                productId = 1L,
                productImageUrl = "",
                productName = "",
                productPrice = 0,
                quantity = 1,
                isPicked = false
            )
        )
        every {
            orderRepository.requestAddOrder(
                cartIds = listOf(1L),
                finalPrice = 0,
                captureLambda(),
                any()
            )
        } answers {
            val onSuccess = lambda<(orderId: Long) -> Unit>().captured
            onSuccess.invoke(orderId)
        }

        justRun { view.showOrderDetailPage(orderId) }

        // when
        presenter.navigateToOrderDetail()

        // then
        verify { view.showOrderDetailPage(orderId) }
    }
}
