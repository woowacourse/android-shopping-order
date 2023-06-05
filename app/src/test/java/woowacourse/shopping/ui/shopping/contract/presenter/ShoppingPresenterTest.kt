package woowacourse.shopping.ui.shopping.contract.presenter

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.domain.model.CartProduct
import com.example.domain.model.Product
import com.example.domain.repository.CartRepository
import com.example.domain.repository.ProductRepository
import com.example.domain.repository.RecentRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import woowacourse.shopping.ui.shopping.contract.ShoppingContract

internal class ShoppingPresenterTest {

    private lateinit var view: ShoppingContract.View
    private lateinit var presenter: ShoppingPresenter
    private lateinit var productRepository: ProductRepository
    private lateinit var recentRepository: RecentRepository
    private lateinit var cartRepository: CartRepository

    private val fakeProduct: Product = Product(
        1,
        "[사미헌] 갈비탕",
        12000,
        "https://img-cf.kurly.com/cdn-cgi/image/quality=85,width=676/shop/data/goods/1648206780555l0.jpeg",
    )

    private val cartfakeProduct: CartProduct = CartProduct(
        1,
        1,
        fakeProduct,
    )

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setUp() {
        view = mockk(relaxed = true)
        productRepository = mockk(relaxed = true)
        recentRepository = mockk(relaxed = true)
        cartRepository = mockk(relaxed = true)
        presenter = ShoppingPresenter(view, productRepository, recentRepository, cartRepository)
    }

    @Test
    fun `상품을 불러와서 세팅한다`() {
        // given
        every { cartRepository.getAllProductInCart() } returns Result.success(listOf(cartfakeProduct))
        every { productRepository.getMoreProducts(any(), any()) } returns Result.success(
            listOf(
                fakeProduct,
            ),
        )
        every { recentRepository.getRecent(10) } returns List(10) { fakeProduct }
        every { view.setProducts(any()) } answers { nothing }

        // when
        presenter.initProducts()

        // then
        verify { view.setProducts(any()) }
    }

    @Test
    fun `최근 상품이 없으면 최근 상품을 세팅하지 않는다`() {
        // given
        every { productRepository.getMoreProducts(any(), any()) } returns Result.success(
            listOf(
                fakeProduct,
            ),
        )
        every { recentRepository.getRecent(10) } returns emptyList()
        every { view.setProducts(any()) } answers { nothing }

        // when
        presenter.updateProducts()

        verify { view.setProducts(any()) }
    }

    @Test
    fun `상품을 불러와서 업데이트한다`() {
        // given
        every { productRepository.getMoreProducts(any(), any()) } returns Result.success(
            listOf(
                fakeProduct,
            ),
        )
        every { recentRepository.getRecent(10) } returns List(10) { fakeProduct }
        every { view.setProducts(any()) } answers { nothing }

        // when
        presenter.updateProducts()

        // then
        verify { view.setProducts(any()) }
    }

    @Test
    fun `리스트에 있는 상품을 클릭하면 상세화면으로 이동한다`() {
        // given
        every { cartRepository.getAllProductInCart() } returns Result.success(listOf(cartfakeProduct))
        every { productRepository.getMoreProducts(any(), any()) } returns Result.success(
            listOf(
                fakeProduct,
            ),
        )
        every { recentRepository.getRecent(1) } returns listOf(fakeProduct)

        // when
        presenter.initProducts()
        presenter.navigateToItemDetail(fakeProduct.id)

        // then
        verify { view.navigateToProductDetail(fakeProduct.id, any()) }
    }

    @Test
    fun `수량이 증가한다`() {
        // given
        every { cartRepository.getAllProductInCart() } returns Result.success(listOf(cartfakeProduct))
        every { productRepository.getMoreProducts(any(), any()) } returns Result.success(
            listOf(
                fakeProduct,
            ),
        )
        every { recentRepository.getRecent(10) } returns List(10) { fakeProduct }
        every { cartRepository.updateCount(any(), any()) } returns Result.success(Unit)
        every { view.updateItem(any(), any()) } answers { nothing }

        // when
        presenter.initProducts()
        presenter.increaseCount(fakeProduct.id)

        // then
        verify { view.updateItem(any(), any()) }
    }

    @Test
    fun `수량이 감소한다`() {
        // given
        every { cartRepository.getAllProductInCart() } returns Result.success(listOf(cartfakeProduct))
        every { productRepository.getMoreProducts(any(), any()) } returns Result.success(
            listOf(
                fakeProduct,
            ),
        )
        every { recentRepository.getRecent(10) } returns List(10) { fakeProduct }
        every { cartRepository.updateCount(any(), any()) } returns Result.success(Unit)
        every { view.updateItem(any(), any()) } answers { nothing }

        // when
        presenter.initProducts()
        presenter.increaseCount(fakeProduct.id)

        // then
        verify { view.updateItem(any(), any()) }
    }

    @Test
    fun `장바구니에 들어있는 상품의 개수를 알 수 있다`() {
        // given
        val slot = slot<Int>()
        every { cartRepository.getAllProductInCart() } returns Result.success(listOf(cartfakeProduct))
        every { view.showCountSize(capture(slot)) } answers { nothing }
        // when
        presenter.updateCountSize()

        // then
        verify { view.showCountSize(slot.captured) }
    }

    @Test
    fun `더 보기를 누르면 상품을 불러온다`() {
// given
        every { cartRepository.getAllProductInCart() } returns Result.success(listOf(cartfakeProduct))
        every { productRepository.getMoreProducts(any(), any()) } returns Result.success(
            List(10) { fakeProduct },
        )
        every { view.addProducts(any()) } answers { nothing }

// when
        presenter.initProducts()
        presenter.fetchMoreProducts()

// then

        verify { view.addProducts(any()) }
    }
}
