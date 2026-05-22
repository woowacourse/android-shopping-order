package woowacourse.shopping.domain.repository

import woowacourse.shopping.domain.model.PurchaseProduct
import woowacourse.shopping.domain.model.PurchaseProducts

interface CartRepository {
    suspend fun insert(purchaseProduct: PurchaseProduct)

    suspend fun deleteCartItem(purchaseProductId: Long)

    suspend fun updateCount(cartItemId: Long, newQuantity: Int)

    suspend fun getProductCount(): Int

    suspend fun getPagedCart(page: Int, size: Int): PurchaseProducts

    suspend fun getCartItemCount(): Int
}