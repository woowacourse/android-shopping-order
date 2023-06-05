package woowacourse.shopping.ui.productdetail

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.Before
import org.junit.Test
import woowacourse.shopping.CartItem
import woowacourse.shopping.Product
import woowacourse.shopping.RecentlyViewedProduct
import woowacourse.shopping.User
import woowacourse.shopping.async
import woowacourse.shopping.repository.CartItemRepository
import woowacourse.shopping.repository.ProductRepository
import woowacourse.shopping.repository.RecentlyViewedProductRepository
import woowacourse.shopping.repository.UserRepository
import woowacourse.shopping.ui.productdetail.uistate.LastViewedProductUIState
import woowacourse.shopping.ui.productdetail.uistate.ProductDetailUIState

class ProductDetailPresenterTest {
    private lateinit var view: ProductDetailContract.View
    private lateinit var presenter: ProductDetailPresenter
    private lateinit var productRepository: ProductRepository
    private lateinit var cartItemRepository: CartItemRepository
    private lateinit var userRepository: UserRepository
    private lateinit var recentlyViewedProductRepository: RecentlyViewedProductRepository

    @Before
    fun setUp() {
        view = mockk(relaxed = true)
        productRepository = mockk()
        cartItemRepository = mockk()
        userRepository = mockk()
        recentlyViewedProductRepository = mockk()

        every { userRepository.findCurrent() } returns async(User())

        presenter = ProductDetailPresenter(
            view,
            productRepository,
            cartItemRepository,
            userRepository,
            recentlyViewedProductRepository
        )
    }

    @Test
    fun loadProduct() {
        // given
        val product = Product()
        val recentlyViewedProduct = RecentlyViewedProduct()
        every { productRepository.findById(any()) } returns async(product)
        every { cartItemRepository.existByProductId(any(), any()) } returns async(true)
        every { recentlyViewedProductRepository.save(any(), any()) } returns async(
            recentlyViewedProduct
        )

        // when
        presenter.loadProduct(0)

        // then
        val expect = ProductDetailUIState(0, "", "", 0, true)
        verify { view.setProduct(expect) }
    }

    @Test
    fun addProductToCart() {
        // given
        val product = Product()
        val savedCartItem = CartItem()

        every { productRepository.findById(any()) } returns async(product)
        every { cartItemRepository.save(any(), any()) } returns async(savedCartItem)
        every { cartItemRepository.updateCountById(any(), any(), any()) } returns async(Unit)

        // when
        presenter.addProductToCart(0, 1)

        // then
        verify { view.showCartView() }
    }

    @Test
    fun showCartCounter() {
        // given
        val product = Product()
        every { productRepository.findById(any()) } returns async(product)

        // when
        presenter.showCartCounter(0)

        // then
        val expect = ProductDetailUIState(0, "", "", 0, false)
        verify { view.openCartCounter(expect) }
    }

    @Test
    fun loadLastViewedProduct() {
        // given
        val recentlyViewedProduct = listOf(RecentlyViewedProduct())
        every {
            recentlyViewedProductRepository.findLimitedOrderByViewedTimeDesc(1)
        } returns async(recentlyViewedProduct)

        // when
        presenter.loadLastViewedProduct(1)
        // then
        val expect = LastViewedProductUIState(0, "", 0)
        verify { view.setLastViewedProduct(expect) }
    }
}
