package woowacourse.shopping.view.home

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import org.junit.jupiter.api.extension.ExtendWith
import woowacourse.shopping.data.repository.FakeCartRepository
import woowacourse.shopping.data.repository.FakeProductRepository
import woowacourse.shopping.data.repository.FakeRecentProductRepository
import woowacourse.shopping.domain.repository.CartRepository
import woowacourse.shopping.domain.repository.ProductRepository
import woowacourse.shopping.domain.repository.RecentProductRepository
import woowacourse.shopping.utils.CoroutinesTestExtension
import woowacourse.shopping.utils.InstantTaskExecutorExtension
import woowacourse.shopping.utils.getOrAwaitValue

@ExperimentalCoroutinesApi
@ExtendWith(CoroutinesTestExtension::class)
@ExtendWith(InstantTaskExecutorExtension::class)
class HomeViewModelTest {
    private lateinit var homeViewModel: HomeViewModel
    private lateinit var productRepository: ProductRepository
    private lateinit var cartRepository: CartRepository
    private lateinit var recentProductRepository: RecentProductRepository

    @BeforeEach
    fun setUp() {
        productRepository = FakeProductRepository(count = 100)
        cartRepository = FakeCartRepository(count = 5)
        recentProductRepository = FakeRecentProductRepository()
        homeViewModel =
            HomeViewModel(
                productRepository = productRepository,
                cartRepository = cartRepository,
                recentProductRepository = recentProductRepository,
            )
    }

    @Test
    fun `초기에 한 페이지만큼의 상품 데이터를 불러온다`() {
        // then
        val actualUiState = homeViewModel.homeProductUiState.getOrAwaitValue()

        assertAll(
            { assertThat(actualUiState.productItems).hasSize(20) },
            { assertThat(actualUiState.currentPageNumber).isEqualTo(1) },
            { assertThat(actualUiState.loadMoreAvailable).isEqualTo(true) },
        )
    }

    @Test
    fun `상품 데이터를 추가적으로 불러올 수 있다`() =
        runTest {
            // when
            homeViewModel.loadMore()

            // then
            val actualUiState = homeViewModel.homeProductUiState.getOrAwaitValue()
            assertThat(actualUiState.productItems).hasSize(40)
        }

    @Test
    fun `장바구니에 추가된 상품 데이터의 수량을 증가시킬 수 있다`() =
        runTest {
            // when
            homeViewModel.addQuantity(1)

            // then
            val actualUiState = homeViewModel.homeProductUiState.getOrAwaitValue()
            val actualQuantity = actualUiState.cartItems.first { it.cartItemId == 1 }.quantity
            assertThat(actualQuantity).isEqualTo(2)
        }

    @Test
    fun `장바구니에 추가된 상품 데이터의 수량을 감소시킬 수 있다`() =
        runTest {
            // when
            homeViewModel.addQuantity(1)
            homeViewModel.subtractQuantity(1)

            // then
            val actualUiState = homeViewModel.homeProductUiState.getOrAwaitValue()
            val actualQuantity = actualUiState.cartItems.first { it.cartItemId == 1 }.quantity
            assertThat(actualQuantity).isEqualTo(1)
        }

    @Test
    fun `장바구니에 담은 수량이 0이 되면 장바구니 목록에서 삭제된다`() =
        runTest {
            // when
            homeViewModel.subtractQuantity(1)

            // then
            val actualUiState = homeViewModel.homeProductUiState.getOrAwaitValue()
            val actualQuantity = actualUiState.cartItems.firstOrNull { it.cartItemId == 1 }
            assertThat(actualQuantity).isNull()
        }

    @Test
    fun `최근 본 상품들을 불러올 수 있다`() {
        // when
        homeViewModel.loadRecentItems()

        // then
        val actualUiState = homeViewModel.recentProductUiState.getOrAwaitValue()
        assertThat(actualUiState.productItems).hasSize(10)
    }
}
