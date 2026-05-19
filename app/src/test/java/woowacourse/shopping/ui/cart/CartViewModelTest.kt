package woowacourse.shopping.ui.cart

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension
import woowacourse.shopping.MainDispatcherExtension
import woowacourse.shopping.data.repository.RecentItemRepositoryImpl
import woowacourse.shopping.mockup.MockCartRepository
import woowacourse.shopping.mockup.MockProductRepository
import woowacourse.shopping.mockup.MockRecentItemDao
import woowacourse.shopping.mockup.createCartItems
import woowacourse.shopping.mockup.createProducts
import woowacourse.shopping.model.CartItem

@OptIn(ExperimentalCoroutinesApi::class)
class CartViewModelTest {
    @JvmField
    @RegisterExtension
    val mainDispatcherExtension = MainDispatcherExtension()

    @Test
    fun `장바구니 첫 페이지의 상품 5개를 반영한다`() =
        runTest {
            val viewModel = createViewModel(cartItems = createCartItems(size = 6))

            mainDispatcherExtension.advanceUntilIdle()

            val productIds =
                viewModel.uiState.value.items
                    .map { it.product.id }
            assertThat(productIds).containsExactly("1", "2", "3", "4", "5")
            assertThat(viewModel.uiState.value.isCanMoveNext).isTrue()
        }

    @Test
    fun `다음 페이지로 이동하면 다음 상품 목록을 반영한다`() =
        runTest {
            val viewModel = createViewModel(cartItems = createCartItems(size = 6))
            mainDispatcherExtension.advanceUntilIdle()

            viewModel.nextPage()
            mainDispatcherExtension.advanceUntilIdle()

            val productIds =
                viewModel.uiState.value.items
                    .map { it.product.id }
            assertThat(productIds).containsExactly("6")
            assertThat(viewModel.uiState.value.page).isEqualTo(1)
            assertThat(viewModel.uiState.value.isCanMoveNext).isFalse()
        }

    @Test
    fun `이전 페이지로 이동하면 이전 상품 목록이 제공된다`() =
        runTest {
            val viewModel = createViewModel(cartItems = createCartItems(size = 6))
            mainDispatcherExtension.advanceUntilIdle()
            viewModel.nextPage()
            mainDispatcherExtension.advanceUntilIdle()

            viewModel.previousPage()
            mainDispatcherExtension.advanceUntilIdle()

            val productIds =
                viewModel.uiState.value.items
                    .map { it.product.id }
            assertThat(productIds).containsExactly("1", "2", "3", "4", "5")
            assertThat(viewModel.uiState.value.page).isEqualTo(0)
        }

    @Test
    fun `장바구니 상품 개수와 총 수량을 반영한다`() =
        runTest {
            val viewModel = createViewModel(cartItems = createCartItems(size = 2))

            mainDispatcherExtension.advanceUntilIdle()

            assertThat(viewModel.uiState.value.totalCartCount).isEqualTo(2)
            assertThat(viewModel.uiState.value.totalCartQuantity).isEqualTo(2)
        }

    @Test
    fun `상품을 선택하면 선택 목록과 총 가격을 반영한다`() =
        runTest {
            val viewModel = createViewModel(cartItems = createCartItems(size = 2))
            mainDispatcherExtension.advanceUntilIdle()

            viewModel.checkItem("1")
            mainDispatcherExtension.advanceUntilIdle()

            assertThat(viewModel.uiState.value.selectedCartItems).containsExactly("1")
            assertThat(
                viewModel.uiState.value.items
                    .first { it.id == "1" }
                    .isChecked,
            ).isTrue()
            assertThat(viewModel.uiState.value.totalPrice).isEqualTo(2000)
        }

    private fun createViewModel(cartItems: List<CartItem>): CartViewModel {
        val productRepository = MockProductRepository(createProducts(size = 10))

        return CartViewModel(
            cartRepository = MockCartRepository(cartItems),
            recentItemRepository = RecentItemRepositoryImpl(MockRecentItemDao(), productRepository),
            productRepository = productRepository,
        )
    }
}
