package woowacourse.shopping.repository

import kotlinx.coroutines.flow.StateFlow
import woowacourse.shopping.model.Product
import woowacourse.shopping.model.ShoppingItem
import kotlin.uuid.ExperimentalUuidApi

@OptIn(ExperimentalUuidApi::class)
interface ShoppingItemRepository {
    val shoppingItems: StateFlow<List<ShoppingItem>>

    suspend fun upsertProduct(product: ShoppingItem)

    suspend fun replaceProducts(products: List<Product>)

    suspend fun getQuantity(productId: Long): Int

    suspend fun fetchProductById(productId: Long): ShoppingItem? = shoppingItems.value.find { productId == it.getProductId() }

    suspend fun plusQuantity(
        productId: Long,
        amount: Int = 1,
    )

    suspend fun minusQuantity(
        productId: Long,
        amount: Int = 1,
    )
}
