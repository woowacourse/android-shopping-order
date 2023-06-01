package woowacourse.shopping.feature.order

import com.example.domain.model.CartProduct
import com.example.domain.repository.CartRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.Before
import org.junit.Test
import woowacourse.shopping.feature.ProductFixture
import woowacourse.shopping.mapper.toPresentation

class OrderPresenterTest {
    lateinit var presenter: OrderContract.Presenter
    lateinit var view: OrderContract.View
    lateinit var cartRepository: CartRepository

    @Before
    fun setUp() {
        view = mockk(relaxed = true)
        cartRepository = mockk(relaxed = true)
        every { cartRepository.getAll(onSuccess = any(), onFailure = any()) } answers {
            firstArg<(List<CartProduct>) -> Unit>().invoke(ProductFixture.cartProducts)
        }
        presenter = OrderPresenter(view, cartRepository)
    }

    @Test
    fun `화면에 주문할 상품 목록이 나타난다`() {
        // Given: cartId 를 이용해 상품을 er4받아올 수 있는 상태이다.
        val cartIds = listOf<Long>(1, 2, 3)

        // When: cartId 를 통해 상품을 요청한다.
        presenter.requestProducts(cartIds)

        // Then: 주문할 상품 목록이 노출된다.
        verify { view.showProducts(ProductFixture.cartProducts.map { it.toPresentation() }) }
    }
}
