@file:Suppress("NonAsciiCharacters")

package woowacourse.shopping.ui.shopping

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertInstanceOf
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import woowacourse.shopping.network.NetworkMonitor
import woowacourse.shopping.repository.FakeCartRepository
import woowacourse.shopping.repository.FakeProductRepository
import woowacourse.shopping.repository.FakeRecentProductRepository
import woowacourse.shopping.repository.ProductRepositoryFixture

@OptIn(ExperimentalCoroutinesApi::class)
class ShoppingViewModelTest {
    private lateinit var dispatcher: TestDispatcher

    @BeforeEach
    fun setUp() {
        dispatcher = StandardTestDispatcher()
        Dispatchers.setMain(dispatcher)
    }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `데이터 로딩 전 스켈레톤 UI 상태가 노출된다`() {
        val viewModel = createViewModel()

        assertInstanceOf(ProductListUiState.Loading::class.java, viewModel.uiState.value.productListState)
    }

    @Test
    fun `데이터 로딩 완료 후 상품 목록 상태가 노출된다`() {
        val viewModel = createViewModel()

        dispatcher.scheduler.advanceUntilIdle()

        val contentState =
            assertInstanceOf(
                ProductListUiState.Content::class.java,
                viewModel.uiState.value.productListState,
            )
        assertEquals(20, contentState.products.size)
        assertEquals(ProductRepositoryFixture.products.take(20).map { it.id }, contentState.products.map { it.product.id })
    }

    @Test
    fun `더 보기 요청 시 ViewModel이 다음 페이지를 조회하고 기존 목록 뒤에 누적한다`() {
        val viewModel = createViewModel()

        dispatcher.scheduler.advanceUntilIdle()
        viewModel.loadMore()
        dispatcher.scheduler.advanceUntilIdle()

        val contentState =
            assertInstanceOf(
                ProductListUiState.Content::class.java,
                viewModel.uiState.value.productListState,
            )
        assertEquals(ProductRepositoryFixture.products.size, contentState.products.size)
        assertEquals(ProductRepositoryFixture.products.map { it.id }, contentState.products.map { it.product.id })
    }

    private fun createViewModel(): ShoppingViewModel =
        ShoppingViewModel(
            productRepository = FakeProductRepository(ProductRepositoryFixture.products),
            cartRepository = FakeCartRepository(),
            recentProductRepository = FakeRecentProductRepository(),
            networkMonitor = FakeNetworkMonitor(),
        )

    private class FakeNetworkMonitor : NetworkMonitor {
        override val isNetworkConnected = MutableStateFlow(true)
    }
}
