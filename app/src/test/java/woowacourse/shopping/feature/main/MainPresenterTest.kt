package woowacourse.shopping.feature.main

import com.example.domain.ProductCache
import com.example.domain.datasource.productsDatasource
import com.example.domain.model.product.Product
import com.example.domain.model.recentProduct.RecentProduct
import com.example.domain.repository.CartRepository
import com.example.domain.repository.PointRepository
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
import woowacourse.shopping.data.cache.ProductCacheImpl
import woowacourse.shopping.mapper.toDomain
import woowacourse.shopping.model.ProductUiModel
import woowacourse.shopping.model.RecentProductUiModel
import java.time.LocalDateTime

internal class MainPresenterTest {
    private lateinit var view: MainContract.View
    private lateinit var presenter: MainContract.Presenter
    private lateinit var productRepository: ProductRepository
    private lateinit var recentProductRepository: RecentProductRepository
    private lateinit var productCache: ProductCache
    private lateinit var cartRepository: CartRepository
    private lateinit var pointRepository: PointRepository

    @Before
    fun init() {
        view = mockk()
        productRepository = mockk(relaxed = true)
        recentProductRepository = mockk()
        productCache = ProductCacheImpl
        cartRepository = mockk()
        pointRepository = mockk(relaxed = true)

        presenter = MainPresenter(
            view,
            productRepository,
            recentProductRepository,
            cartRepository,
            pointRepository
        )
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
        every { cartRepository.getAll() } returns emptyList()

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
        every { recentProductRepository.getAll() } returns mockRecentProducts.map { it.product.id }
        every { productRepository.getProductById(any()) } returns mockProducts[0]
        val slot = slot<List<RecentProductUiModel>>()
        every { view.updateRecent(capture(slot)) } just Runs

        presenter.loadRecent()

        val actual = slot.captured[0].productUiModel.toDomain()
        val expected = mockRecentProducts[0].product
        println(actual.toString())
        println(expected.toString())
        assert(actual == expected)
        verify { view.updateRecent(any()) }
    }

    private val mockProducts = productsDatasource

    private val mockRecentProducts = List(20) {
        RecentProduct(
            mockProducts[it],
            LocalDateTime.now().plusMinutes(it.toLong())
        )
    }
}
