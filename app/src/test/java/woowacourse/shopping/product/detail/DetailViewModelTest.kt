package woowacourse.shopping.product.detail

import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import woowacourse.shopping.cart.ButtonEvent
import woowacourse.shopping.data.repository.CartProductRepository
import woowacourse.shopping.data.repository.CatalogProductRepository
import woowacourse.shopping.data.repository.RecentlyViewedProductRepository
import woowacourse.shopping.product.catalog.ProductUiModel
import woowacourse.shopping.util.CoroutinesTestExtension
import woowacourse.shopping.util.InstantTaskExecutorExtension
import woowacourse.shopping.util.getOrAwaitValue

@ExperimentalCoroutinesApi
@ExtendWith(InstantTaskExecutorExtension::class)
@ExtendWith(CoroutinesTestExtension::class)
class DetailViewModelTest {
    private lateinit var cartProductRepository: CartProductRepository
    private lateinit var catalogProductRepository: CatalogProductRepository
    private lateinit var recentlyViewedProductRepository: RecentlyViewedProductRepository

    private lateinit var viewModel: DetailViewModel

    private val dummyProduct =
        ProductUiModel(
            id = 1L,
            name = "Test Product",
            price = 1000,
            imageUrl = "https://example.com/image.jpg",
            quantity = 1,
            cartItemId = null,
        )

    @BeforeEach
    fun setUp() {
        cartProductRepository = mockk(relaxed = true)
        recentlyViewedProductRepository = mockk(relaxed = true)

        viewModel =
            DetailViewModel(
                product = dummyProduct,
                cartProductRepository = cartProductRepository,
                recentlyViewedProductRepository = recentlyViewedProductRepository,
            )
    }

    @Test
    fun `초기 product 값은 생성자 인자로 설정된 값과 동일하다`() =
        runTest {
            val product = viewModel.product.getOrAwaitValue()
            assertThat(product).isEqualTo(dummyProduct)
        }

    @Test
    fun `수량 증가 버튼 클릭 시 quantity가 1 증가한다`() =
        runTest {
            viewModel.updateQuantity(ButtonEvent.INCREASE)

            val updated = viewModel.product.getOrAwaitValue()
            assertThat(updated.quantity).isEqualTo(dummyProduct.quantity + 1)
        }

    @Test
    fun `수량 감소 버튼 클릭 시 quantity가 1 감소한다`() =
        runTest {
            viewModel.updateQuantity(ButtonEvent.DECREASE)

            val updated = viewModel.product.getOrAwaitValue()
            assertThat(updated.quantity).isEqualTo(dummyProduct.quantity - 1)
        }

    @Test
    fun `quantity가 0일 때 감소 버튼 클릭 시 값이 변하지 않는다`() =
        runTest {
            val productWithZeroQuantity = dummyProduct.copy(quantity = 0)
            viewModel =
                DetailViewModel(
                    productWithZeroQuantity,
                    cartProductRepository,
                    recentlyViewedProductRepository,
                )

            viewModel.updateQuantity(ButtonEvent.DECREASE)

            val updated = viewModel.product.getOrAwaitValue()
            assertThat(updated.quantity).isEqualTo(0)
        }

    @Test
    fun `최근 본 상품 설정 시 해당 값이 LiveData로 노출된다`() =
        runTest {
            viewModel.setLatestViewedProduct()

            val viewed = viewModel.latestViewedProduct.getOrAwaitValue()
            assertThat(viewed).isEqualTo(dummyProduct)
        }
}
