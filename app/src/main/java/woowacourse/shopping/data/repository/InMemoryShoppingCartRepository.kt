package woowacourse.shopping.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import woowacourse.shopping.domain.model.ShoppingCartItem
import woowacourse.shopping.domain.model.ShoppingItem
import woowacourse.shopping.domain.repository.ShoppingCartRepository
import woowacourse.shopping.domain.repository.ShoppingItemRepository

class InMemoryShoppingCartRepository(
    private val shoppingItemRepository: ShoppingItemRepository,
) : ShoppingCartRepository {
    private val mutex = Mutex()
    private val cartEntries = MutableStateFlow<List<CartEntry>>(emptyList())
    private var nextCartItemId: Long = INITIAL_CART_ITEM_ID

    override fun observeShoppingItems(): Flow<List<ShoppingCartItem>> =
        combine(cartEntries, shoppingItemRepository.shoppingItems) { entries, shoppingItems ->
            toShoppingCartItems(
                entries = entries,
                shoppingItems = shoppingItems,
            )
        }

    override suspend fun addIfAbsent(productId: Long) {
        mutex.withLock {
            if (cartEntries.value.any { entry -> entry.productId == productId }) return
            cartEntries.value =
                cartEntries.value + CartEntry(
                    id = nextCartItemId++,
                    productId = productId,
                )
        }
    }

    override suspend fun remove(shoppingCartItem: ShoppingCartItem) {
        mutex.withLock {
            val id = shoppingCartItem.getId()
            val removed = cartEntries.value.removeById(id)
            if (!removed.second) {
                throw IllegalArgumentException("장바구니에 존재하지 않는 상품입니다")
            }
            cartEntries.value = removed.first
        }
    }

    override suspend fun getShoppingItems(): List<ShoppingCartItem> {
        val shoppingItems = shoppingItemRepository.shoppingItems.value
        return toShoppingCartItems(
            entries = cartEntries.value,
            shoppingItems = shoppingItems,
        )
    }

    override suspend fun removeByProductId(productId: Long): Boolean =
        mutex.withLock {
            val beforeSize = cartEntries.value.size
            cartEntries.value = cartEntries.value.filterNot { entry -> entry.productId == productId }
            beforeSize != cartEntries.value.size
        }

    private fun toShoppingCartItems(
        entries: List<CartEntry>,
        shoppingItems: List<ShoppingItem>,
    ): List<ShoppingCartItem> {
        val shoppingItemByProductId =
            shoppingItems.associateBy { shoppingItem -> shoppingItem.getProductId() }
        return entries.mapNotNull { entry ->
            val shoppingItem = shoppingItemByProductId[entry.productId] ?: return@mapNotNull null
            if (shoppingItem.getQuantity() <= 0) return@mapNotNull null
            ShoppingCartItem(
                id = entry.id,
                shoppingItem = shoppingItem,
            )
        }
    }

    private fun List<CartEntry>.removeById(id: Long): Pair<List<CartEntry>, Boolean> {
        var removed = false
        val filtered =
            filterNot { entry ->
                if (entry.id == id) {
                    removed = true
                    true
                } else {
                    false
                }
            }
        return filtered to removed
    }

    private data class CartEntry(
        val id: Long,
        val productId: Long,
    )

    private companion object {
        const val INITIAL_CART_ITEM_ID: Long = 1L
    }
}
