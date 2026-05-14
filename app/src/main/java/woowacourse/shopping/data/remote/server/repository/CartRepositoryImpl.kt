package woowacourse.shopping.data.remote.server.repository

import woowacourse.shopping.data.remote.server.dto.cart.items.PatchQuantityRequest
import woowacourse.shopping.data.remote.server.dto.cart.items.PostCartRequest
import woowacourse.shopping.data.remote.server.dto.cart.items.toDomain
import woowacourse.shopping.data.remote.server.service.CartService
import woowacourse.shopping.domain.PurchaseProduct
import woowacourse.shopping.domain.PurchaseProducts

class CartRepositoryImpl(private val cartService: CartService) : CartRepository {

    override suspend fun insert(purchaseProduct: PurchaseProduct) {
        cartService.postCartItems(
            PostCartRequest(
                productId = purchaseProduct.id,
                quantity = purchaseProduct.count
            )
        )
    }

    override suspend fun updateCount(cartItemId: Long, newQuantity: Int) {
        cartService.patchQuantity(
            cartItemId = cartItemId,
            request = PatchQuantityRequest(newQuantity)
        )
    }

    override suspend fun deleteCartItem(purchaseProductId: Long) {
        cartService.deleteProduct(
            productId = purchaseProductId
        )
    }

    override suspend fun getProductCount(): Int {
        try {
            return  cartService.requestQuantity().quantity
        } catch (e: Exception){
            throw e
        }
    }

    override suspend fun getPagedCart(
        page: Int,
        size: Int
    ): PurchaseProducts {
        try {
            val response = cartService.requestCartItems(page, size)
            val cartItems = response.content.map { content->
                content.toDomain()
            }
            android.util.Log.d("content", response.content.toString())
            return PurchaseProducts(cartItems)
        } catch (e: Exception) {
            throw e
        }
    }

    override suspend fun getCartItemCount(): Int {
        return cartService.requestCartItems(0, 1).totalElements.toInt()
    }
}