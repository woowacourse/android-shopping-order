package woowacourse.shopping.feature.payment

import com.example.domain.model.CartProduct
import com.example.domain.model.CartProducts
import com.example.domain.model.Price
import com.example.domain.model.Product
import com.example.domain.repository.CartRepository
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.verify
import org.junit.Before
import org.junit.Test

class PaymentPresenterTest {

    private lateinit var presenter: PaymentContract.Presenter
    private lateinit var view: PaymentContract.View
    private lateinit var cartRepository: CartRepository

    @Before
    fun setup() {
        view = mockk(relaxed = true)
        cartRepository = mockk(relaxed = true)
        presenter = PaymentPresenter(view, cartRepository)
    }

    @Test
    fun `구매할 상품 목록을 노출한다`() {
        every { view.showCartProducts(any()) } just Runs

        presenter.loadCartProducts(listOf(1, 2, 4))

        verify { view.showCartProducts(any()) }
    }

    private fun fakeCartProducts(): CartProducts {
        return CartProducts(
            listOf(
                CartProduct(1L, fakeProduct("쿨피스 1"), 1, true),
                CartProduct(2L, fakeProduct("쿨피스 2"), 1, true),
                CartProduct(3L, fakeProduct("쿨피스 3"), 1, true),
                CartProduct(4L, fakeProduct("쿨피스 4"), 1, true),
                CartProduct(5L, fakeProduct("쿨피스 5"), 1, true)
            )
        )
    }

    private fun fakeProduct(name: String) = Product(
        1,
        name,
        "https://product-image.kurly.com/product/image/0a8fe9ec-2ee0-495e-a6fc-b25de98e2d09.jpg",
        Price(2000)
    )

}
