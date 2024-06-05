package woowacourse.shopping.presentation.shopping.product

import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.junit5.MockKExtension
import io.mockk.verify
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import woowacourse.shopping.domain.entity.Cart
import woowacourse.shopping.domain.product
import woowacourse.shopping.domain.repository.ProductRepository
import woowacourse.shopping.domain.usecase.DecreaseCartProductUseCase
import woowacourse.shopping.domain.usecase.IncreaseCartProductUseCase
import woowacourse.shopping.domain.usecase.LoadCartUseCase
import woowacourse.shopping.presentation.shopping.toShoppingUiModel
import woowacourse.shopping.presentation.util.InstantTaskExecutorExtension
import woowacourse.shopping.presentation.util.getOrAwaitValue

@ExtendWith(InstantTaskExecutorExtension::class, MockKExtension::class)
class ProductListViewModelTest {
    @RelaxedMockK
    private lateinit var productRepository: ProductRepository

    @RelaxedMockK
    private lateinit var loadCartUseCase: LoadCartUseCase

    @RelaxedMockK
    private lateinit var increaseCartProductUseCase: IncreaseCartProductUseCase

    @RelaxedMockK
    private lateinit var decreaseCartProductUseCase: DecreaseCartProductUseCase

    private lateinit var productListViewModel: ProductListViewModel
    private val uiState get() = productListViewModel.uiState.getOrAwaitValue()

    @Test
    @DisplayName("viewModel 이 초기화 될 때 상품이 추가 된다")
    fun `init ViewModel`() {
        // given
        val expectProducts = listOf(product().toShoppingUiModel())
        every { productRepository.loadProducts(currentPage = 0, size = 20) } returns
            Result.success(
                listOf(product()),
            )
        every { productRepository.canLoadMore(page = 1, size = 20) } returns Result.success(false)
        every { loadCartUseCase(listOf(1)) } returns Result.success(Cart())
        every { productRepository.loadRecentProducts(10) } returns Result.success(emptyList())
        // when
        productListViewModel =
            ProductListViewModel(
                productRepository,
                loadCartUseCase,
                increaseCartProductUseCase,
                decreaseCartProductUseCase,
            )
        // when
        verify(exactly = 1) { productRepository.loadProducts(currentPage = 0, size = 20) }
        verify(exactly = 2) { productRepository.canLoadMore(page = 1, size = 20) }
        verify(exactly = 1) { loadCartUseCase(listOf(1)) }
        verify(exactly = 1) { productRepository.loadRecentProducts(10) }
        uiState.products shouldBe expectProducts
    }

    @Test
    @DisplayName("viewModel 이 초기화 될 때 상품이 추가 되고, 다음 페이지를 로드할 수 있으면 더보기 버튼이 추가된다")
    fun `init ViewModel2 - show load more btn`() {
        // given
        val expectProducts = listOf(product().toShoppingUiModel(), ShoppingUiModel.LoadMore)
        every { productRepository.loadProducts(currentPage = 0, size = 20) } returns
            Result.success(
                listOf(product()),
            )
        every {
            productRepository.canLoadMore(page = 1, size = 20)
        } returns Result.success(true)
        every { loadCartUseCase(listOf(1)) } returns Result.success(Cart())
        every { productRepository.loadRecentProducts(10) } returns Result.success(emptyList())
        // when
        productListViewModel =
            ProductListViewModel(
                productRepository,
                loadCartUseCase,
                increaseCartProductUseCase,
                decreaseCartProductUseCase,
            )
        // when
        verify(exactly = 1) { productRepository.loadProducts(currentPage = 0, size = 20) }
        verify(exactly = 2) { productRepository.canLoadMore(page = 1, size = 20) }
        uiState.totalProducts shouldBe expectProducts
    }

    @Test
    @DisplayName("상품을 추가한 후, 더 이상 로드할 수 있는 상품이 없을 때, 추가로 로드할 수 없다")
    fun `init ViewModel3 - cant show load more btn`() {
        // given
        val expectProducts = listOf(product().toShoppingUiModel())
        every { productRepository.loadProducts(currentPage = 0, size = 20) } returns
            Result.success(
                listOf(product()),
            )
        every { productRepository.canLoadMore(page = 1, size = 20) } returns Result.success(false)
        every { loadCartUseCase(listOf(1)) } returns Result.success(Cart())
        every { productRepository.loadRecentProducts(10) } returns Result.success(emptyList())
        // when
        productListViewModel =
            ProductListViewModel(
                productRepository,
                loadCartUseCase,
                increaseCartProductUseCase,
                decreaseCartProductUseCase,
            )
        // then
        verify(exactly = 1) { productRepository.loadProducts(currentPage = 0, size = 20) }
        verify(exactly = 2) { productRepository.canLoadMore(page = 1, size = 20) }
        uiState.totalProducts shouldBe expectProducts
    }
}
