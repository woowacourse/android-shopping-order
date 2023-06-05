package woowacourse.shopping.ui.shopping

import io.mockk.every
import io.mockk.justRun
import io.mockk.mockk
import io.mockk.verify
import org.junit.Before
import org.junit.Test
import woowacourse.shopping.domain.model.CartProduct
import woowacourse.shopping.domain.model.Price
import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.model.ProductCount
import woowacourse.shopping.domain.model.RecentProduct
import woowacourse.shopping.domain.model.RecentProducts
import woowacourse.shopping.domain.repository.CartRepository
import woowacourse.shopping.domain.repository.ProductRepository
import woowacourse.shopping.domain.repository.RecentProductRepository
import woowacourse.shopping.model.PriceModel
import woowacourse.shopping.model.ProductModel
import woowacourse.shopping.model.RecentProductModel
import woowacourse.shopping.model.mapper.toUi

internal class ShoppingPresenterTest {
    private lateinit var presenter: ShoppingContract.Presenter
    private lateinit var view: ShoppingContract.View
    private lateinit var productRepository: ProductRepository
    private lateinit var recentProductRepository: RecentProductRepository
    private lateinit var cartRepository: CartRepository

    @Before
    fun setUp() {
        view = mockk(relaxed = true)
        productRepository = mockk(relaxed = true)
        recentProductRepository = mockk(relaxed = true)
        cartRepository = mockk(relaxed = true)
        presenter =
            ShoppingPresenter(view, productRepository, recentProductRepository, cartRepository)
    }

    @Test
    internal fun fetchAll_메서드를_호출하면_제품과_최근_본_목록을_갱신한다() {
        // given
        val recentProductSize = 10
        presenter =
            ShoppingPresenter(view, productRepository, recentProductRepository, cartRepository)

        every { recentProductRepository.getRecentProducts(any()) } returns RecentProducts(
            items = listOf(
                RecentProduct(1, Product(1, "상품", Price(1000), "상품 이미지"))
            )
        )

        // when
        presenter.fetchAll()

        // then
        verify(exactly = 1) { recentProductRepository.getRecentProducts(recentProductSize) }
        verify(exactly = 1) { view.updateRecentProducts(any()) }
    }

    @Test
    internal fun 제품_상세_내용을_조회한다() {
        // given
        val cartProduct = mockk<CartProduct>(relaxed = true)

        // when
        presenter.inquiryProductDetail(cartProduct.toUi())

        // then
        verify(exactly = 1) { view.navigateToProductDetail(cartProduct.product.toUi()) }
        verify(exactly = 1) { view.updateRecentProducts(any()) }
    }

    @Test
    internal fun 최근_제품_목록을_갱신한다() {
        // given
        val recentProducts = mockk<RecentProducts>(relaxed = true)
        presenter = ShoppingPresenter(
            view,
            productRepository,
            recentProductRepository,
            cartRepository,
            10,
            recentProducts,
        )

        // when
        presenter.fetchRecentProducts()

        // then
        val expected = recentProducts.getItems().toUi()
        verify(exactly = 1) { view.updateRecentProducts(expected) }
    }

    @Test
    internal fun 최근_상품_상세_내용을_조회한다() {
        // given
        val recentProduct = mockk<RecentProductModel>(relaxed = true)

        // when
        presenter.inquiryRecentProductDetail(recentProduct)

        // then
        verify(exactly = 1) { view.navigateToProductDetail(any()) }
    }

    @Test
    internal fun 장바구니_목록을_조회한다() {
        // given
        /* ... */

        // when
        presenter.inquiryCart()

        // then
        verify(exactly = 1) { view.navigateToCart() }
    }

    @Test
    internal fun 장바구니_화면으로_이동한다() {
        // given
        /* ... */

        // when
        presenter.inquiryCart()

        // then
        verify(exactly = 1) { view.navigateToCart() }
    }

    @Test
    internal fun `제품_개수를_증가_시킨다`() {
        // given
        val product = ProductModel(0, "제품", PriceModel(1000), "")
        val count = 3
        val productCount = ProductCount(3)
        justRun { productRepository.getAllProducts(any(), any()) }

        // when
        presenter.increaseCartCount(product, count)

        // then
        verify(exactly = 1) {
            cartRepository.increaseProductCountByProductId(
                product.id,
                productCount
            )
        }
        verify(exactly = 1) { productRepository.getAllProducts(any(), any()) }
    }
}