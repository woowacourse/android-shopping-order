package woowacourse.shopping.ui.detail

import androidx.lifecycle.SavedStateHandle
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
import woowacourse.shopping.data.repository.CartRepository
import woowacourse.shopping.data.repository.CartResponseResult
import woowacourse.shopping.data.repository.ProductRepository
import woowacourse.shopping.data.repository.ProductResponseResult
import woowacourse.shopping.data.repository.RecentItemRepository
import woowacourse.shopping.model.Money
import woowacourse.shopping.model.Product
import woowacourse.shopping.model.ProductName

@OptIn(ExperimentalCoroutinesApi::class)
class DetailViewModelTest {
    @JvmField
    @RegisterExtension
    val mainDispatcherExtension = MainDispatcherExtension()

    @Test
    fun `상품을 불러오면 상품 정보와 수량과 총 가격을 반영한다`() =
        runTest {
            val viewModel = createViewModel()

            mainDispatcherExtension.advanceUntilIdle()

            assertThat(viewModel.uiState.value.product.id).isEqualTo("1")
            assertThat(viewModel.uiState.value.quantity).isEqualTo(1)
            assertThat(viewModel.uiState.value.totalPrice).isEqualTo(2000)
        }

    @Test
    fun `장바구니에 담긴 수량을 상세 수량 초기값으로 사용한다`() =
        runTest {
            val viewModel =
                createViewModel(
                    cartRepository = FakeCartRepository(quantities = mapOf("1" to 3)),
                )

            mainDispatcherExtension.advanceUntilIdle()

            assertThat(viewModel.uiState.value.quantity).isEqualTo(3)
            assertThat(viewModel.uiState.value.totalPrice).isEqualTo(6000)
        }

    @Test
    fun `수량을 변경하면 총 가격도 변경된다`() =
        runTest {
            val viewModel = createViewModel()
            mainDispatcherExtension.advanceUntilIdle()

            viewModel.updateQuantity(2)

            assertThat(viewModel.uiState.value.quantity).isEqualTo(2)
            assertThat(viewModel.uiState.value.totalPrice).isEqualTo(4000)
        }

    @Test
    fun `상세 화면 진입 시 해당 상품을 최근 본 상품으로 저장한다`() =
        runTest {
            val recentItemDao = TestRecentItemDao()

            createViewModel(recentItemDao = recentItemDao)
            mainDispatcherExtension.advanceUntilIdle()

            assertThat(recentItemDao.getRecentItemById("1")).isNotNull()
        }

    private fun createViewModel(
        productRepository: ProductRepository =
            FakeProductRepository(
                products =
                    listOf(
                        createProduct("1"),
                        createProduct("2"),
                    ),
            ),
        cartRepository: CartRepository = FakeCartRepository(),
        recentItemDao: TestRecentItemDao = TestRecentItemDao(),
        savedStateHandle: SavedStateHandle =
            SavedStateHandle(
                mapOf(
                    "id" to "1",
                    "hideRecentItem" to false,
                ),
            ),
    ): DetailViewModel =
        DetailViewModel(
            savedStateHandle = savedStateHandle,
            productRepository = productRepository,
            cartRepository = cartRepository,
            recentItemRepository = RecentItemRepository(recentItemDao),
        )
}

private class FakeProductRepository(
    private val products: List<Product>,
) : ProductRepository {
    override suspend fun getProducts(
        category: String,
        page: Int,
        size: Int,
    ): ProductResponseResult = ProductResponseResult(products, isLastPage = true)

    override suspend fun getProductById(id: String): Product =
        products.firstOrNull { it.id == id } ?: throw IllegalArgumentException("Product not found")
}

private class FakeCartRepository(
    private val quantities: Map<String, Int> = emptyMap(),
) : CartRepository {
    override suspend fun getCartItemsByPage(
        page: Int,
        size: Int,
    ): CartResponseResult = CartResponseResult(emptyList(), isLastPage = true)

    override suspend fun setCartItem(
        productId: String,
        quantity: Int,
    ) = Unit

    override suspend fun deleteItem(cartItemId: String) = Unit

    override suspend fun getCartItemQuantity(productId: String): Int? = quantities[productId]

    override suspend fun getTotalCartItemQuantity(): Int = quantities.values.sum()

    override suspend fun getCartItemsCount(): Int = quantities.size

    override suspend fun getTotalPrice(cartIds: List<String>): Money = Money(0)
}

private class TestRecentItemDao : RecentItemDao {
    private val items = MutableStateFlow<List<RecentItemEntity>>(emptyList())

    override suspend fun upsert(item: RecentItemEntity) {
        items.value = items.value.filterNot { it.id == item.id } + item
    }

    override fun getRecentItems(): Flow<List<RecentItemEntity>> =
        items.map { entities ->
            entities
                .sortedWith(compareByDescending<RecentItemEntity> { it.timestamp }.thenByDescending { it.id })
                .take(10)
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

private fun createProduct(id: String): Product =
    Product(
        id = id,
        name = ProductName("product$id"),
        price = Money(2000),
        imageUrl = "image$id",
        category = "book",
    )
