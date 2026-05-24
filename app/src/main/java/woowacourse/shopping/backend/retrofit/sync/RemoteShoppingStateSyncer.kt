package woowacourse.shopping.backend.retrofit.sync

import woowacourse.shopping.model.Product
import woowacourse.shopping.model.ShoppingItem
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

    suspend fun syncProduct(product: ShoppingItem) {
        shoppingItemRepository.upsertProduct(product)
    }
}
