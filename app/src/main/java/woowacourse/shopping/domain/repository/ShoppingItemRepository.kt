package woowacourse.shopping.domain.repository

import kotlinx.coroutines.flow.StateFlow
import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.model.ShoppingItem

interface ShoppingItemRepository {
    val shoppingItems: StateFlow<List<ShoppingItem>>

    suspend fun upsertProduct(product: Product)

    suspend fun replaceProducts(products: List<Product>)

    suspend fun getQuantity(productId: Long): Int

    suspend fun plusQuantity(
        productId: Long,
        amount: Int = 1,
    )

    suspend fun minusQuantity(
        productId: Long,
        amount: Int = 1,
    )
}
