package woowacourse.shopping.backend.retrofit.sync

import woowacourse.shopping.model.Product
import woowacourse.shopping.model.ShoppingCartItem
import woowacourse.shopping.repository.ShoppingCartRepository
import woowacourse.shopping.repository.ShoppingItemRepository

class RemoteShoppingStateSyncer(
    private val shoppingCartRepository: ShoppingCartRepository,
    private val shoppingItemRepository: ShoppingItemRepository,
) {
    suspend fun syncProducts(products: List<Product>) {
        if (products.isEmpty()) {
            return
        }
        shoppingItemRepository.replaceProducts(products)
    }

    suspend fun syncProduct(product: Product) {
        shoppingItemRepository.upsertProduct(product)
    }

    suspend fun syncCartItems(shoppingCartItems: List<ShoppingCartItem>) {
        val quantityByProductId =
            shoppingCartItems.associate { shoppingCartItem ->
                shoppingCartItem.product.id to shoppingCartItem.getQuantity()
            }
        val localShoppingItems = shoppingItemRepository.shoppingItems.value
        localShoppingItems.forEach { shoppingItem ->
            val productId = shoppingItem.getProductId()
            val currentQuantity = shoppingItem.getQuantity()
            val targetQuantity = quantityByProductId[productId] ?: 0
            when {
                targetQuantity > currentQuantity ->
                    shoppingItemRepository.plusQuantity(productId, targetQuantity - currentQuantity)

                targetQuantity < currentQuantity ->
                    shoppingItemRepository.minusQuantity(productId, currentQuantity - targetQuantity)
            }
            if (targetQuantity > 0) {
                shoppingCartRepository.addIfAbsent(productId)
            } else {
                shoppingCartRepository.removeByProductId(productId)
            }
        }
    }
}
