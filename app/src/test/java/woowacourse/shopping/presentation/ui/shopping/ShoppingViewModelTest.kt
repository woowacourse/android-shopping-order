package woowacourse.shopping.presentation.ui.shopping

import io.mockk.coEvery
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.junit5.MockKExtension
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import woowacourse.shopping.InstantTaskExecutorExtension
import woowacourse.shopping.cartProducts
import woowacourse.shopping.data.remote.paging.LoadResult
import woowacourse.shopping.domain.repository.CartItemRepository
import woowacourse.shopping.domain.repository.ProductRepository
import woowacourse.shopping.domain.repository.RecentProductRepository
import woowacourse.shopping.getOrAwaitValue
import woowacourse.shopping.presentation.CoroutinesTestExtension
import woowacourse.shopping.presentation.ErrorType
import woowacourse.shopping.presentation.ui.UiState

@ExtendWith(InstantTaskExecutorExtension::class, CoroutinesTestExtension::class, MockKExtension::class)
class ShoppingViewModelTest {
    @RelaxedMockK
    private lateinit var cartItemRepository: CartItemRepository

    @RelaxedMockK
    private lateinit var productRepository: ProductRepository

    @RelaxedMockK
    private lateinit var recentProductRepository: RecentProductRepository

    private lateinit var viewModel: ShoppingViewModel

    @BeforeEach
    fun setUp() {
        coEvery { cartItemRepository.getCount() } returns Result.success(10)
        viewModel =
            ShoppingViewModel(
                productRepository, cartItemRepository, recentProductRepository,
            )
    }

    @Test
    fun `viewModel이 초기화되면 데이터가 20개 불러와진다`() {
        coEvery { productRepository.getAllByPaging(any(), any()) } returns Result.success(LoadResult.Page(0, false, cartProducts))
        viewModel.loadProductsByOffset()
        Thread.sleep(1000)
        assertEquals(viewModel.products.getOrAwaitValue(), UiState.Success(LoadResult.Page(0, false, cartProducts)))
    }

    @Test
    fun `viewModel에서 데이터 로드가 실패하면 Error로 상태가 변화한다`() {
        coEvery { productRepository.getAllByPaging(any(), any()) } returns Result.failure(Throwable())
        viewModel.loadProductsByOffset()
        Thread.sleep(1000)
        assertEquals(viewModel.errorHandler.getOrAwaitValue(1).getContentIfNotHandled(), ErrorType.ERROR_PRODUCT_LOAD)
    }
}
