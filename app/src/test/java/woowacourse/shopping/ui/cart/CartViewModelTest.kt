package woowacourse.shopping.ui.cart

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
import woowacourse.shopping.model.CartItem
import woowacourse.shopping.model.Money
import woowacourse.shopping.model.Product
import woowacourse.shopping.model.ProductName

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
    fun `상품 삭제 후 해당 페이지에 상품이 없을 경우 이전 페이지로 보정한다`() =
        runTest {
            val viewModel = createViewModel(cartItems = createCartItems(size = 2))

            mainDispatcherExtension.advanceUntilIdle()

            assertThat(viewModel.uiState.value.totalCartCount).isEqualTo(2)
            assertThat(viewModel.uiState.value.totalCartQuantity).isEqualTo(2)
        }

    @Test
    fun `장바구니 총 상품 개수와 총 가격을 반영한다`() =
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
        val productRepository = FakeProductRepository(createProducts(size = 10))

        return CartViewModel(
            cartRepository = FakeCartRepository(cartItems),
            recentItemRepository = RecentItemRepository(TestRecentItemDao(), productRepository),
            productRepository = productRepository,
        )
    }
}

private class FakeCartRepository(
    private var cartItems: List<CartItem>,
) : CartRepository {
    override suspend fun getCartItemsByPage(
        page: Int,
        size: Int,
    ): CartResponseResult {
        val fromIndex = page * size
        val pageItems = cartItems.drop(fromIndex).take(size)
        return CartResponseResult(
            cartItems = pageItems,
            isLastPage = fromIndex + pageItems.size >= cartItems.size,
        )
    }

    override suspend fun setCartItem(
        productId: String,
        quantity: Int,
    ) {
        cartItems =
            cartItems.map { cartItem ->
                if (cartItem.product.id == productId) {
                    cartItem.copy(quantity = quantity)
                } else {
                    cartItem
                }
            }
    }

    override suspend fun deleteItem(cartItemId: String) {
        cartItems = cartItems.filterNot { it.id == cartItemId }
    }

    override suspend fun getCartItemQuantity(productId: String): Int? = cartItems.firstOrNull { it.product.id == productId }?.quantity

    override suspend fun getTotalCartItemQuantity(): Int = cartItems.sumOf { it.quantity }

    override suspend fun getCartItemsCount(): Int = cartItems.size

    override suspend fun getTotalPrice(cartIds: List<String>): Money =
        cartItems
            .filter { it.id in cartIds }
            .fold(Money(0)) { acc, cartItem -> acc + cartItem.getTotalPrice() }
}

private class FakeProductRepository(
    private val products: List<Product>,
) : ProductRepository {
    override suspend fun getProducts(
        category: String,
        page: Int,
        size: Int,
    ): ProductResponseResult {
        val filteredProducts =
            if (category.isBlank()) {
                products
            } else {
                products.filter { it.category == category }
            }
        val fromIndex = page * size
        val pageProducts = filteredProducts.drop(fromIndex).take(size)
        return ProductResponseResult(
            products = pageProducts,
            isLastPage = fromIndex + pageProducts.size >= filteredProducts.size,
        )
    }

    override suspend fun getProductById(id: String): Product =
        products.firstOrNull { it.id == id } ?: throw IllegalArgumentException("Product not found")
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

private fun createCartItems(size: Int): List<CartItem> =
    (1..size).map { id ->
        CartItem(
            id = id.toString(),
            product = createProduct(id = id.toString()),
            quantity = 1,
        )
    }

private fun createProducts(size: Int): List<Product> = (1..size).map { createProduct(id = it.toString()) }

private fun createProduct(id: String): Product =
    Product(
        id = id,
        name = ProductName("product$id"),
        price = Money(2000),
        imageUrl = "image$id",
        category = "book",
    )
