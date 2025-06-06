package woowacourse.shopping.viewModel.products

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import woowacourse.shopping.data.product.repository.ProductsRepository
import woowacourse.shopping.fixture.PRODUCT1
import woowacourse.shopping.fixture.RECENT_PRODUCTS
import woowacourse.shopping.view.product.ProductsItem
import woowacourse.shopping.view.product.viewModel.RecentProductsViewModel
import woowacourse.shopping.viewModel.common.CoroutinesTestExtension
import woowacourse.shopping.viewModel.common.InstantTaskExecutorExtension
import woowacourse.shopping.viewModel.common.getOrAwaitValue

@ExperimentalCoroutinesApi
@ExtendWith(InstantTaskExecutorExtension::class)
@ExtendWith(CoroutinesTestExtension::class)
@Suppress("FunctionName")
class RecentProductsViewModelTest {
    private val productsRepository: ProductsRepository = mockk()
    private lateinit var viewModel: RecentProductsViewModel

    @OptIn(DelicateCoroutinesApi::class)
    @BeforeEach
    fun setUp() {
        coEvery { productsRepository.getProduct(any()) } returns Result.success(PRODUCT1)
        coEvery { productsRepository.load(any(), any()) } returns Result.success(listOf(PRODUCT1))
        coEvery { productsRepository.updateRecentWatchingProduct(any()) } returns
            Result.success(
                Unit,
            )
        coEvery { productsRepository.getRecentWatchingProducts(any()) } returns
            Result.success(
                RECENT_PRODUCTS,
            )

        viewModel =
            RecentProductsViewModel(
                productsRepository,
            )
    }

    @Test
    fun `최근 본 상품을 갱신할 수 있다`() {
        // given
        val expected =
            ProductsItem.RecentWatchingItem(
                RECENT_PRODUCTS.map {
                    ProductsItem.ProductItem(product = it)
                },
            )
        // when
        viewModel.updateRecentProducts()

        // then
        coVerify { productsRepository.getRecentWatchingProducts(10) }
        assertThat(viewModel.recentProducts.getOrAwaitValue()).isEqualTo(expected)
    }
}
