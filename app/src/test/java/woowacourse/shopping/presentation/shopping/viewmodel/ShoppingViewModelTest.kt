package woowacourse.shopping.presentation.shopping.viewmodel

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.assertj.core.api.Assertions
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import woowacourse.shopping.fake.FakeCartRepository
import woowacourse.shopping.fake.FakeProductRepository
import woowacourse.shopping.fake.fakeProduct

@OptIn(ExperimentalCoroutinesApi::class)
class ShoppingViewModelTest {
    private val dispatcher = UnconfinedTestDispatcher()
    private lateinit var viewModel: ShoppingViewModel
    private lateinit var productRepository: FakeProductRepository
    private lateinit var cartRepository: FakeCartRepository

    @BeforeEach
    fun setUp() {
        Dispatchers.setMain(dispatcher)
        val products = (1L..20L).map { fakeProduct(it) }
        productRepository = FakeProductRepository(products)
        cartRepository = FakeCartRepository(products.associateBy { it.id })
        viewModel =
            ShoppingViewModel(
                productRepository = productRepository,
                cartRepository = cartRepository,
            )
    }

    @Test
    fun `데이터를 불러올 때 제한된 개수의 상품 데이터를 불러온다`() =
        runTest {
            advanceUntilIdle()

            val state = viewModel.uiState.value
            Assertions.assertThat(state.products.size).isEqualTo(20)
            Assertions.assertThat(state.page).isEqualTo(20)
        }

    @Test
    fun `데이터를 처음 불러오고 나서 offset은 처음 불러온 데이터의 크기가 된다`() =
        runTest {
            advanceUntilIdle()

            val state = viewModel.uiState.value
            Assertions.assertThat(state.page).isEqualTo(20)
        }

    @Test
    fun `상품 데이터를 불러온 뒤 아직 불러오지 않은 상품이 있다면 canLoadMore는 true를 반환한다`() =
        runTest {
            advanceUntilIdle()

            val state = viewModel.uiState.value
            Assertions.assertThat(state.canLoadMore).isTrue
        }

    @Test
    fun `상품 데이터를 불러온 뒤 아직 불러오지 않은 상품이 없다면 canLoadMore는 false를 반환한다`() =
        runTest {
            advanceUntilIdle()
            viewModel.loadMoreProducts()
            advanceUntilIdle()

            val state = viewModel.uiState.value
            Assertions.assertThat(state.canLoadMore).isFalse
        }
}
