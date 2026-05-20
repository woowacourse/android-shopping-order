package woowacourse.shopping.ui.shopping

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.test.runTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension
import woowacourse.shopping.MainDispatcherExtension
import woowacourse.shopping.data.localdb.dao.RecentItemDao
import woowacourse.shopping.data.localdb.entity.RecentItemEntity
import woowacourse.shopping.data.remote.NetworkObserver
import woowacourse.shopping.data.repository.CartRepository
import woowacourse.shopping.data.repository.CartResponseResult
import woowacourse.shopping.data.repository.ProductRepository
import woowacourse.shopping.data.repository.ProductResponseResult
import woowacourse.shopping.data.repository.RecentItemRepository
import woowacourse.shopping.model.Money
import woowacourse.shopping.model.Product
import woowacourse.shopping.model.ProductName

@OptIn(ExperimentalCoroutinesApi::class)
class ShoppingViewModelTest {
    @JvmField
    @RegisterExtension
    val mainDispatcherExtension = MainDispatcherExtension()

    @Test
    fun `초기 장바구니 관찰 시 네트워크가 없어도 상품을 불러온다`() =
        runTest {
            val productRepository = FakeProductRepository(products = createProducts(size = 20))
            val viewModel =
                createViewModel(
                    productRepository = productRepository,
                    networkObserver = FakeNetworkObserver(isAvailable = false),
                )

            mainDispatcherExtension.advanceUntilIdle()

            assertThat(viewModel.uiState.value.products).hasSize(20)
            assertThat(productRepository.getProductsCallCount).isEqualTo(1)
        }

    @Test
    fun `네트워크가 연결되면 상품 목록을 불러온다`() =
        runTest {
            val viewModel =
                createViewModel(
                    productRepository = FakeProductRepository(products = createProducts(size = 20)),
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
                    productRepository = FakeProductRepository(products = createProducts(size = 25)),
                    networkObserver = FakeNetworkObserver(isAvailable = true),
                )
            mainDispatcherExtension.advanceUntilIdle()

            viewModel.loadMore()
            mainDispatcherExtension.advanceUntilIdle()

            assertThat(viewModel.uiState.value.products).hasSize(25)
            assertThat(viewModel.uiState.value.cartSummary.canLoadMore).isFalse()
        }

    private fun createViewModel(
        productRepository: ProductRepository = FakeProductRepository(products = createProducts(size = 20)),
        cartRepository: CartRepository = FakeCartRepository(),
        recentItemRepository: RecentItemRepository = RecentItemRepository(TestRecentItemDao(), productRepository),
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

private class FakeProductRepository(
    private val products: List<Product>,
) : ProductRepository {
    var getProductsCallCount = 0
        private set

    override suspend fun getProducts(
        category: String,
        page: Int,
        size: Int,
    ): ProductResponseResult {
        getProductsCallCount++
        val fromIndex = page * size
        val pageProducts = products.drop(fromIndex).take(size)
        return ProductResponseResult(
            products = pageProducts,
            isLastPage = fromIndex + pageProducts.size >= products.size,
        )
    }

    override suspend fun getProductById(id: String): Product =
        products.firstOrNull { it.id == id } ?: throw IllegalArgumentException("Product not found")
}

private class FakeCartRepository : CartRepository {
    override suspend fun getCartItemsByPage(
        page: Int,
        size: Int,
    ): CartResponseResult = CartResponseResult(emptyList(), isLastPage = true)

    override suspend fun setCartItem(
        productId: String,
        quantity: Int,
    ) = Unit

    override suspend fun deleteItem(cartItemId: String) = Unit

    override suspend fun getCartItemQuantity(productId: String): Int? = null

    override suspend fun getTotalCartItemQuantity(): Int = 0

    override suspend fun getCartItemsCount(): Int = 0

    override suspend fun getTotalPrice(cartIds: List<String>): Money = Money(0)
}

private class TestRecentItemDao : RecentItemDao {
    private val items = MutableStateFlow<List<RecentItemEntity>>(emptyList())

    override suspend fun upsert(item: RecentItemEntity) {
        items.value = items.value.filterNot { it.id == item.id } + item
    }

    override fun getRecentItems(): Flow<List<RecentItemEntity>> =
        items.map { entities ->
            entities.sortedWith(compareByDescending<RecentItemEntity> { it.timestamp }.thenByDescending { it.id }).take(10)
        }

    override suspend fun getRecentItemById(id: String): RecentItemEntity? = items.value.firstOrNull { it.id == id }

    override suspend fun deleteOldItem() {
        val recentIds =
            items.value
                .sortedWith(compareByDescending<RecentItemEntity> { it.timestamp }.thenByDescending { it.id })
                .take(10)
                .map { it.id }
                .toSet()
        items.value = items.value.filter { it.id in recentIds }
    }

    override suspend fun getLastViewedItem(): RecentItemEntity? =
        items.value.maxWithOrNull(compareBy<RecentItemEntity> { it.timestamp }.thenBy { it.id })
}

private fun createProducts(size: Int): List<Product> =
    (1..size).map { id ->
        createProduct(id = id.toString())
    }

private fun createProduct(id: String): Product =
    Product(
        id = id,
        name = ProductName("product$id"),
        price = Money(2000),
        imageUrl = "image$id",
        category = "book",
    )
