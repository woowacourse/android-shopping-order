package woowacourse.shopping.data.carts.repository

import woowacourse.shopping.data.carts.dto.CartQuantity
import woowacourse.shopping.data.carts.dto.CartResponse
import woowacourse.shopping.domain.model.Goods
interface CartRepository {
    suspend fun checkValidBasicKey(validKey: String): Int

    suspend fun fetchAllCartItems(): CartResponse

    suspend fun fetchCartItemsByOffset(
        limit: Int,
        offset: Int,
    ): CartResponse

    suspend fun fetchCartItemsByPage(
        page: Int,
        size: Int,
    ): CartResponse

    suspend fun updateQuantity(
        cartId: Int,
        cartQuantity: CartQuantity,
    )

    suspend fun delete(cartId: Int): Int

    suspend fun getAllItemsSize(): Int

    suspend fun addCartItem(
        goods: Goods,
        quantity: Int,
    ): Int
}