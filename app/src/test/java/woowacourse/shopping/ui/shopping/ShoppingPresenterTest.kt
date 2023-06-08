package woowacourse.shopping.ui.shopping

import io.mockk.every
import io.mockk.justRun
import io.mockk.mockk
import io.mockk.verify
import org.junit.Before
import org.junit.Test
import woowacourse.shopping.data.repository.CartRepository
import woowacourse.shopping.data.repository.ProductRepository
import woowacourse.shopping.data.repository.RecentRepository
import woowacourse.shopping.mapper.toUIModel
import woowacourse.shopping.model.CartProduct
import woowacourse.shopping.model.Product
import woowacourse.shopping.model.RecentProduct

class ShoppingPresenterTest {

    private lateinit var view: ShoppingContract.View
    private lateinit var presenter: ShoppingContract.Presenter
    private lateinit var productRepository: ProductRepository
    private lateinit var recentRepository: RecentRepository
    private lateinit var cartRepository: CartRepository

    private val fakeProduct: Product = Product(
        1,
        "[사미헌] 갈비탕",
        12000,
        "https://search4.kakaocdn.net/argon/656x0_80_wr/KjhZ1Chrw9p"
    )

    private val fakeRecentProduct: RecentProduct = RecentProduct(
        1,
        "[사미헌] 갈비탕",
        12000,
        "https://search4.kakaocdn.net/argon/656x0_80_wr/KjhZ1Chrw9p"
    )

    private val fakeCartProduct: CartProduct = CartProduct(
        1,
        1,
        fakeProduct
    )

    private val fakeProducts = List(10) { fakeProduct }
    private val fakeRecentProducts = List(10) { fakeRecentProduct }
    private val fakeCartProducts = MutableList(10) { fakeCartProduct }

    private val fakeCartCounts = fakeCartProducts.toUIModel()
        .associateBy { it.id }
        .mapValues { it.value.count }

    @Before
    fun setUp() {
        view = mockk()
        productRepository = mockk(relaxed = true)
        recentRepository = mockk(relaxed = true)
        cartRepository = mockk(relaxed = true)
        presenter = ShoppingPresenter(view, productRepository, recentRepository, cartRepository)
    }

    @Test
    fun `상품들을 추가로 전달한다`() {
        // given
        every { productRepository.getNext(any()) } answers { Result.success(fakeProducts) }
        justRun { view.setMoreProducts(any()) }

        // when
        presenter.fetchNextProducts()

        // then
        verify(exactly = 1) { view.setMoreProducts(fakeProducts.map { it.toUIModel() }) }
    }

    @Test
    fun `최근 본 상품들을 전달한다`() {
        // given
        every { recentRepository.getRecent(10) } returns fakeRecentProducts
        justRun { view.setRecentProducts(any()) }

        // when
        presenter.fetchRecentProducts()

        // then
        verify(exactly = 1) { view.setRecentProducts(fakeRecentProducts.toUIModel()) }
    }

    @Test
    fun `상품들에 장바구니 숫자를 반영하고 툴바를 설정한다`() {
        // given
        every { cartRepository.getAll() } returns Result.success(fakeCartProducts)
        justRun { view.setCartProducts(any()) }
        justRun { view.setToolbar(any()) }

        // when
        presenter.fetchCartCounts()

        // then
        verify(exactly = 1) { view.setCartProducts(fakeCartCounts) }

        // and
        verify(exactly = 1) { view.setToolbar(0) }
    }

    @Test
    fun `상품의 개수를 장바구니 저장한다`() {
        // given
        justRun { cartRepository.updateCountWithProductId(any(), any()) }
        every { cartRepository.getTotalCheckedQuantity() } returns 10
        every { cartRepository.getAll() } returns Result.success(fakeCartProducts)
        justRun { view.setToolbar(any()) }

        // when
        presenter.updateItemCount(1, 10)

        // then
        verify(exactly = 1) { view.setToolbar(10) }
    }

    @Test
    fun `선택한 상품의 상세페이지로 이동한다`() {
        // given
        justRun { view.navigateToProductDetail(any()) }

        // when
        presenter.processToItemDetail(fakeProduct.toUIModel().id)

        // then
        verify(exactly = 1) { view.navigateToProductDetail(fakeProduct.toUIModel().id) }
    }
}
