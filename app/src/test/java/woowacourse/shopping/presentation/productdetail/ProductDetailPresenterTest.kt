package woowacourse.shopping.presentation.productdetail

import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import io.mockk.verify
import org.junit.Before
import org.junit.Test
import woowacourse.shopping.data.cart.CartRepository
import woowacourse.shopping.model.Product
import woowacourse.shopping.presentation.fixture.CartProductFixture

class ProductDetailPresenterTest {
    private lateinit var presenter: ProductDetailContract.Presenter
    private lateinit var view: ProductDetailContract.View
    private lateinit var cartRepository: CartRepository

    @Before
    fun setUp() {
        view = mockk(relaxed = true)
        cartRepository = mockk(relaxed = true)
        presenter = ProductDetailPresenter(view, cartRepository)
    }

    @Test
    fun `상품 목록을 불러온다`() {
        // given : 상품 상세를 불러올 수 있는 상태다.
        every {
            view.showProductDetail(
                productModel = CartProductFixture.getProductModel(1),
            )
        } just runs

        every {
            cartRepository.findProductById(
                productId = any(),
                callback = any(),
            )
        } answers {
            val callback = args[1] as (Product) -> Unit
            callback(CartProductFixture.getProduct(1))
        }

        // when : 상품 상세 불러오기 요청을 보낸다.
        presenter.loadProductDetail(1)

        // then : 상품 상세가 화면에 노출된다.
        verify {
            view.showProductDetail(
                productModel = CartProductFixture.getProductModel(1),
            )
        }
    }
}
