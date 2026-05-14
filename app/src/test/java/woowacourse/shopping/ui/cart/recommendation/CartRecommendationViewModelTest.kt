@file:Suppress("NonAsciiCharacters")

package woowacourse.shopping.ui.cart.recommendation

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
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import woowacourse.shopping.model.CartItem
import woowacourse.shopping.network.NetworkMonitor
import woowacourse.shopping.ui.cart.SelectedCartOrder
import woowacourse.shopping.ui.cart.SelectedCartOrderItem
import woowacourse.shopping.repository.CartRepository
import woowacourse.shopping.repository.FakeProductRepository
import woowacourse.shopping.repository.FakeRecentProductRepository
import woowacourse.shopping.repository.ProductRepositoryFixture
import woowacourse.shopping.repository.cart.CartPageItem
import woowacourse.shopping.repository.cart.CartPageResult

@OptIn(ExperimentalCoroutinesApi::class)
class CartRecommendationViewModelTest {
    private lateinit var dispatcher: TestDispatcher
    private lateinit var cartRepository: RecordingCartRepository
    private lateinit var recentProductRepository: FakeRecentProductRepository
    private lateinit var viewModel: CartRecommendationViewModel

    private val orderedProduct = ProductRepositoryFixture.products[0]
    private val recommendedProduct = ProductRepositoryFixture.products[1]

    @BeforeEach
    fun setUp() {
        dispatcher = StandardTestDispatcher()
        Dispatchers.setMain(dispatcher)

        cartRepository =
            RecordingCartRepository().apply {
                runTest {
                    setQuantity(orderedProduct.id, 1)
                }
            }
        recentProductRepository =
            FakeRecentProductRepository().apply {
                runTest {
                    recordView(recommendedProduct.id)
                }
            }
        viewModel =
            CartRecommendationViewModel(
                productRepository = FakeProductRepository(ProductRepositoryFixture.products),
                cartRepository = cartRepository,
                recentProductRepository = recentProductRepository,
                networkMonitor = FakeNetworkMonitor(),
            )

        dispatcher.scheduler.advanceUntilIdle()
    }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `추천 상품을 추가하고 바로 주문하면 추가한 상품까지 주문에 포함되고 장바구니에서 제거된다`() =
        runTest(dispatcher.scheduler) {
            viewModel.startOrder(
                SelectedCartOrder(
                    items =
                        listOf(
                            SelectedCartOrderItem(
                                cartItemId = 101L,
                                productId = orderedProduct.id,
                                price = orderedProduct.price.value,
                                quantity = 1,
                            ),
                        ),
                ),
            )
            advanceUntilIdle()

            assertEquals(listOf(101L), viewModel.uiState.value.pendingOrder.cartItemIds)

            viewModel.addRecommendedProduct(recommendedProduct.id)
            advanceUntilIdle()

            assertEquals(
                setOf(101L, 102L),
                viewModel.uiState.value.pendingOrder.cartItemIds.toSet(),
            )
            assertEquals(2, viewModel.uiState.value.pendingOrder.selectedCount)
            assertEquals(
                orderedProduct.price.value + recommendedProduct.price.value,
                viewModel.uiState.value.pendingOrder.totalPrice,
            )

            viewModel.placeOrder()
            advanceUntilIdle()

            assertEquals(
                listOf(listOf(101L, 102L)),
                cartRepository.createdOrders,
            )
            assertTrue(cartRepository.getCartItemsByProductIds(setOf(orderedProduct.id, recommendedProduct.id)).isEmpty())
            assertEquals(1, viewModel.uiState.value.orderCompletedCount)
        }

    private class FakeNetworkMonitor : NetworkMonitor {
        override val isNetworkConnected = MutableStateFlow(true)
    }

    private class RecordingCartRepository : CartRepository {
        val createdOrders = mutableListOf<List<Long>>()

        private val cartItems = linkedMapOf<Long, StoredCartItem>()
        private var nextCartItemId = 101L

        override suspend fun createOrder(cartItemIds: List<Long>) {
            createdOrders += cartItemIds
            val orderedIds = cartItemIds.toSet()
            cartItems.entries.removeIf { (_, item) -> item.cartItemId in orderedIds }
        }

        override suspend fun setQuantity(
            productId: Long,
            quantity: Int,
        ) {
            require(quantity >= 0) { "수량은 0 이상이어야 합니다." }

            if (quantity == 0) {
                cartItems.remove(productId)
                return
            }

            val existing = cartItems[productId]
            cartItems[productId] =
                StoredCartItem(
                    cartItemId = existing?.cartItemId ?: nextCartItemId++,
                    productId = productId,
                    quantity = quantity,
                )
        }

        override suspend fun getCartPage(page: Int, size: Int): CartPageResult {
            val safePage = page.coerceAtLeast(0)
            val safeSize = size.coerceAtLeast(0)
            val items = cartItems.values.toList()
            val totalElements = items.size
            val fromIndex = safePage * safeSize
            val safeFrom = fromIndex.coerceIn(0, totalElements)
            val safeTo = minOf(safeFrom + safeSize, totalElements)

            return CartPageResult(
                items =
                    items.subList(safeFrom, safeTo).map { item ->
                        CartPageItem(
                            cartItemId = item.cartItemId,
                            productId = item.productId,
                            quantity = item.quantity,
                        )
                    },
                totalElements = totalElements,
                totalPages =
                    if (safeSize == 0 || totalElements == 0) {
                        0
                    } else {
                        (totalElements - 1) / safeSize + 1
                    },
                page = safePage,
            )
        }

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

        private data class StoredCartItem(
            val cartItemId: Long,
            val productId: Long,
            val quantity: Int,
        )
    }
}
