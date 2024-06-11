package woowacourse.shopping.presentation.ui.detail

import io.mockk.coEvery
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import woowacourse.shopping.CoroutinesTestExtension
import woowacourse.shopping.InstantTaskExecutorExtension
import woowacourse.shopping.domain.repository.CartRepository
import woowacourse.shopping.domain.repository.ProductRepository
import woowacourse.shopping.domain.repository.RecentRepository
import woowacourse.shopping.dummyCarts
import woowacourse.shopping.dummyProduct
import woowacourse.shopping.dummyProducts
import woowacourse.shopping.getOrAwaitValue
import woowacourse.shopping.presentation.ui.model.toUiModel

@ExperimentalCoroutinesApi
@ExtendWith(CoroutinesTestExtension::class)
@ExtendWith(InstantTaskExecutorExtension::class)
@ExtendWith(MockKExtension::class)
class ProductDetailViewModelTest {
    @MockK
    private lateinit var productRepository: ProductRepository

    @MockK
    private lateinit var cartRepository: CartRepository

    @MockK
    private lateinit var recentRepository: RecentRepository

    private val productId: Long = 10L
    private val isLastViewedItem = false

    private lateinit var viewModel: ProductDetailViewModel

    @BeforeEach
    fun setup() {
        coEvery { productRepository.loadById(10L) } returns Result.success(dummyProduct)
        coEvery { recentRepository.add(any()) } returns Result.success(1L)
        coEvery { recentRepository.loadMostRecent() } returns Result.success(dummyProducts[1])

        viewModel =
            ProductDetailViewModel(
                productRepository,
                cartRepository,
                recentRepository,
                productId,
                isLastViewedItem,
            )
    }

    @Test
    fun `선택된 상품의 상세 데이터를 불러온다`() =
        runTest {
            val expected = dummyProduct.toUiModel(1)
            assertThat(viewModel.product.getOrAwaitValue()).isEqualTo(expected)
        }

    @Test
    fun `가장 최근에 본 데이터를 불러온다`() =
        runTest {
            val expected = dummyProducts[1].toUiModel()
            assertThat(viewModel.lastProduct.getOrAwaitValue()).isEqualTo(expected)
        }

    @Test
    fun `선택한 상품을 장바구니에 추가한다`() =
        runTest {
            coEvery { cartRepository.loadAll() } returns Result.success(dummyCarts)
            coEvery { cartRepository.saveNewCartItem(any(), any()) } returns Result.success(10L)

            viewModel.addProductToCart()

            val actual = viewModel.navigationEvent.getOrAwaitValue().getContentIfNotHandled()
            val expected = FromDetailToScreen.ShoppingWithUpdated(productId, 1)
            assertThat(actual).isEqualTo(expected)
        }
}
