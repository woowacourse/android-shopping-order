package woowacourse.shopping.view

import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import woowacourse.shopping.CoroutinesTestExtension
import woowacourse.shopping.InstantTaskExecutorExtension
import woowacourse.shopping.domain.Quantity
import woowacourse.shopping.domain.exception.NetworkResult
import woowacourse.shopping.domain.product.Product
import woowacourse.shopping.domain.repository.CartRepository
import woowacourse.shopping.domain.repository.HistoryRepository
import woowacourse.shopping.domain.repository.ProductRepository
import woowacourse.shopping.ext.getOrAwaitValue
import woowacourse.shopping.view.detail.DetailActivity.Companion.NO_LAST_SEEN_PRODUCT
import woowacourse.shopping.view.detail.vm.DetailUiState
import woowacourse.shopping.view.detail.vm.DetailViewModel
import woowacourse.shopping.view.main.state.ProductState

@OptIn(ExperimentalCoroutinesApi::class)
@ExtendWith(CoroutinesTestExtension::class)
@ExtendWith(InstantTaskExecutorExtension::class)
class DetailViewModelTest {
    private val productRepository = mockk<ProductRepository>()
    private val cartRepository = mockk<CartRepository>(relaxed = true)
    private val historyRepository = mockk<HistoryRepository>(relaxed = true)

    private lateinit var viewModel: DetailViewModel

    @BeforeEach
    fun setUp() {
        viewModel = DetailViewModel(productRepository, cartRepository, historyRepository)
    }

    @Test
    fun `상품의_상세_정보를_가져온다`() =
        runTest {
            // given
            val productId = 1L
            val product = mockk<Product>()

            coEvery { productRepository.loadProduct(productId) } returns NetworkResult.Success(product)

            // when
            viewModel.load(productId, NO_LAST_SEEN_PRODUCT)

            // then
            val expected = DetailUiState(ProductState(item = product, cartQuantity = Quantity(1)))
            assertEquals(expected, viewModel.uiState.getOrAwaitValue())
        }

    @Test
    fun `load - 최근 본 상품이 있으면 이전에 최근 본 상품의 정보를 가져온다`() =
        runTest {
            // given
            val productId = 1L
            val lastSeenId = 2L
            val product = mockk<Product>(relaxed = true, name = "product")
            val lastSeenProduct = mockk<Product>(relaxed = true, name = "lastSeenProduct")

            coEvery { productRepository.loadProduct(productId) } returns NetworkResult.Success(product)
            coEvery { productRepository.loadProduct(lastSeenId) } returns
                NetworkResult.Success(
                    lastSeenProduct,
                )

            // when
            viewModel.load(productId, lastSeenId)

            // then
            val expected =
                DetailUiState(
                    ProductState(item = product, cartQuantity = Quantity(1)),
                    lastSeenProduct,
                ).lastSeenProduct?.id

            val actual = viewModel.uiState.getOrAwaitValue().lastSeenProduct?.id
            assertEquals(expected, actual)
        }
}
