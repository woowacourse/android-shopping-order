package woowacourse.shopping.feature.main

import com.example.domain.ProductCache
import com.example.domain.datasource.productsDatasource
import com.example.domain.model.CartProducts
import com.example.domain.model.Product
import com.example.domain.repository.CartRepository
import com.example.domain.repository.ProductRepository
import com.example.domain.repository.RecentProductRepository
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import org.junit.Before
import org.junit.Test
import woowacourse.shopping.data.datasource.local.product.ProductCacheImpl
import woowacourse.shopping.mapper.toDomain
import woowacourse.shopping.model.ProductUiModel

internal class MainPresenterTest {
    private lateinit var view: MainContract.View
    private lateinit var presenter: MainContract.Presenter
    private lateinit var productRepository: ProductRepository
    private lateinit var recentProductRepository: RecentProductRepository
    private lateinit var productCache: ProductCache
    private lateinit var cartRepository: CartRepository

    @Before
    fun init() {
        view = mockk()
        productRepository = mockk(relaxed = true)
        recentProductRepository = mockk()
        productCache = ProductCacheImpl
        cartRepository = mockk()

        presenter = MainPresenter(view, productRepository, recentProductRepository, cartRepository)
    }

    @Test
    fun `처음에 상품 목록을 불러와서 상품을 화면에 띄운다`() {

        every {
            productRepository.getProducts(page = 1, onSuccess = any(), onFailure = any())
        } answers {
            secondArg<(List<Product>) -> Unit>().invoke(mockProducts.take(20))
        }

        val slot = slot<List<ProductUiModel>>()
        every { view.addProducts(capture(slot)) } just Runs
        every { cartRepository.getAll() } returns CartProducts(emptyList())

        presenter.loadProducts()

        val actual = slot.captured.map { it.toDomain() }
        val expected = mockProducts.take(20)
        assert(actual == expected)
        verify { view.addProducts(any()) }
    }

    @Test
    fun `장바구니 화면으로 이동한다`() {
        every { view.showCartScreen() } just Runs

        presenter.moveToCart()

        verify { view.showCartScreen() }
    }

    @Test
    fun `최근 본 상품 목록을 가져와서 화면에 띄운다`() {
        every { recentProductRepository.getAll() } returns listOf()
        every { view.updateRecent(any()) } just Runs

        presenter.loadRecent()

        verify { view.updateRecent(any()) }
    }

    private val mockProducts = productsDatasource
}
