package woowacourse.shopping.data.repository.item

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.model.ShoppingItem
import woowacourse.shopping.domain.repository.ShoppingItemRepository

class ShoppingItemRepositoryImpl : ShoppingItemRepository {
    private val _shoppingItems = MutableStateFlow<List<ShoppingItem>>(emptyList())
    override val shoppingItems: StateFlow<List<ShoppingItem>> = _shoppingItems.asStateFlow()

    override suspend fun upsertProducts(products: List<Product>) {
        if (products.isEmpty()) return

        val incomingProductById = products.associateBy { product -> product.id }
        val currentItemsByProductId =
            _shoppingItems.value.associateBy { shoppingItem -> shoppingItem.getProductId() }

        val mergedProductIds = (currentItemsByProductId.keys + incomingProductById.keys).sorted()
        val mergedShoppingItems =
            mergedProductIds.map { productId ->
                val product = incomingProductById[productId] ?: currentItemsByProductId[productId]!!.getProduct()
                val quantity = currentItemsByProductId[productId]?.getQuantity() ?: 0
                ShoppingItem(
                    product = product,
                    quantity = quantity,
                )
            }

        _shoppingItems.value = mergedShoppingItems
    }

    override suspend fun plusQuantity(
        productId: Long,
        amount: Int,
    ) {
        if (amount == 0) return
        updateQuantityByDelta(productId = productId, delta = amount)
    }

    override suspend fun minusQuantity(
        productId: Long,
        amount: Int,
    ) {
        if (amount == 0) return
        updateQuantityByDelta(productId = productId, delta = -amount)
    }

    private fun updateQuantityByDelta(
        productId: Long,
        delta: Int,
    ) {
        val currentShoppingItems = _shoppingItems.value
        val targetIndex =
            currentShoppingItems.indexOfFirst { shoppingItem ->
                shoppingItem.getProductId() == productId
            }

        if (targetIndex == -1) {
            throw IllegalArgumentException("해당 상품을 찾을 수 없습니다.")
        }

        val targetShoppingItem = currentShoppingItems[targetIndex]
        val updatedQuantity = (targetShoppingItem.getQuantity() + delta).coerceAtLeast(0)
        val updatedShoppingItem =
            ShoppingItem(
                product = targetShoppingItem.getProduct(),
                quantity = updatedQuantity,
            )

        _shoppingItems.update { shoppingItems ->
            shoppingItems.toMutableList().apply {
                this[targetIndex] = updatedShoppingItem
            }
        }
    }
}
