package woowacourse.shopping.presentation.order

import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import io.mockk.verify
import org.junit.Before
import org.junit.Test
import woowacourse.shopping.data.cart.CartRepository
import woowacourse.shopping.data.cash.CashRepository
import woowacourse.shopping.data.order.OrderRepository
import woowacourse.shopping.model.CartProduct
import woowacourse.shopping.presentation.fixture.CartProductFixture

class OrderPresenterTest {

    private lateinit var presenter: OrderContract.Presenter
    private lateinit var view: OrderContract.View
    private lateinit var orderRepository: OrderRepository
    private lateinit var cartRepository: CartRepository
    private lateinit var cashRepository: CashRepository

    @Before
    fun setUp() {
        view = mockk()
        orderRepository = mockk()
        cartRepository = mockk()
        cashRepository = mockk()
        presenter = OrderPresenter(view, cartRepository, cashRepository, orderRepository)
    }

    @Test
    fun `주문의 장바구니를 불러온다`() {
        // given : 주문 장바구니를 불러올 수 있는 상태다.
        every { view.showTotalPrice(3000) } just runs
        every {
            view.showOrderCartProducts(
                CartProductFixture.getCheckableCartProductModels(1, 1, 2, 3),
            )
        } just runs

        every {
            cartRepository.getCartProducts(any())
        } answers {
            val callback = args[0] as (List<CartProduct>) -> Unit
            callback(CartProductFixture.getCartProducts(quantity = 1, 1, 2, 3, 4, 5))
        }

        // when : 주문 장바구니 불러오기 요청을 보낸다.
        presenter.loadOrderCarts(listOf(1, 2, 3))

        // then : 주문 장바구니가 화면에 노출된다.
        verify { view.showTotalPrice(3000) }
        verify {
            view.showOrderCartProducts(
                CartProductFixture.getCheckableCartProductModels(1, 1, 2, 3),
            )
        }
    }
}
