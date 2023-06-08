package woowacourse.shopping.feature.payment

import com.example.domain.model.CartProduct
import com.example.domain.model.CartProducts
import com.example.domain.model.Price
import com.example.domain.model.Product
import com.example.domain.repository.CartRepository
import com.example.domain.repository.OrderRepository
import com.example.domain.repository.PointRepository
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import org.junit.Before
import org.junit.Test
import woowacourse.shopping.mapper.toPresentation
import woowacourse.shopping.model.CartProductUiModel

class PaymentPresenterTest {

    private lateinit var presenter: PaymentContract.Presenter
    private lateinit var view: PaymentContract.View
    private lateinit var cartRepository: CartRepository
    private lateinit var pointRepository: PointRepository
    private lateinit var orderRepository: OrderRepository

    @Before
    fun setup() {
        view = mockk(relaxed = true)
        cartRepository = mockk(relaxed = true)
        pointRepository = mockk(relaxed = true)
        orderRepository = mockk()

        presenter = PaymentPresenter(view, cartRepository, pointRepository, orderRepository)
    }

    @Test
    fun `구매할 상품 목록을 노출한다`() {
        val slot = slot<List<CartProductUiModel>>()
        every { view.showCartProducts(capture(slot)) } just Runs
        every { cartRepository.getAll() } returns fakeCartProducts()

        presenter.loadCartProducts(listOf(1, 2, 3, 4, 5))

        val expected = fakeCartProducts().data.map { it.toPresentation() }
        val actual = slot.captured
        verify { view.showCartProducts(any()) }
        assert(actual == expected)
    }

    @Test
    fun `보유 포인트를 노출한다`() {
//        presenter.loadPoint()
//
//        every {
//            pointRepository.getPoint(onSuccess = any(), onFailure = any())
//        } answers {
//            val callback = args[0] as (Point) -> Unit
//            callback(Point(3000, 2000))
//        }
//
//        verify { view.showPoint(any()) }
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
