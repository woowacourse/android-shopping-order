package woowacourse.shopping.ui.shopping

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.runTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension
import woowacourse.shopping.MainDispatcherExtension
import woowacourse.shopping.data.remote.NetworkObserver
import woowacourse.shopping.data.repository.CartRepository
import woowacourse.shopping.data.repository.ProductRepository
import woowacourse.shopping.data.repository.RecentItemRepository
import woowacourse.shopping.data.repository.RecentItemRepositoryImpl
import woowacourse.shopping.mockup.MockCartRepository
import woowacourse.shopping.mockup.MockProductRepository
import woowacourse.shopping.mockup.MockRecentItemDao
import woowacourse.shopping.mockup.createProducts

@OptIn(ExperimentalCoroutinesApi::class)
class ShoppingViewModelTest {
    @JvmField
    @RegisterExtension
    val mainDispatcherExtension = MainDispatcherExtension()

    @Test
    fun `네트워크가 없으면 초기 상품 목록을 불러오지 않는다`() =
        runTest {
            val productRepository = MockProductRepository(products = createProducts(size = 20))
            val viewModel =
                createViewModel(
                    productRepository = productRepository,
                    networkObserver = FakeNetworkObserver(isAvailable = false),
                )

            mainDispatcherExtension.advanceUntilIdle()

            assertThat(viewModel.uiState.value.products).isEmpty()
            assertThat(productRepository.getProductsCallCount).isEqualTo(0)
        }

    @Test
    fun `네트워크가 연결되면 상품 목록을 불러온다`() =
        runTest {
            val viewModel =
                createViewModel(
                    productRepository = MockProductRepository(products = createProducts(size = 20)),
                    networkObserver = FakeNetworkObserver(isAvailable = true),
                )

            mainDispatcherExtension.advanceUntilIdle()

            assertThat(viewModel.uiState.value.products).hasSize(20)
        }

    @Test
    fun `상품 목록 추가 로드 시 기존 목록에 합산하고 마지막 페이지 상태를 갱신한다`() =
        runTest {
            val viewModel =
                createViewModel(
                    productRepository = MockProductRepository(products = createProducts(size = 25)),
                    networkObserver = FakeNetworkObserver(isAvailable = true),
                )
            mainDispatcherExtension.advanceUntilIdle()

            viewModel.loadMore()
            mainDispatcherExtension.advanceUntilIdle()

            assertThat(viewModel.uiState.value.products).hasSize(25)
            assertThat(viewModel.uiState.value.canLoadMore).isFalse()
        }

    private fun createViewModel(
        productRepository: ProductRepository = MockProductRepository(products = createProducts(size = 20)),
        cartRepository: CartRepository = MockCartRepository(),
        recentItemRepository: RecentItemRepository = RecentItemRepositoryImpl(MockRecentItemDao(), productRepository),
        networkObserver: NetworkObserver = FakeNetworkObserver(isAvailable = true),
    ): ShoppingViewModel =
        ShoppingViewModel(
            productRepository = productRepository,
            cartRepository = cartRepository,
            recentItemRepository = recentItemRepository,
            networkObserver = networkObserver,
        )
}

private class FakeNetworkObserver(
    isAvailable: Boolean,
) : NetworkObserver {
    private val state = MutableStateFlow(isAvailable)

    override fun observeNetwork(): Flow<Boolean> = state
}
