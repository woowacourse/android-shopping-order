package woowacourse.shopping.data.repository

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.model.ShoppingItem
import woowacourse.shopping.domain.repository.ShoppingItemRepository

class InMemoryShoppingItemRepository : ShoppingItemRepository {
    private val mutex = Mutex()
    private val _shoppingItems = MutableStateFlow<List<ShoppingItem>>(emptyList())

    override val shoppingItems: StateFlow<List<ShoppingItem>> = _shoppingItems.asStateFlow()

    override suspend fun upsertProduct(product: Product) {
        mutex.withLock {
            val currentItems = _shoppingItems.value
            val preservedQuantity =
                currentItems
                    .firstOrNull { shoppingItem -> shoppingItem.getProductId() == product.id }
                    ?.getQuantity()
                    ?: 0

            val upsertedItem = ShoppingItem(product = product, quantity = preservedQuantity)
            val updatedItems =
                currentItems.toMutableList().apply {
                    val targetIndex = indexOfFirst { shoppingItem -> shoppingItem.getProductId() == product.id }
                    if (targetIndex >= 0) {
                        set(targetIndex, upsertedItem)
                    } else {
                        add(upsertedItem)
                    }
                }
            _shoppingItems.value = updatedItems
        }
    }

    override suspend fun replaceProducts(products: List<Product>) {
        mutex.withLock {
            val quantityByProductId =
                _shoppingItems.value.associate { shoppingItem ->
                    shoppingItem.getProductId() to shoppingItem.getQuantity()
                }
            _shoppingItems.value =
                products.map { product ->
                    ShoppingItem(
                        product = product,
                        quantity = quantityByProductId[product.id] ?: 0,
                    )
                }
        }
    }

    override suspend fun getQuantity(productId: Long): Int =
        _shoppingItems.value
            .firstOrNull { shoppingItem -> shoppingItem.getProductId() == productId }
            ?.getQuantity()
            ?: throw IllegalArgumentException("해당 상품을 찾을 수 없습니다.")

    override suspend fun plusQuantity(
        productId: Long,
        amount: Int,
    ) {
        if (amount <= 0) return
        updateQuantityByDelta(productId = productId, delta = amount)
    }

    override suspend fun minusQuantity(
        productId: Long,
        amount: Int,
    ) {
        if (amount <= 0) return
        updateQuantityByDelta(productId = productId, delta = -amount)
    }

    private suspend fun updateQuantityByDelta(
        productId: Long,
        delta: Int,
    ) {
        mutex.withLock {
            val updatedItems =
                _shoppingItems.value.map { shoppingItem ->
                    if (shoppingItem.getProductId() != productId) {
                        return@map shoppingItem
                    }
                    val updatedQuantity = (shoppingItem.getQuantity() + delta).coerceAtLeast(0)
                    return@map ShoppingItem(
                        product = shoppingItem.getProduct(),
                        quantity = updatedQuantity,
                    )
                }
            val hasTarget = updatedItems.any { shoppingItem -> shoppingItem.getProductId() == productId }
            if (!hasTarget) {
                throw IllegalArgumentException("해당 상품을 찾을 수 없습니다.")
            }
            _shoppingItems.value = updatedItems
        }
    }
}
