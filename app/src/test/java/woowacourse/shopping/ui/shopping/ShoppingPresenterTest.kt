package woowacourse.shopping.ui.shopping

import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import org.junit.Before
import org.junit.Test
import woowacourse.shopping.mapper.toUIModel
import woowacourse.shopping.model.CartProduct
import woowacourse.shopping.model.CartProducts
import woowacourse.shopping.model.Product
import woowacourse.shopping.model.RecentProduct
import woowacourse.shopping.repository.CartRepository
import woowacourse.shopping.repository.ProductRepository
import woowacourse.shopping.repository.RecentRepository

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
        "https://img-cf.kurly.com/cdn-cgi/image/quality=85,width=676/shop/data/goods/1648206780555l0.jpeg",
    )

    private val fakeRecentProduct: RecentProduct = RecentProduct(
        1,
        "[사미헌] 갈비탕",
        12000,
        "https://img-cf.kurly.com/cdn-cgi/image/quality=85,width=676/shop/data/goods/1648206780555l0.jpeg",
    )

    private val fakeCartProduct: CartProduct = CartProduct(
        1,
        1,
        fakeProduct,
    )

    private val fakeProducts = List(10) { fakeProduct }
    private val fakeRecentProducts = List(10) { fakeRecentProduct }
    private val fakeCartProducts = CartProducts(List(10) { fakeCartProduct })

    private val fakeCartCounts = fakeCartProducts.toUIModel()
        .associateBy { it.id }
        .mapValues { it.value.count }

    @Before
    fun setUp() {
        view = mockk(relaxed = true)
        productRepository = mockk()
        recentRepository = mockk()
        cartRepository = mockk()
        presenter = ShoppingPresenter(view, productRepository, recentRepository, cartRepository)
    }

    @Test
    fun `초기 상품들을 세팅한다`() {
        // given
        every { productRepository.getNext(any(), any()) } answers { fakeProduct }
        every { recentRepository.getRecent(10) } returns fakeRecentProducts
        val productSlot = slot<(List<Product>?) -> Unit>()
        every { productRepository.getAll(capture(productSlot)) } answers {
            productSlot.captured(fakeProducts)
        }
        val cartSlot = slot<(CartProducts) -> Unit>()
        every { cartRepository.getAll(capture(cartSlot)) } answers {
            cartSlot.captured(fakeCartProducts)
        }

        // when
        presenter.setUpProducts()

        // then
        verify(exactly = 1) { view.setRecentProducts(fakeRecentProducts.map { it.toUIModel() }) }
        verify(exactly = 1) { view.addMoreProducts(fakeProducts.map { it.toUIModel() }) }
        verify(exactly = 1) { view.setCartProducts(fakeCartCounts) }
    }

    @Test
    fun `상품들을 추가로 전달한다`() {
        // given
        val slot = slot<(List<Product>?) -> Unit>()
        every { productRepository.getAll(capture(slot)) } answers {
            slot.captured(fakeProducts)
        }
        every { productRepository.getNext(any(), any()) } answers { fakeProduct }

        // when
        presenter.setUpNextProducts()

        // then
        verify(exactly = 1) { view.addMoreProducts(fakeProducts.map { it.toUIModel() }) }
    }

    @Test
    fun `상품들에 장바구니 숫자를 반영한다`() {
        // given
        val slot = slot<(CartProducts) -> Unit>()
        every { cartRepository.getAll(capture(slot)) } answers {
            slot.captured(fakeCartProducts)
        }

        // when
        presenter.setUpCartCounts()

        // then
        verify(exactly = 1) {
            view.setCartProducts(fakeCartCounts)
        }
    }

    @Test
    fun `상품의 개수를 장바구니 저장한다`() {
        // given
        val productSlot = slot<(List<Product>?) -> Unit>()
        every { productRepository.getAll(capture(productSlot)) } answers {
            productSlot.captured(fakeProducts)
        }
        every { productRepository.getNext(any(), any()) } answers { fakeProduct }
        every { productRepository.findById(1, any()) } answers { fakeProduct }
        every { recentRepository.getRecent(10) } returns fakeRecentProducts

        val cartSlot = slot<(CartProducts) -> Unit>()
        every { cartRepository.getAll(capture(cartSlot)) } answers {
            cartSlot.captured(fakeCartProducts)
        }
        val countUpdateSlot = slot<(Int?) -> Unit>()
        every { cartRepository.updateCount(any(), any(), capture(countUpdateSlot)) } answers {
            countUpdateSlot.captured(fakeProduct.id)
        }
        every { cartRepository.insert(any()) } returns Unit
        every { cartRepository.getTotalSelectedCount() } answers { 10 }

        // when
        presenter.setUpProducts()
        presenter.updateItemCount(1, 10)

        // then
        verify { view.setToolbar(10) }
    }

    @Test
    fun `총 장바구니 상품 개수를 반영한다`() {
        // given
        val slot = slot<(CartProducts) -> Unit>()
        every { cartRepository.getAll(capture(slot)) } answers {
            slot.captured(fakeCartProducts)
        }

        // when
        presenter.setUpCartCounts()

        // then
        verify(exactly = 1) {
            view.setCartProducts(fakeCartCounts)
        }
    }
}
