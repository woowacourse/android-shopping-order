package woowacourse.shopping.feature.order

import com.example.domain.Cart
import com.example.domain.FixedDiscountPolicies
import com.example.domain.FixedDiscountPolicy
import com.example.domain.repository.OrderRepository
import io.mockk.every
import io.mockk.justRun
import io.mockk.mockk
import io.mockk.verify
import org.junit.Before
import org.junit.Test

class OrderPresenterTest {

    private lateinit var view: OrderContract.View
    private lateinit var orderProducts: Cart
    private lateinit var orderRepository: OrderRepository
    private lateinit var presenter: OrderContract.Presenter

    @Before
    fun setup() {
        view = mockk()
        orderProducts = mockk()
        orderRepository = mockk()
        presenter = OrderPresenter(
            view = view, orderProducts = orderProducts, orderRepository = orderRepository
        )
    }

    @Test
    fun `주문할 상품들을 화면에 표시한다`() {
        // given
        justRun { view.setOrderProducts(orderProducts) }

        // when
        presenter.loadOrderProducts()

        // then
        verify { view.setOrderProducts(orderProducts) }
    }

    @Test
    fun `주문할 상품들의 가격 총합을 계산하고 화면을 표시한다`() {
        // given
        val productsSum = 100000
        val fixedDiscountPolicies = FixedDiscountPolicies(
            listOf(
                FixedDiscountPolicy(50000, 2000),
                FixedDiscountPolicy(100000, 5000),
                FixedDiscountPolicy(200000, 12000)
            )
        )
        every { orderProducts.getPickedProductsTotalPrice() } returns productsSum
        every { fixedDiscountPolicies.getDiscountPrice(productsSum) } returns 5000
        every { fixedDiscountPolicies.getFinalPrice(productsSum) } returns 95000

        justRun { view.setProductsSum(productsSum) }
        justRun { view.setDiscountPrice(discountPrice) }
        justRun { view.setFinalPrice(finalPrice) }
        // when
        presenter.calculatePrice()

        // then
        verify { view.setProductsSum(productsSum) }
        verify { view.setDiscountPrice(discountPrice) }
        verify { view.setFinalPrice(finalPrice) }
    }

    @Test
    fun `주문상세내역 화면으로 이동한다`() {
        // given
        val orderId = 1
        every { orderRepository.createOrder(orderProducts) }

        justRun { view.showOrderDetailPage(orderId) }

        // when
        presenter.navigateToOrderDetail() // 주문생성하고 success로 온 orderId를 상세페이지로 전달

        // then
        verify { view.showOrderDetailPage(orderId) }
    }
}
