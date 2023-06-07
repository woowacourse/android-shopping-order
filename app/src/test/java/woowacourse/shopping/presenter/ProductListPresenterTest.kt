package woowacourse.shopping.presenter

import io.mockk.mockk
import io.mockk.verify
import org.junit.Before
import org.junit.Test
import woowacourse.shopping.data.remote.dto.ProductsWithCartItemDTO
import woowacourse.shopping.data.repository.CartRepository
import woowacourse.shopping.data.repository.ProductRepository
import woowacourse.shopping.data.repository.RecentViewedRepository
import woowacourse.shopping.domain.model.CartProduct
import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.model.ProductWithCartInfo
import woowacourse.shopping.model.toUiModel
import woowacourse.shopping.view.productlist.ProductListContract
import woowacourse.shopping.view.productlist.ProductListPresenter

class ProductListPresenterTest {
    private lateinit var presenter: ProductListContract.Presenter
    private lateinit var view: ProductListContract.View
    private lateinit var cartRepository: CartRepository
    private lateinit var productRepository: ProductRepository
    private lateinit var recentViewedRepository: RecentViewedRepository

    @Before
    fun setUp() {
        view = mockk(relaxed = true)
        productRepository = object : ProductRepository {
            override fun getProductsByRange(
                lastId: Int,
                pageItemCount: Int,
                callback: (ProductsWithCartItemDTO) -> Unit,
            ) {
                callback(
                    ProductsWithCartItemDTO(
                        ProductListFixture.products.map {
                            ProductWithCartInfo(
                                it,
                                ProductWithCartInfo.CartItem(1, 1),
                            )
                        },
                        false,
                    ),
                )
            }

            override fun getProductById(id: Int, callback: (ProductWithCartInfo) -> Unit) {
                callback(ProductWithCartInfo(ProductListFixture.products[0], null))
            }
        }

        recentViewedRepository = object : RecentViewedRepository {
            override fun findAll(callback: (List<Product>) -> Unit) {
                callback(ProductListFixture.products)
            }

            override fun add(product: Product) {
            }

            override fun remove(id: Int) {
            }
        }

        cartRepository = object : CartRepository {
            override fun getAll(callback: (List<CartProduct>) -> Unit) {
                callback(CartProductsFixture.cartProducts)
            }

            override fun insert(productId: Int, quantity: Int, callback: (Int) -> Unit) {
                callback(1)
            }

            override fun update(cartId: Int, quantity: Int, callback: (Boolean) -> Unit) {
                callback(true)
            }

            override fun remove(cartId: Int, callback: (Boolean) -> Unit) {
                callback(true)
            }
        }

        presenter =
            ProductListPresenter(view, productRepository, recentViewedRepository, cartRepository)
    }

    @Test
    fun 최근_본_상품과_상품들을_띄울_수_있다() {
        // given

        // when
        presenter.fetchProducts()

        // then
        verify(exactly = 1) { view.showProducts(any()) }
        verify(exactly = 1) { view.stopLoading() }
    }

    @Test
    fun 상품_상세_정보를_띄울_수_있다() {
        // given

        // when
        presenter.showProductDetail(ProductListFixture.products[0].toUiModel(null, 0))
        // then
        verify(exactly = 1) { view.onClickProductDetail(ProductListFixture.products[0].toUiModel(null, 0), any()) }
    }

    @Test
    fun 상품을_추가로_띄울_수_있다() {
        // given
        presenter.fetchProducts()

        // when
        presenter.loadMoreProducts()

        // then
        verify(exactly = 1) { view.notifyAddProducts(any(), any()) }
    }

    @Test
    fun 장바구니에_상품을_추가할_수_있다() {
        // given
        presenter.fetchProducts()
        // when
        presenter.insertCartProduct(1)

        // then
        verify(exactly = 1) { view.showCartCount(any()) }
        verify(exactly = 1) { view.notifyDataChanged(any()) }
    }

    @Test
    fun 장바구니_상품_개수를_1이상으로_지정하면_업데이트할_수_있다() {
        // given
        presenter.fetchProducts()

        // when
        presenter.updateCartProductCount(1, 2, 3)

        //
        verify { view.notifyDataChanged(any()) }
    }

    @Test
    fun 장바구니_상품_개수를_0으로_지정하면_장바구니에서_삭제하고_뱃지_숫자도_변경한다() {
        // given
        presenter.fetchProducts()

        // when
        presenter.updateCartProductCount(0, 0, 0)

        // then
        verify { view.notifyDataChanged(any()) }
        verify { view.showCartCount(any()) }
    }

    @Test
    fun 장바구니_상품_개수를_띄울_수_있다() {
        // given

        // when
        presenter.fetchCartCount()

        // then
        verify { view.showCartCount(any()) }
    }

    @Test
    fun 여러_개의_상품_개수를_업데이트할_수_있다() {
        // given
        presenter.fetchProducts()

        // when
        presenter.fetchProductsCounts()

        // then
        verify { view.notifyDataChanged(any()) }
    }

    @Test
    fun 특정_상품_개수를_업데이트할_수_있다() {
        // given
        presenter.fetchProducts()

        // when
        presenter.fetchProductCount(1)

        // then
        verify { view.notifyDataChanged(any()) }
    }

    @Test
    fun 최근에_본_상품_리스트를_업데이트할_수_있다() {
        // given
        presenter.fetchProducts()

        // when
        presenter.updateRecentViewed(1)

        // then
        verify { view.notifyRecentViewedChanged() }
    }
}
