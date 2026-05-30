package woowacourse.shopping.presentation.shopping.viewmodel

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import woowacourse.shopping.domain.usecase.AddToCartUseCase
import woowacourse.shopping.fake.fakeProduct
import woowacourse.shopping.fake.repository.FakeCartRepository
import woowacourse.shopping.fake.repository.FakeProductRepository
import woowacourse.shopping.fake.repository.FakeRecentProductRepository

@OptIn(ExperimentalCoroutinesApi::class)
class ShoppingViewModelTest {
    private val dispatcher = UnconfinedTestDispatcher()
    private lateinit var viewModel: ShoppingViewModel
    private lateinit var productRepository: FakeProductRepository
    private lateinit var cartRepository: FakeCartRepository
    private lateinit var recentProductRepository: FakeRecentProductRepository
    private lateinit var addToCartUseCase: AddToCartUseCase

    @BeforeEach
    fun setUp() {
        Dispatchers.setMain(dispatcher)
        val products = (1L..35L).map { fakeProduct(it) }
        productRepository = FakeProductRepository(products)
        cartRepository = FakeCartRepository()
        recentProductRepository = FakeRecentProductRepository(products)
        viewModel =
            ShoppingViewModel(
                addToCartUseCase = AddToCartUseCase(cartRepository),
                productRepository = productRepository,
                cartRepository = cartRepository,
                recentProductRepository = recentProductRepository,
            )
    }

    @Test
    fun `데이터를 처음 불러오고 나서 page는 1이 된다`() =
        runTest {
            viewModel.loadMore()
            advanceUntilIdle()

            val state = viewModel.uiState.value
            Assertions.assertThat(state.products.size).isEqualTo(20)
            Assertions.assertThat(state.page).isEqualTo(1)
        }

    @Test
    fun `상품 데이터를 불러온 뒤 아직 불러오지 않은 상품이 있다면 canLoadMore는 true를 반환한다`() =
        runTest {
            viewModel.loadMore()
            advanceUntilIdle()

            val state = viewModel.uiState.value
            Assertions.assertThat(state.canLoadMore).isTrue()
        }

    @Test
    fun `상품 데이터를 불러온 뒤 아직 불러오지 않은 상품이 없다면 canLoadMore는 false를 반환한다`() =
        runTest {
            viewModel.loadMore()
            advanceUntilIdle()
            viewModel.loadMore()
            advanceUntilIdle()

            val state = viewModel.uiState.value
            Assertions.assertThat(state.canLoadMore).isFalse()
        }
}
