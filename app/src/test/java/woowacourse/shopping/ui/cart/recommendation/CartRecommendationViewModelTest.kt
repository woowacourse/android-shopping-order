@file:Suppress("NonAsciiCharacters")

package woowacourse.shopping.ui.cart.recommendation

import CartRecommendation
import androidx.lifecycle.SavedStateHandle
import androidx.navigation.toRoute
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkAll
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.runBlocking
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
import woowacourse.shopping.model.cart.CartItem
import woowacourse.shopping.model.product.Money
import woowacourse.shopping.model.product.Product
import woowacourse.shopping.network.NetworkMonitor
import woowacourse.shopping.repository.CartRepository
import woowacourse.shopping.repository.FakeProductRepository
import woowacourse.shopping.repository.FakeRecentProductRepository
import woowacourse.shopping.repository.ProductRepositoryFixture
import woowacourse.shopping.repository.query.CartPageItem
import woowacourse.shopping.repository.query.CartPageResult

@OptIn(ExperimentalCoroutinesApi::class)
class CartRecommendationViewModelTest {
    private lateinit var dispatcher: TestDispatcher
    private lateinit var cartRepository: RecordingCartRepository
    private lateinit var recentProductRepository: FakeRecentProductRepository
    private lateinit var viewModel: CartRecommendationViewModel

    private val orderedProduct = ProductRepositoryFixture.products[0] // ID: 1, Category: dessert
    private val recommendedProduct = ProductRepositoryFixture.products[1] // ID: 2, Category: fruit

    @BeforeEach
    fun setUp() {
        dispatcher = StandardTestDispatcher()
        Dispatchers.setMain(dispatcher)

        mockkStatic("androidx.navigation.SavedStateHandleKt")

        cartRepository = RecordingCartRepository()
        runBlocking {
            cartRepository.setQuantity(orderedProduct.id, 1).getOrThrow()
        }

        recentProductRepository = FakeRecentProductRepository()
        runBlocking {
            recentProductRepository.recordView(recommendedProduct.id)
        }

        val savedStateHandle = mockk<SavedStateHandle>(relaxed = true)
        every { savedStateHandle.toRoute<CartRecommendation>() } returns
            CartRecommendation(selectedCartItemIds = longArrayOf(101L))

        viewModel =
            CartRecommendationViewModel(
                savedStateHandle = savedStateHandle,
                productRepository = FakeProductRepository(ProductRepositoryFixture.products),
                cartRepository = cartRepository,
                recentProductRepository = recentProductRepository,
                networkMonitor = FakeNetworkMonitor(),
            )

        dispatcher.scheduler.advanceUntilIdle()
    }

    @AfterEach
    fun tearDown() {
        unmockkAll()
        Dispatchers.resetMain()
    }

    @Test
    fun `추천 상품을 추가하고 바로 주문하면 추가한 상품까지 주문에 포함되고 장바구니에서 제거된다`() =
        runTest(dispatcher.scheduler) {
            advanceUntilIdle()

            viewModel.addRecommendedProduct(recommendedProduct.id)
            advanceUntilIdle()

            assertEquals(
                setOf(101L),
                viewModel.uiState.value.pendingOrder.cartItemIds
                    .toSet(),
            )

            viewModel.placeOrder()
            advanceUntilIdle()

            assertEquals(
                listOf(listOf(101L, 102L)),
                cartRepository.createdOrders,
            )
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
            val savedStateHandle = mockk<SavedStateHandle>(relaxed = true)
            every { savedStateHandle.toRoute<CartRecommendation>() } returns
                CartRecommendation(selectedCartItemIds = longArrayOf(101L))

            viewModel =
                CartRecommendationViewModel(
                    savedStateHandle = savedStateHandle,
                    productRepository = FakeProductRepository(dessertProducts + fruitProduct),
                    cartRepository = cartRepository,
                    recentProductRepository = recentProductRepository,
                    networkMonitor = FakeNetworkMonitor(),
                )

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
            val savedStateHandle = mockk<SavedStateHandle>(relaxed = true)
            every { savedStateHandle.toRoute<CartRecommendation>() } returns
                CartRecommendation(selectedCartItemIds = longArrayOf(101L))

            viewModel =
                CartRecommendationViewModel(
                    savedStateHandle = savedStateHandle,
                    productRepository = FakeProductRepository(dessertProducts + selectedOrderProduct),
                    cartRepository = cartRepository,
                    recentProductRepository = recentProductRepository,
                    networkMonitor = FakeNetworkMonitor(),
                )

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
            val savedStateHandle = mockk<SavedStateHandle>(relaxed = true)
            every { savedStateHandle.toRoute<CartRecommendation>() } returns
                CartRecommendation(selectedCartItemIds = longArrayOf(101L))

            viewModel =
                CartRecommendationViewModel(
                    savedStateHandle = savedStateHandle,
                    productRepository = FakeProductRepository(dessertProducts + selectedOrderProduct),
                    cartRepository = cartRepository,
                    recentProductRepository = recentProductRepository,
                    networkMonitor = FakeNetworkMonitor(),
                )

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
            cartRepository.setQuantity(selectedOrderProduct.id, 1).getOrThrow()
            cartRepository.setQuantity(dessertProduct.id, 1).getOrThrow()
            recentProductRepository =
                FakeRecentProductRepository().apply {
                    recordView(dessertProduct.id)
                }
            val savedStateHandle = mockk<SavedStateHandle>(relaxed = true)
            every { savedStateHandle.toRoute<CartRecommendation>() } returns
                CartRecommendation(selectedCartItemIds = longArrayOf(101L))

            viewModel =
                CartRecommendationViewModel(
                    savedStateHandle = savedStateHandle,
                    productRepository = FakeProductRepository(listOf(dessertProduct, selectedOrderProduct)),
                    cartRepository = cartRepository,
                    recentProductRepository = recentProductRepository,
                    networkMonitor = FakeNetworkMonitor(),
                )

            advanceUntilIdle()

            assertTrue(
                viewModel.uiState.value.recommendedProducts
                    .isEmpty(),
            )
        }

    @Test
    fun `추천 상품을 추가해도 실제 장바구니(DB)에는 즉시 저장되지 않는다`() =
        runTest(dispatcher.scheduler) {
            advanceUntilIdle()

            viewModel.addRecommendedProduct(recommendedProduct.id)
            advanceUntilIdle()

            assertTrue(
                cartRepository
                    .getCartItemsByProductIds(setOf(recommendedProduct.id))
                    .getOrThrow()
                    .isEmpty(),
            )
        }

    @Test
    fun `추천 상품 추가 후 장바구니 상태가 갱신된다`() =
        runTest(dispatcher.scheduler) {
            advanceUntilIdle()

            viewModel.addRecommendedProduct(recommendedProduct.id)
            advanceUntilIdle()

            assertEquals(
                setOf(101L),
                viewModel.uiState.value.pendingOrder.cartItemIds
                    .toSet(),
            )
            assertEquals(2, viewModel.uiState.value.pendingOrder.selectedCount)
            assertEquals(
                orderedProduct.price.value + recommendedProduct.price.value,
                viewModel.uiState.value.pendingOrder.totalPrice,
            )
        }

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

        override suspend fun createOrder(cartItemIds: List<Long>): Result<Unit> {
            createdOrders += cartItemIds
            val orderedIds = cartItemIds.toSet()
            cartItems.entries.removeIf { (_, item) -> item.cartItemId in orderedIds }
            return Result.success(Unit)
        }

        override suspend fun setQuantity(
            productId: Long,
            quantity: Int,
        ): Result<Unit> {
            if (quantity < 0) {
                return Result.failure(
                    IllegalArgumentException("수량은 0 이상이어야 합니다."),
                )
            }

            if (quantity == 0) {
                cartItems.remove(productId)
                return Result.success(Unit)
            }

            val existing = cartItems[productId]

            cartItems[productId] =
                StoredCartItem(
                    cartItemId = existing?.cartItemId ?: nextCartItemId++,
                    productId = productId,
                    quantity = quantity,
                )

            return Result.success(Unit)
        }

        override suspend fun getCartPage(
            page: Int,
            size: Int,
        ): Result<CartPageResult> {
            val safePage = page.coerceAtLeast(0)
            val safeSize = size.coerceAtLeast(0)
            val items = cartItems.values.toList()
            val totalElements = items.size
            val fromIndex = safePage * safeSize
            val safeFrom = fromIndex.coerceIn(0, totalElements)
            val safeTo = minOf(safeFrom + safeSize, totalElements)

            return Result.success(
                CartPageResult(
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
                ),
            )
        }

        override suspend fun getCartItemsByProductIds(productIds: Set<Long>): Result<List<CartItem>> =
            Result.success(
                cartItems.values
                    .filter { it.productId in productIds }
                    .map { item ->
                        CartItem(
                            productId = item.productId,
                            quantity = item.quantity,
                        )
                    },
            )

        override suspend fun count(): Result<Int> = Result.success(cartItems.size)

        private data class StoredCartItem(
            val cartItemId: Long,
            val productId: Long,
            val quantity: Int,
        )
    }
}
