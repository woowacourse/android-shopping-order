package woowacourse.shopping.presentation.cart

import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import io.mockk.verify
import org.junit.Before
import org.junit.Test
import woowacourse.shopping.data.cart.CartRepository
import woowacourse.shopping.data.recentproduct.RecentProductRepository
import woowacourse.shopping.model.CartProduct
import woowacourse.shopping.presentation.fixture.CartProductFixture

class CartPresenterTest {
    private lateinit var presenter: CartContract.Presenter
    private lateinit var view: CartContract.View
    private lateinit var cartRepository: CartRepository
    private lateinit var recentProductRepository: RecentProductRepository

    @Before
    fun setUp() {
        view = mockk(relaxed = true)
        cartRepository = mockk(relaxed = true)
        recentProductRepository = mockk(relaxed = true)
        presenter = CartPresenter(view, cartRepository)
    }

    @Test
    fun `장바구니 목록을 불러온다`() {
        // given : 장바구니 목록을 불러올 수 있는 상태다.
        every {
            view.showCartProductModels(
                CartProductFixture.getCheckableCartProductModels(1, 2, 3, 4, 5),
            )
        } just runs

        every {
            cartRepository.getCartProducts(
                callback = any(),
            )
        } answers {
            val callback = args[0] as (List<CartProduct>) -> Unit
            callback(CartProductFixture.getCartProducts(1, 2, 3, 4, 5, 6, 7, 8, 9, 10))
        }

        // when : 장바구니 목록 불러오기 요청을 보낸다.
        presenter.loadCarts()

        // then : 장바구니 목록이 노출된다.
        verify {
            view.showCartProductModels(
                CartProductFixture.getCheckableCartProductModels(1, 2, 3, 4, 5),
            )
        }
    }

    @Test
    fun `장바구니 아이템을 삭제한다`() {
        // given : 장바구니 목록을 불러올 수 있는 상태다.
        every {
            view.showCartProductModels(
                CartProductFixture.getCheckableCartProductModels(1, 2, 3, 4, 5),
            )
        } just runs

        every {
            cartRepository.getCartProducts(
                callback = any(),
            )
        } answers {
            val callback = args[0] as (List<CartProduct>) -> Unit
            callback(CartProductFixture.getCartProducts(1, 2, 3, 4, 5, 6, 7, 8, 9, 10))
        }

        // when : 장바구니 목록 불러오기 요청을 보낸다.
        presenter.loadCarts()

        // then : 장바구니 목록이 노출된다.
        verify {
            view.showCartProductModels(
                CartProductFixture.getCheckableCartProductModels(1, 2, 3, 4, 5),
            )
        }
    }
}
