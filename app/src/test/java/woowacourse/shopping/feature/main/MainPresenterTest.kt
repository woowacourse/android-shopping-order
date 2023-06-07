package woowacourse.shopping.feature.main

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.domain.model.BaseResponse
import com.example.domain.model.CartProduct
import com.example.domain.model.Product
import com.example.domain.model.RecentProduct
import com.example.domain.repository.CartRepository
import com.example.domain.repository.ProductRepository
import com.example.domain.repository.RecentProductRepository
import io.mockk.every
import io.mockk.mockk
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import woowacourse.shopping.feature.CartFixture
import woowacourse.shopping.feature.Product
import woowacourse.shopping.feature.ProductFixture
import woowacourse.shopping.feature.RecentProductFixture
import woowacourse.shopping.feature.getOrAwaitValue
import woowacourse.shopping.mapper.toPresentation
import woowacourse.shopping.model.CartProductUiModel
import woowacourse.shopping.model.ProductUiModel
import woowacourse.shopping.model.RecentProductUiModel
import java.time.LocalDateTime

internal class MainPresenterTest {
    private lateinit var view: MainContract.View
    private lateinit var presenter: MainContract.Presenter
    private lateinit var productRepository: ProductRepository
    private lateinit var cartRepository: CartRepository
    private lateinit var recentProductRepository: RecentProductRepository

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @Before
    fun init() {
        view = mockk()
        productRepository = mockk()
        cartRepository = mockk()
        recentProductRepository = mockk()
        presenter = MainPresenter(productRepository, cartRepository, recentProductRepository)
    }

    @Test
    fun `처음에 상품 목록과 장바구니 정보를 불러와서 화면에 보여주고, 현재 장바구니에 담긴 상품의 갯수를 뱃지에 보여준다`() {
        // given
        val mockProducts = ProductFixture.getProducts(
            1L to 2000,
            2L to 3000
        )
        every {
            productRepository.fetchFirstProducts(any())
        } answers {
            val successBlock = arg<(BaseResponse<List<Product>>) -> Unit>(0)
            successBlock(BaseResponse.SUCCESS(mockProducts))
        }

        val mockCartProducts = CartFixture.getMockCarts(
            Triple(1L, Product(2L, 3000), 3),
            Triple(2L, Product(3L, 5000), 1),
        )
        every {
            cartRepository.fetchAll(any())
        } answers {
            val successBlock = arg<(BaseResponse<List<CartProduct>>) -> Unit>(0)
            successBlock(BaseResponse.SUCCESS(mockCartProducts))
        }

        every {
            cartRepository.fetchSize(any())
        } answers {
            val successBlock = arg<(BaseResponse<Int>) -> Unit>(0)
            successBlock(BaseResponse.SUCCESS(4))
        }

        // when
        presenter.initLoadProducts()

        // then
        val actualCartProductUiModels = presenter.products.getOrAwaitValue()
        val expectedCartProductUiModels = listOf(
            CartProductUiModel(-1L, ProductUiModel(1L, "", "", 2000, 0), false),
            CartProductUiModel(1L, ProductUiModel(2L, "", "", 3000, 3), true),
        )
        assert(actualCartProductUiModels == expectedCartProductUiModels)

        // and
        val actualCartCountBadge = presenter.badgeCount.getOrAwaitValue()
        val expectedCartCountBadge = 4
        assert(actualCartCountBadge == expectedCartCountBadge)
    }

    @Test
    fun `최근 본 상품 목록을 불러와서 화면에 보여준다`() {
        // given
        val mockRecentProducts = RecentProductFixture.getRecentProducts(
            Product(2L, 3000) to LocalDateTime.of(2023, 5, 1, 0, 0),
            Product(1L, 2000) to LocalDateTime.of(2023, 1, 1, 0, 0),
        )
        every {
            recentProductRepository.fetchAllRecentProduct(any())
        } answers {
            val successBlock = arg<(BaseResponse<List<RecentProduct>>) -> Unit>(0)
            successBlock(BaseResponse.SUCCESS(mockRecentProducts))
        }

        // when
        presenter.loadRecentProducts()

        // then
        val actual = presenter.recentProducts.getOrAwaitValue()
        val expected = listOf(
            RecentProductUiModel(
                Product(2L, 3000).toPresentation(),
                LocalDateTime.of(2023, 5, 1, 0, 0),
            ),
            RecentProductUiModel(
                Product(1L, 2000).toPresentation(),
                LocalDateTime.of(2023, 1, 1, 0, 0),
            )
        )
        assert(actual == expected)
    }

    @Test
    fun `상품을 선택했을 때 해당 상품의 id가 서버에 유효한 상품 id면 상품 상세 화면을 보여준다`() {
        // given
        val mockProduct = Product(1L, 2000)
        every {
            productRepository.fetchProductById(1L, any())
        } answers {
            val successBlock = arg<(BaseResponse.SUCCESS<Product>) -> Unit>(1)
            successBlock(BaseResponse.SUCCESS(mockProduct))
        }

        every {
            recentProductRepository.addRecentProduct(mockProduct, any())
        } answers {
            val successBlock = arg<(BaseResponse<Product>) -> Unit>(1)
            successBlock(BaseResponse.SUCCESS(mockProduct))
        }

        // when
        presenter.showProductDetail(1L)

        // then
        val actual =
            presenter.mainScreenEvent.getOrAwaitValue() as MainContract.View.MainScreenEvent.ShowProductDetailScreen
        val expected = MainContract.View.MainScreenEvent.ShowProductDetailScreen(
            mockProduct.toPresentation(),
            null
        )
        assert(actual.product == expected.product)
    }

    @Test
    fun `장바구니 화면으로 이동한다`() {
        // when
        presenter.moveToCart()

        // then
        val actual = presenter.mainScreenEvent.getOrAwaitValue()
        assert(actual is MainContract.View.MainScreenEvent.ShowCartScreen)
    }
}
