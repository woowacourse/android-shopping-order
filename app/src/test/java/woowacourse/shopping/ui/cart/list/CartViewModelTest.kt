@file:Suppress("NonAsciiCharacters")

package woowacourse.shopping.ui.cart.list

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import woowacourse.shopping.network.NetworkMonitor
import woowacourse.shopping.repository.CartRepositoryFixture
import woowacourse.shopping.repository.FakeCartRepository
import woowacourse.shopping.repository.FakeProductRepository

@OptIn(ExperimentalCoroutinesApi::class)
class CartViewModelTest {
    private lateinit var dispatcher: TestDispatcher
    private lateinit var viewModel: CartViewModel

    private val product1 = CartRepositoryFixture.shrimpCracker
    private val product2 = CartRepositoryFixture.sourCandy

    @BeforeEach
    fun setUp() {
        dispatcher = StandardTestDispatcher()
        Dispatchers.setMain(dispatcher)

        val cartRepository =
            FakeCartRepository().apply {
                runTest {
                    setQuantity(product1.id, 1)
                    setQuantity(product2.id, 2)
                }
            }

        viewModel =
            CartViewModel(
                productRepository = FakeProductRepository(CartRepositoryFixture.products),
                cartRepository = cartRepository,
                networkMonitor = FakeNetworkMonitor(),
            )
        dispatcher.scheduler.advanceUntilIdle()
    }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `장바구니에서 특정 상품만 선택할 수 있다`() {
        viewModel.toggleItemSelection(product2.id, isSelected = false)

        val contentState = viewModel.uiState.value.cartListState as CartListUiState.Content

        assertEquals(
            mapOf(
                product1.id to true,
                product2.id to false,
            ),
            contentState.items.associate { it.productId to it.isSelected },
        )
    }

    @Test
    fun `선택된 상품만 주문 대상에 포함된다`() {
        viewModel.toggleItemSelection(product2.id, isSelected = false)

        val selectedCartOrder = viewModel.createSelectedCartOrder()

        assertNotNull(selectedCartOrder)
        assertEquals(listOf(product1.id), selectedCartOrder?.items?.map { it.productId })
        assertEquals(listOf(1), selectedCartOrder?.items?.map { it.quantity })
    }

    @Test
    fun `선택한 상품이 없으면 주문 버튼이 비활성화된다`() {
        viewModel.toggleItemSelection(product1.id, isSelected = false)
        viewModel.toggleItemSelection(product2.id, isSelected = false)

        val contentState = viewModel.uiState.value.cartListState as CartListUiState.Content

        assertNull(viewModel.createSelectedCartOrder())
        assertFalse(contentState.items.any { it.isSelected })
    }

    private class FakeNetworkMonitor : NetworkMonitor {
        override val isNetworkConnected = MutableStateFlow(true)
    }
}
