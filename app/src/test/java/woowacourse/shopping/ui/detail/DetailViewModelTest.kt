package woowacourse.shopping.ui.detail

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension
import woowacourse.shopping.MainDispatcherExtension
import woowacourse.shopping.data.localdb.entity.RecentItemEntity
import woowacourse.shopping.data.repository.CartRepository
import woowacourse.shopping.data.repository.ProductRepository
import woowacourse.shopping.data.repository.RecentItemRepositoryImpl
import woowacourse.shopping.mockup.MockCartRepository
import woowacourse.shopping.mockup.MockProductRepository
import woowacourse.shopping.mockup.MockRecentItemDao
import woowacourse.shopping.mockup.createCartItem
import woowacourse.shopping.mockup.createProduct

@OptIn(ExperimentalCoroutinesApi::class)
class DetailViewModelTest {
    @JvmField
    @RegisterExtension
    val mainDispatcherExtension = MainDispatcherExtension()

    @Test
    fun `상품을 불러오면 상품 정보와 수량과 총 가격을 반영한다`() =
        runTest {
            val viewModel = createViewModel(id = "1")

            mainDispatcherExtension.advanceUntilIdle()

            assertThat(viewModel.uiState.value.product.id).isEqualTo("1")
            assertThat(viewModel.uiState.value.quantity).isEqualTo(1)
            assertThat(viewModel.uiState.value.totalPrice).isEqualTo(2000)
        }

    @Test
    fun `장바구니에 담긴 수량을 상세 수량 초기값으로 사용한다`() =
        runTest {
            val product = createProduct(id = "1")
            val viewModel =
                createViewModel(
                    id = "1",
                    cartRepository = MockCartRepository(listOf(createCartItem(id = "1", product = product, quantity = 3))),
                )

            mainDispatcherExtension.advanceUntilIdle()

            assertThat(viewModel.uiState.value.quantity).isEqualTo(3)
            assertThat(viewModel.uiState.value.totalPrice).isEqualTo(6000)
        }

    @Test
    fun `마지막으로 본 상품을 최근 본 상품으로 제공한다`() =
        runTest {
            val recentItemDao = MockRecentItemDao()
            recentItemDao.insert(RecentItemEntity(id = "2", timestamp = 100L))

            val viewModel = createViewModel(id = "1", recentItemDao = recentItemDao)
            mainDispatcherExtension.advanceUntilIdle()

            assertThat(viewModel.uiState.value.recentItem?.id).isEqualTo("2")
        }

    @Test
    fun `최근 본 상품 숨김 옵션이 참이면 최근 본 상품을 제공하지 않는다`() =
        runTest {
            val recentItemDao = MockRecentItemDao()
            recentItemDao.insert(RecentItemEntity(id = "2", timestamp = 100L))

            val viewModel =
                createViewModel(
                    id = "1",
                    hideRecentItem = true,
                    recentItemDao = recentItemDao,
                )
            mainDispatcherExtension.advanceUntilIdle()

            assertThat(viewModel.uiState.value.recentItem).isNull()
        }

    @Test
    fun `수량을 변경하면 총 가격도 변경된다`() =
        runTest {
            val viewModel = createViewModel(id = "1")
            mainDispatcherExtension.advanceUntilIdle()

            viewModel.updateQuantity(2)

            assertThat(viewModel.uiState.value.quantity).isEqualTo(2)
            assertThat(viewModel.uiState.value.totalPrice).isEqualTo(4000)
        }

    @Test
    fun `상세 화면 진입 시 해당 상품을 최근 본 상품으로 저장한다`() =
        runTest {
            val recentItemDao = MockRecentItemDao()

            createViewModel(id = "1", recentItemDao = recentItemDao)
            mainDispatcherExtension.advanceUntilIdle()

            assertThat(recentItemDao.getRecentItemById("1")).isNotNull()
        }

    private fun createViewModel(
        id: String,
        hideRecentItem: Boolean = false,
        productRepository: ProductRepository = MockProductRepository(products = listOf(createProduct("1"), createProduct("2"))),
        cartRepository: CartRepository = MockCartRepository(),
        recentItemDao: MockRecentItemDao = MockRecentItemDao(),
    ): DetailViewModel =
        DetailViewModel(
            id = id,
            hideRecentItem = hideRecentItem,
            productRepository = productRepository,
            cartRepository = cartRepository,
            recentItemRepository = RecentItemRepositoryImpl(recentItemDao, productRepository),
        )
}
