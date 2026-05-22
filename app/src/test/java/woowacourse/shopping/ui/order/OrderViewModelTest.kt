@file:Suppress("NonAsciiCharacters")

package woowacourse.shopping.ui.order

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import woowacourse.shopping.model.CartItem
import woowacourse.shopping.network.NetworkMonitor
import woowacourse.shopping.repository.CartRepository
import woowacourse.shopping.repository.CartRepositoryFixture
import woowacourse.shopping.repository.query.CartPageResult
import woowacourse.shopping.ui.cart.SelectedCartOrder
import woowacourse.shopping.ui.cart.SelectedCartOrderItem

@OptIn(ExperimentalCoroutinesApi::class)
class OrderViewModelTest {
    private lateinit var dispatcher: TestDispatcher
    private lateinit var cartRepository: RecordingCartRepository
    private lateinit var viewModel: OrderViewModel

    private val shrimpCracker = CartRepositoryFixture.shrimpCracker
    private val sourCandy = CartRepositoryFixture.sourCandy

    @BeforeEach
    fun setUp() {
        dispatcher = StandardTestDispatcher()
        Dispatchers.setMain(dispatcher)

        cartRepository = RecordingCartRepository()
        viewModel =
            OrderViewModel(
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
    fun `선택된 장바구니 상품으로 결제 요약을 구성한다`() {
        viewModel.startOrder(
            SelectedCartOrder(
                items =
                    listOf(
                        SelectedCartOrderItem(
                            cartItemId = 101L,
                            productId = shrimpCracker.id,
                            price = shrimpCracker.price.value,
                            quantity = 2,
                        ),
                        SelectedCartOrderItem(
                            cartItemId = 102L,
                            productId = sourCandy.id,
                            price = sourCandy.price.value,
                            quantity = 1,
                        ),
                    ),
            ),
        )

        val uiState = viewModel.uiState.value

        assertEquals(7_500L, uiState.priceSummary.items[0].price)
        assertEquals(0L, uiState.priceSummary.items[1].price)
        assertEquals(3_000L, uiState.priceSummary.items[2].price)
        assertEquals(10_500L, uiState.priceSummary.totalPaymentPrice)
        assertTrue(uiState.isPaymentEnabled)
    }

    @Test
    fun `결제하기 버튼을 누르면 주문을 생성하고 완료 이벤트를 보낸다`() =
        runTest(dispatcher.scheduler) {
            cartRepository.setCartItems(
                listOf(
                    StoredCartItem(cartItemId = 101L, productId = shrimpCracker.id, quantity = 2),
                    StoredCartItem(cartItemId = 102L, productId = sourCandy.id, quantity = 1),
                ),
            )
            viewModel.startOrder(
                SelectedCartOrder(
                    items =
                        listOf(
                            SelectedCartOrderItem(
                                cartItemId = 101L,
                                productId = shrimpCracker.id,
                                price = shrimpCracker.price.value,
                                quantity = 2,
                            ),
                            SelectedCartOrderItem(
                                cartItemId = 102L,
                                productId = sourCandy.id,
                                price = sourCandy.price.value,
                                quantity = 1,
                            ),
                        ),
                ),
            )

            val event = async { viewModel.events.first() }

            viewModel.placeOrder()
            advanceUntilIdle()

            assertEquals(listOf(listOf(101L, 102L)), cartRepository.createdOrders)
            assertTrue(cartRepository.getCartItemsByProductIds(setOf(shrimpCracker.id, sourCandy.id)).isEmpty())
            assertEquals(OrderEvent.OrderCompleted, event.await())
            assertEquals(0L, viewModel.uiState.value.priceSummary.totalPaymentPrice)
        }

    private class FakeNetworkMonitor : NetworkMonitor {
        override val isNetworkConnected = MutableStateFlow(true)
    }

    private class RecordingCartRepository : CartRepository {
        val createdOrders = mutableListOf<List<Long>>()
        private val cartItems = linkedMapOf<Long, StoredCartItem>()

        fun setCartItems(items: List<StoredCartItem>) {
            cartItems.clear()
            items.forEach { item ->
                cartItems[item.productId] = item
            }
        }

        override suspend fun createOrder(cartItemIds: List<Long>) {
            createdOrders += cartItemIds
            val orderedIds = cartItemIds.toSet()
            cartItems.entries.removeIf { (_, item) -> item.cartItemId in orderedIds }
        }

        override suspend fun setQuantity(
            productId: Long,
            quantity: Int,
        ) {
            if (quantity == 0) {
                cartItems.remove(productId)
                return
            }

            cartItems[productId] =
                StoredCartItem(
                    cartItemId = cartItems[productId]?.cartItemId ?: productId,
                    productId = productId,
                    quantity = quantity,
                )
        }

        override suspend fun getCartPage(
            page: Int,
            size: Int,
        ): CartPageResult = CartPageResult(emptyList(), 0, 0, 0)

        override suspend fun getCartItemsByProductIds(productIds: Set<Long>): List<CartItem> =
            cartItems.values
                .filter { it.productId in productIds }
                .map { item ->
                    CartItem(
                        productId = item.productId,
                        quantity = item.quantity,
                    )
                }

        override suspend fun count(): Int = cartItems.size
    }

    private data class StoredCartItem(
        val cartItemId: Long,
        val productId: Long,
        val quantity: Int,
    )
}
