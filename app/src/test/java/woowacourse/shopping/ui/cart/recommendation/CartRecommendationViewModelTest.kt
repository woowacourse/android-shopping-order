@file:Suppress("NonAsciiCharacters")

package woowacourse.shopping.ui.cart.recommendation

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
import woowacourse.shopping.model.Money
import woowacourse.shopping.model.Product
import woowacourse.shopping.network.NetworkMonitor
import woowacourse.shopping.repository.CartRepository
import woowacourse.shopping.repository.FakeProductRepository
import woowacourse.shopping.repository.FakeRecentProductRepository
import woowacourse.shopping.repository.ProductRepositoryFixture
import woowacourse.shopping.repository.query.CartPageItem
import woowacourse.shopping.repository.query.CartPageResult
import woowacourse.shopping.ui.cart.SelectedCartOrder
import woowacourse.shopping.ui.cart.SelectedCartOrderItem

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
                selectedCartOrderOf(orderedProduct),
            )
            advanceUntilIdle()

            assertEquals(listOf(101L), viewModel.uiState.value.pendingOrder.cartItemIds)

            viewModel.addRecommendedProduct(recommendedProduct.id)
            advanceUntilIdle()

            assertEquals(
                setOf(101L, 102L),
                viewModel.uiState.value.pendingOrder.cartItemIds
                    .toSet(),
            )
            assertEquals(2, viewModel.uiState.value.pendingOrder.selectedCount)
            assertEquals(
                orderedProduct.price.value + recommendedProduct.price.value,
                viewModel.uiState.value.pendingOrder.totalPrice,
            )

            val event = async { viewModel.events.first() }
            viewModel.placeOrder()
            advanceUntilIdle()

            assertEquals(
                listOf(listOf(101L, 102L)),
                cartRepository.createdOrders,
            )
            assertTrue(cartRepository.getCartItemsByProductIds(setOf(orderedProduct.id, recommendedProduct.id)).isEmpty())
            assertEquals(CartRecommendationEvent.OrderCompleted, event.await())
        }

    @Test
    fun `가장 최근에 본 상품의 카테고리 상품만 추천한다`() =
        runTest(dispatcher.scheduler) {
            val dessertProducts =
                listOf(
                    product(id = 1L, category = "dessert"),
                    product(id = 2L, category = "dessert"),
                )
            val fruitProduct = product(id = 3L, category = "fruit")

            cartRepository = RecordingCartRepository()
            cartRepository.setQuantity(fruitProduct.id, 1)
            recentProductRepository =
                FakeRecentProductRepository().apply {
                    recordView(1L)
                }
            viewModel =
                CartRecommendationViewModel(
                    productRepository = FakeProductRepository(dessertProducts + fruitProduct),
                    cartRepository = cartRepository,
                    recentProductRepository = recentProductRepository,
                    networkMonitor = FakeNetworkMonitor(),
                )

            viewModel.startOrder(selectedCartOrderOf(fruitProduct))
            advanceUntilIdle()

            assertEquals(
                listOf(1L, 2L),
                viewModel.uiState.value.recommendedProducts
                    .map { it.product.id },
            )
            assertEquals(
                setOf("dessert"),
                viewModel.uiState.value.recommendedProducts
                    .map { it.product.category }
                    .toSet(),
            )
        }

    @Test
    fun `해당 카테고리 상품이 10개 미만이면 가능한 개수만 추천한다`() =
        runTest(dispatcher.scheduler) {
            val dessertProducts =
                listOf(
                    product(id = 1L, category = "dessert"),
                    product(id = 2L, category = "dessert"),
                    product(id = 3L, category = "dessert"),
                )
            val selectedOrderProduct = product(id = 100L, category = "fruit")

            cartRepository = RecordingCartRepository()
            cartRepository.setQuantity(selectedOrderProduct.id, 1)
            recentProductRepository =
                FakeRecentProductRepository().apply {
                    recordView(1L)
                }
            viewModel =
                CartRecommendationViewModel(
                    productRepository = FakeProductRepository(dessertProducts + selectedOrderProduct),
                    cartRepository = cartRepository,
                    recentProductRepository = recentProductRepository,
                    networkMonitor = FakeNetworkMonitor(),
                )

            viewModel.startOrder(selectedCartOrderOf(selectedOrderProduct))
            advanceUntilIdle()

            assertEquals(3, viewModel.uiState.value.recommendedProducts.size)
            assertEquals(
                listOf(1L, 2L, 3L),
                viewModel.uiState.value.recommendedProducts
                    .map { it.product.id },
            )
        }

    @Test
    fun `장바구니에 이미 담긴 상품을 제외하고 최대 10개까지만 추천한다`() =
        runTest(dispatcher.scheduler) {
            val dessertProducts = (1L..12L).map { id -> product(id = id, category = "dessert") }
            val selectedOrderProduct = product(id = 100L, category = "fruit")

            cartRepository = RecordingCartRepository()
            cartRepository.setQuantity(selectedOrderProduct.id, 1)
            cartRepository.setQuantity(1L, 1)
            cartRepository.setQuantity(2L, 1)
            recentProductRepository =
                FakeRecentProductRepository().apply {
                    recordView(1L)
                }
            viewModel =
                CartRecommendationViewModel(
                    productRepository = FakeProductRepository(dessertProducts + selectedOrderProduct),
                    cartRepository = cartRepository,
                    recentProductRepository = recentProductRepository,
                    networkMonitor = FakeNetworkMonitor(),
                )

            viewModel.startOrder(selectedCartOrderOf(selectedOrderProduct))
            advanceUntilIdle()

            assertEquals(
                (3L..12L).toList(),
                viewModel.uiState.value.recommendedProducts
                    .map { it.product.id },
            )
            assertEquals(10, viewModel.uiState.value.recommendedProducts.size)
        }

    @Test
    fun `추천 가능한 상품이 없으면 빈 목록을 노출한다`() =
        runTest(dispatcher.scheduler) {
            val dessertProduct = product(id = 1L, category = "dessert")
            val selectedOrderProduct = product(id = 2L, category = "fruit")

            cartRepository = RecordingCartRepository()
            cartRepository.setQuantity(selectedOrderProduct.id, 1)
            cartRepository.setQuantity(dessertProduct.id, 1)
            recentProductRepository =
                FakeRecentProductRepository().apply {
                    recordView(dessertProduct.id)
                }
            viewModel =
                CartRecommendationViewModel(
                    productRepository = FakeProductRepository(listOf(dessertProduct, selectedOrderProduct)),
                    cartRepository = cartRepository,
                    recentProductRepository = recentProductRepository,
                    networkMonitor = FakeNetworkMonitor(),
                )

            viewModel.startOrder(selectedCartOrderOf(selectedOrderProduct))
            advanceUntilIdle()

            assertTrue(
                viewModel.uiState.value.recommendedProducts
                    .isEmpty(),
            )
        }

    @Test
    fun `추천 상품을 장바구니에 추가할 수 있다`() =
        runTest(dispatcher.scheduler) {
            viewModel.startOrder(selectedCartOrderOf(orderedProduct))
            advanceUntilIdle()

            viewModel.addRecommendedProduct(recommendedProduct.id)
            advanceUntilIdle()

            assertEquals(
                listOf(CartItem(productId = recommendedProduct.id, quantity = 1)),
                cartRepository.getCartItemsByProductIds(setOf(recommendedProduct.id)),
            )
        }

    @Test
    fun `추천 상품 추가 후 장바구니 상태가 갱신된다`() =
        runTest(dispatcher.scheduler) {
            viewModel.startOrder(selectedCartOrderOf(orderedProduct))
            advanceUntilIdle()

            viewModel.addRecommendedProduct(recommendedProduct.id)
            advanceUntilIdle()

            assertEquals(2, viewModel.uiState.value.pendingOrder.selectedCount)
            assertEquals(
                setOf(101L, 102L),
                viewModel.uiState.value.pendingOrder.cartItemIds
                    .toSet(),
            )
            assertTrue(
                viewModel.uiState.value.recommendedProducts
                    .any { it.product.id == recommendedProduct.id },
            )
        }

    private fun selectedCartOrderOf(product: Product): SelectedCartOrder =
        SelectedCartOrder(
            items =
                listOf(
                    SelectedCartOrderItem(
                        cartItemId = 101L,
                        productId = product.id,
                        price = product.price.value,
                        quantity = 1,
                    ),
                ),
        )

    private fun product(
        id: Long,
        category: String,
    ): Product =
        Product(
            id = id,
            name = "상품$id",
            price = Money((10_000 + id).toInt()),
            imageUrl = "https://example.com/product-$id.png",
            category = category,
        )

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

        override suspend fun getCartPage(
            page: Int,
            size: Int,
        ): CartPageResult {
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
