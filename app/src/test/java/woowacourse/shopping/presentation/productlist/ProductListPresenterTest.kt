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
import woowacourse.shopping.model.Product
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

    @Test
    fun `최근 본 상품 목록을 불러온다`() {
        // given : 최근 본 상품을 불러올 수 있는 상태다.
        every {
            view.showRecentProductModels(
                productModels = CartProductFixture.getProductModels(1, 2, 3),
            )
        } just runs

        every {
            recentProductRepository.getRecentProductsBySize(size = any(), callback = any())
        } answers {
            val callback = args[1] as (List<Product>) -> Unit
            callback(CartProductFixture.getProducts(1, 2, 3))
        }

        // when : 최근 본 상품 요청을 보낸다.
        presenter.loadRecentProducts()

        // then : 최근 본 상품을 노출시킨다.
        verify {
            view.showRecentProductModels(
                productModels = CartProductFixture.getProductModels(1, 2, 3),
            )
        }
    }

    @Test
    fun `상품 상세를 보여준다`() {
        // given : 상품 상세를 보여줄 수 있는 상태다.
        every {
            view.showProductDetail(
                productModel = CartProductFixture.getProductModel(1),
            )
        } just runs

        every {
            recentProductRepository.addRecentProduct(any(), any())
        }

        every { recentProductRepository.addRecentProduct(any(), any()) } just runs

        // when : 상품 상세를 보여달라는 요청을 보낸다.
        presenter.navigateProductDetail(CartProductFixture.getProductModel(1))

        // then : 상품 상세 화면이 노출된다.
        verify {
            view.showProductDetail(
                productModel = CartProductFixture.getProductModel(1),
            )
        }

        // and : 최근 본 상품이 저장된다.
        verify {
            recentProductRepository.addRecentProduct(
                product = CartProductFixture.getProduct(1),
                any(),
            )
        }
    }

    @Test
    fun `장바구니에 담긴 상품 개수가 증가한다`() {
        // given : 장바구니의 상품 개수를 증가시킬 수 있는 상태다.
        every {
            view.replaceProductModel(
                cartProductModel = CartProductFixture.getCartProductModel(1, 2),
            )
        } just runs

        every {
            cartRepository.updateCartProductCount(any(), any(), any())
        } answers {
            val callback = args[2] as () -> Unit
            callback()
        }

        // when : 장바구니 상품 개수 증가 요청을 보낸다.
        presenter.addCartProductCount(CartProductFixture.getCartProductModel(1))

        // then : 증가된 상품 개수가 노출된다.
        verify {
            view.replaceProductModel(
                cartProductModel = CartProductFixture.getCartProductModel(1, 2),
            )
        }
    }

    @Test
    fun `장바구니에 담긴 상품 개수가 감소한다`() {
        // given : 장바구니의 상품 개수를 감소시킬 수 있는 상태다.
        every {
            view.replaceProductModel(
                cartProductModel = CartProductFixture.getCartProductModel(1, 1),
            )
        } just runs

        every {
            cartRepository.updateCartProductCount(any(), any(), any())
        } answers {
            val callback = args[2] as () -> Unit
            callback()
        }

        // when : 장바구니 상품 개수 감소 요청을 보낸다.
        presenter.subCartProductCount(CartProductFixture.getCartProductModel(1, 2))

        // then : 감소된 상품 개수가 노출된다.
        verify {
            view.replaceProductModel(
                cartProductModel = CartProductFixture.getCartProductModel(1, 1),
            )
        }
    }
}
