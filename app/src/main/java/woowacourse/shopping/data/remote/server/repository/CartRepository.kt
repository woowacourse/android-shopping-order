package woowacourse.shopping.data.remote.server.repository

import woowacourse.shopping.domain.PurchaseProduct
import woowacourse.shopping.domain.PurchaseProducts

interface CartRepository {
    suspend fun insert(purchaseProduct: PurchaseProduct)

    suspend fun deleteCartItem(purchaseProductId: Long)

    suspend fun updateCount(
        cartItemId: Long,
        newQuantity: Int,
    )

    suspend fun getProductCount(): Int

    suspend fun getPagedCart(
        page: Int,
        size: Int,
    ): PurchaseProducts

    suspend fun getCartItemCount(): Int
}
