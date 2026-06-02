package woowacourse.shopping.domain.repository

import woowacourse.shopping.domain.model.cart.CartPage
import woowacourse.shopping.domain.model.order.PurchaseProduct
import woowacourse.shopping.domain.model.order.PurchaseProducts

interface CartRepository {
    suspend fun insert(purchaseProduct: PurchaseProduct)

    suspend fun deleteCartItem(purchaseProductId: Long)

    suspend fun updateCount(cartItemId: Long, newQuantity: Int)

    suspend fun getProductCount(): Int

    suspend fun getCartPage(page: Int, size: Int): CartPage

    suspend fun getAllCartItems(pageSize: Int = DEFAULT_CART_PAGE_SIZE): PurchaseProducts

    suspend fun findCartItemByProductId(productId: Long, pageSize: Int = DEFAULT_CART_PAGE_SIZE): PurchaseProduct?

    companion object {
        const val DEFAULT_CART_PAGE_SIZE = 5
    }
}
