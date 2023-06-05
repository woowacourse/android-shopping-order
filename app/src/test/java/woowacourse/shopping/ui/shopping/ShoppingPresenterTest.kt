package woowacourse.shopping.ui.shopping

import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
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
import woowacourse.shopping.ui.shopping.uistate.ProductUIState.Companion.toUIState
import woowacourse.shopping.ui.shopping.uistate.RecentlyViewedProductUIState

class ShoppingPresenterTest {
    private lateinit var view: ShoppingContract.View
    private lateinit var presenter: ShoppingPresenter
    private lateinit var recentlyViewedProductRepository: RecentlyViewedProductRepository
    private lateinit var productRepository: ProductRepository
    private lateinit var cartItemRepository: CartItemRepository
    private lateinit var userRepository: UserRepository
    private val pageSize = 20

    @Before
    fun setUp() {
        view = mockk()
        recentlyViewedProductRepository = mockk()
        productRepository = mockk()
        cartItemRepository = mockk()
        userRepository = mockk()

        every { userRepository.findAll() } returns async(listOf(User()))

        presenter = ShoppingPresenter(
            view,
            recentlyViewedProductRepository,
            productRepository,
            cartItemRepository,
            userRepository,
            pageSize = pageSize
        )
    }

    @Test
    fun 최근에_본_상품_10개를_불러오고_표시한다() {
        // given
        val recentlyViewedProducts = List(10) { RecentlyViewedProduct() }
        every {
            recentlyViewedProductRepository.findLimitedOrderByViewedTimeDesc(10)
        } returns async(recentlyViewedProducts)
        every { view.setRecentlyViewedProducts(any()) } just runs

        // when
        presenter.loadRecentlyViewedProducts(10)

        // then
        val expect = List(10) {
            RecentlyViewedProductUIState(
                0, "", "", 0
            )
        }

        verify { recentlyViewedProductRepository.findLimitedOrderByViewedTimeDesc(10) }
        verify { view.setRecentlyViewedProducts(expect) }
    }

    @Test
    fun 다음_페이지_상품을_불러오고_표시한다() {
        // given
        val products = listOf(Product())
        val cartItems = listOf(CartItem())
        every {
            productRepository.findAll(any(), any())
        } returns async(products)
        every {
            cartItemRepository.findAll(any())
        } returns async(cartItems)
        every {
            view.addProducts(any())
        } just runs

        every { productRepository.countAll() } returns async(1)
        every { view.setCanLoadMore(any()) }

        // when
        presenter.loadProductsNextPage()

        // then
        val expect = listOf(CartItem(id = 0, product = Product(0)))
        verify { productRepository.findAll(pageSize, 20) }
        verify { cartItemRepository.findAll(any()) }
        verify { view.addProducts(expect.map { it.toUIState() }) }

        verify { productRepository.countAll() }
        verify { view.setCanLoadMore(false) }
    }

    @Test
    fun 상품_목록을_다시_불러오고_표시한다() {
        // given
        val products = listOf(Product(0))
        every {
            productRepository.findAll(any(), any())
        } returns async(products)
        every {
            cartItemRepository.findAll(any())
        } returns async(listOf(CartItem(0, product = products[0])))
        every { view.setProducts(any()) } just runs
        every { view.setCanLoadMore(any()) } just runs

        every { productRepository.countAll() } returns async(1)
        every { view.setCanLoadMore(any()) }

        // when
        presenter.refreshProducts()

        // then
        val expect = CartItem(0, product = Product(0)).toUIState()
        verify { productRepository.findAll(20, 0) }
        verify { cartItemRepository.findAll(any()) }
        verify { view.setProducts(listOf(expect)) }

        verify { productRepository.countAll() }
        verify { view.setCanLoadMore(false) }
    }

    @Test
    fun 장바구니에_상품을_추가하고_표시한다() {
        // given
        val product = Product(id = 0)
        every { productRepository.findById(any()) } returns async(product)
        every {
            cartItemRepository.save(any(), any())
        } returns async(CartItem(id = 0, quantity = 1, product = product))
        every { view.changeProduct(any()) } just runs

        every { cartItemRepository.countAll(any()) } returns async(0)
        every { view.setCartItemCount(any()) } just runs

        // when
        presenter.addProductToCart(0)

        // then
        verify { productRepository.findById(0) }
        verify { cartItemRepository.save(CartItem(-1, 1, product), any()) }
        verify { view.changeProduct(CartItem(0, 1, product).toUIState()) }

        verify { cartItemRepository.countAll(any()) }
        verify { view.setCartItemCount(0) }
    }

    @Test
    fun 장바구니에서_수량을_더하면_수량이_1_증가하고_표시한다() {
        // given
        val cartItem = CartItem(0, 1, Product())
        every { cartItemRepository.findById(any(), any()) } returns async(cartItem)
        every { cartItemRepository.updateCountById(any(), any(), any()) } returns async(Unit)
        every { view.changeProduct(any()) } just runs

        // when
        presenter.plusCartItemQuantity(0)

        // then
        val plusCartItem = CartItem(0, 2, Product())
        verify { cartItemRepository.findById(0, any()) }
        verify { cartItemRepository.updateCountById(0, 2, any()) }
        verify { view.changeProduct(plusCartItem.toUIState()) }
    }

    @Test
    fun 장바구니에서_수량을_빼면_수량이_1_감소하고_표시한다() {
        // given
        val cartItem = CartItem(0, 2, Product())
        every { cartItemRepository.findById(any(), any()) } returns async(cartItem)
        every { cartItemRepository.updateCountById(any(), any(), any()) } returns async(Unit)
        every { view.changeProduct(any()) } just runs

        // when
        presenter.minusCartItemQuantity(0)

        // then
        val plusCartItem = CartItem(0, 1, Product())
        verify { cartItemRepository.findById(0, any()) }
        verify { cartItemRepository.updateCountById(0, 1, any()) }
        verify { view.changeProduct(plusCartItem.toUIState()) }
    }

    @Test
    fun 장바구니에서_수량을_뺄_때_0이_되면_장바구니에서_제거하고_표시한다() {
        // given
        val cartItem = CartItem(0, 1, Product())
        every { cartItemRepository.findById(any(), any()) } returns async(cartItem)
        every { cartItemRepository.deleteById(any(), any()) } returns async(Unit)
        every { view.changeProduct(any()) } just runs

        // when
        presenter.minusCartItemQuantity(0)

        // then
        val minusCartItem = CartItem(-1, 0, Product())
        verify { cartItemRepository.findById(0, any()) }
        verify { cartItemRepository.deleteById(0, any()) }
        verify { view.changeProduct(minusCartItem.toUIState()) }
    }

    @Test
    fun 장바구니_아이템_개수를_불러오고_표시한다() {
        // given
        every { cartItemRepository.countAll(any()) } returns async(0)
        every { view.setCartItemCount(any()) } just runs

        // when
        presenter.loadCartItemCount()

        // then
        every { cartItemRepository.countAll(any()) }
        every { view.setCartItemCount(0) }
    }

    @Test
    fun 모든_유저_목록을_불러오고_표시한다() {
        // given
        val users = listOf(User())
        every { userRepository.findAll() } returns async(users)
        every { view.showUserList(any()) }

        // when
        presenter.loadUsers()

        // then
        verify { userRepository.findAll() }
        verify { view.showUserList(users) }
    }

    @Test
    fun 유저를_선택하면_해당_유저의_상품과_장바구니를_불러오고_표시한다() {
        // given
        val user = User()
        every { userRepository.saveCurrent(any()) } just runs

        val products = listOf(Product(0))
        every {
            productRepository.findAll(any(), any())
        } returns async(products)
        every {
            cartItemRepository.findAll(any())
        } returns async(listOf(CartItem(0, product = products[0])))
        every { view.setProducts(any()) } just runs
        every { view.setCanLoadMore(any()) } just runs

        every { productRepository.countAll() } returns async(1)
        every { view.setCanLoadMore(any()) }

        every { cartItemRepository.countAll(any()) } returns async(0)
        every { view.setCartItemCount(any()) } just runs

        // when
        presenter.selectUser(user)

        // then
        verify { userRepository.saveCurrent(user) }
    }
}
