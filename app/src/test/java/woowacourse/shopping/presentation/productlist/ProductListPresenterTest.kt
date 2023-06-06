package woowacourse.shopping.presentation.productlist

import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import io.mockk.verify
import org.junit.Before
import org.junit.Test
import woowacourse.shopping.data.cart.CartRepository
import woowacourse.shopping.data.recentproduct.RecentProductRepository
import woowacourse.shopping.presentation.fixture.CartProductFixture
import woowacourse.shopping.presentation.model.CartProductModel

class ProductListPresenterTest {
    private lateinit var presenter: ProductListContract.Presenter
    private lateinit var view: ProductListContract.View
    private lateinit var cartRepository: CartRepository
    private lateinit var recentProductRepository: RecentProductRepository

    @Before
    fun setUp() {
        view = mockk(relaxed = true)
        cartRepository = mockk(relaxed = true)
        recentProductRepository = mockk(relaxed = true)
        presenter = ProductListPresenter(view, cartRepository, recentProductRepository)
    }

    @Test
    fun `상품 목록을 불러온다`() {
        // given : 상품을 불러올 수 있는 상태다.
        every {
            view.showProductModels(
                cartProductModels = CartProductFixture.getCartProductModels(1, 2, 3),
                isLast = false,
            )
        } just runs

        every {
            cartRepository.getProductsByRange(
                lastProductId = any(),
                pageItemCount = any(),
                callback = any(),
            )
        } answers {
            val callback = args[2] as (List<CartProductModel>, Boolean) -> Unit
            callback(CartProductFixture.getCartProductModels(1, 2, 3), false)
        }

        // when : 상품 목록 요청을 보낸다.
        presenter.loadProducts()

        // then : 상품을 노출시킨다.
        verify {
            view.showProductModels(
                cartProductModels = CartProductFixture.getCartProductModels(1, 2, 3),
                isLast = false,
            )
        }
    }
}
