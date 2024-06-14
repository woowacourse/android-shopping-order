package woowacourse.shopping.presentation.shopping.product

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import woowacourse.shopping.domain.product
import woowacourse.shopping.domain.repository.CartRepository
import woowacourse.shopping.domain.repository.ShoppingRepository
import woowacourse.shopping.presentation.shopping.toShoppingUiModel
import woowacourse.shopping.presentation.util.CoroutinesTestExtension
import woowacourse.shopping.presentation.util.InstantTaskExecutorExtension
import woowacourse.shopping.presentation.util.getOrAwaitValue

@ExperimentalCoroutinesApi
@ExtendWith(CoroutinesTestExtension::class)
@ExtendWith(InstantTaskExecutorExtension::class)
class ProductListViewModelTest {
    private lateinit var shoppingRepository: ShoppingRepository

    private lateinit var cartRepository: CartRepository

    private lateinit var productListViewModel: ProductListViewModel
    private val uiState get() = productListViewModel.uiState.getOrAwaitValue()

    @BeforeEach
    fun setUp() {
        shoppingRepository = mockk()
        cartRepository = mockk()
        Dispatchers.setMain(UnconfinedTestDispatcher())
        productListViewModel = ProductListViewModel(shoppingRepository, cartRepository)
    }

    @Test
    fun `viewModel 이 초기화 될 때 상품이 추가 된다`() =
        runTest {
            // given
            val expectProducts = listOf(product().toShoppingUiModel())
            coEvery { shoppingRepository.products(currentPage = 0, size = 20) } returns
                Result.success(
                    listOf(product()),
                )
            coEvery { cartRepository.filterCartProducts(listOf(1)) } returns Result.success(emptyList())
            coEvery { shoppingRepository.recentProducts(10) } returns Result.success(emptyList())
            // when
            productListViewModel.loadProducts()
            // when
//        coVerify(exactly = 1) { shoppingRepository.products(currentPage = 0, size = 20) }
//        coVerify(exactly = 1) { shoppingRepository.canLoadMore(page = 1, size = 20) }
            coVerify(exactly = 1) { cartRepository.filterCartProducts(listOf(1)) }
            coVerify(exactly = 1) { shoppingRepository.recentProducts(10) }
            assertThat(uiState.products).isEqualTo(expectProducts)
        }

//    @Test
//    @DisplayName("viewModel 이 초기화 될 때 상품이 추가 되고, 다음 페이지를 로드할 수 있으면 더보기 버튼이 추가된다")
//    fun `init ViewModel2 - show load more btn`() {
//        // given
//        val expectProducts = listOf(product().toShoppingUiModel(), ShoppingUiModel.LoadMore)
//        every { shoppingRepository.products(currentPage = 0, size = 20) } returns
//            Result.success(
//                listOf(product()),
//            )
//        every {
//            shoppingRepository.canLoadMore(page = 1, size = 20)
//        } returns Result.success(true)
//        every { cartRepository.filterCartProducts(listOf(1)) } returns Result.success(emptyList())
//        every { shoppingRepository.recentProducts(10) } returns Result.success(emptyList())
//        // when
//        productListViewModel = ProductListViewModel(shoppingRepository, cartRepository)
//        // when
//        verify(exactly = 1) { shoppingRepository.products(currentPage = 0, size = 20) }
//        verify(exactly = 1) { shoppingRepository.canLoadMore(page = 1, size = 20) }
//        uiState.totalProducts shouldBe expectProducts
//    }
//
//    @Test
//    @DisplayName("상품을 추가한 후, 더 이상 로드할 수 있는 상품이 없을 때, 추가로 로드할 수 없다")
//    fun `init ViewModel3 - cant show load more btn`() {
//        // given
//        val expectProducts = listOf(product().toShoppingUiModel())
//        every { shoppingRepository.products(currentPage = 0, size = 20) } returns
//            Result.success(
//                listOf(product()),
//            )
//        every { shoppingRepository.canLoadMore(page = 1, size = 20) } returns Result.success(false)
//        every { cartRepository.filterCartProducts(listOf(1)) } returns Result.success(emptyList())
//        every { shoppingRepository.recentProducts(10) } returns Result.success(emptyList())
//        // when
//        productListViewModel = ProductListViewModel(shoppingRepository, cartRepository)
//        // then
//        verify(exactly = 1) { shoppingRepository.products(currentPage = 0, size = 20) }
//        verify(exactly = 1) { shoppingRepository.canLoadMore(page = 1, size = 20) }
//        uiState.totalProducts shouldBe expectProducts
//    }
}
