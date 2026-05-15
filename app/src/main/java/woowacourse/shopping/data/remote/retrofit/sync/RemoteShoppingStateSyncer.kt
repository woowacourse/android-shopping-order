package woowacourse.shopping.data.remote.retrofit.sync

import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.model.ShoppingCartItem
import woowacourse.shopping.domain.repository.ShoppingCartRepository
import woowacourse.shopping.domain.repository.ShoppingItemRepository

class RemoteShoppingStateSyncer(
    /**
     * 이번 미션에서는 지난 미션의 코드를 기반으로 하고 있고 Room을 활용하던 코드를 API로 리팩터링해보는
     * 과정을 경험해볼 수 있습니다. 그러면서 자연스럽게 Repository 패턴에 대해 익혀보고 DataSource는 무엇인지 어떻게 활용할 수 있는지 알아볼 수 있어요.
     * RemoteShoppingStateSyncer는 어떠한 과정으로 도입하게 된 것일까요?
     */
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
