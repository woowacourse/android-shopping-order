package woowacourse.shopping.feature.main

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.domain.model.CartProduct
import com.example.domain.model.Product
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
import woowacourse.shopping.feature.getOrAwaitValue
import woowacourse.shopping.model.CartProductUiModel
import woowacourse.shopping.model.ProductUiModel

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
            2L to 3000,
        )
        every {
            productRepository.fetchFirstProducts(onSuccess = any(), any())
        } answers {
            val successBlock = arg<(List<Product>) -> Unit>(0)
            successBlock(mockProducts)
        }

        val mockCartProducts = CartFixture.getMockCarts(
            Triple(1L, Product(2L, 3000), 3),
            Triple(2L, Product(3L, 5000), 1),
        )
        every {
            cartRepository.getAll(onSuccess = any(), any())
        } answers {
            val successBlock = arg<(List<CartProduct>) -> Unit>(0)
            successBlock(mockCartProducts)
        }

        every {
            cartRepository.getSize(any(), any())
        } answers {
            val successBlock = arg<(Int) -> Unit>(0)
            successBlock(4)
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
    fun `장바구니 화면으로 이동한다`() {
        // when
        presenter.moveToCart()

        // then
        val actual = presenter.mainScreenEvent.getOrAwaitValue()
        assert(actual is MainContract.View.MainScreenEvent.ShowCartScreen)
    }
}
