package woowacourse.shopping.data.carts.repository

import woowacourse.shopping.data.carts.AddItemResult
import woowacourse.shopping.data.carts.CartFetchResult
import woowacourse.shopping.data.carts.CartUpdateResult
import woowacourse.shopping.data.carts.dto.CartQuantity
import woowacourse.shopping.data.carts.dto.CartResponse
import woowacourse.shopping.domain.model.Goods

interface CartRepository {
    suspend fun saveBasicKey(): Result<Unit>

    suspend fun checkValidBasicKey(basicKey: String): CartFetchResult<Int>

    suspend fun checkValidLocalSavedBasicKey(): CartFetchResult<Int>

    suspend fun fetchAllCartItems(): CartFetchResult<CartResponse>

    suspend fun fetchCartItemsByOffset(
        limit: Int,
        offset: Int,
    ): CartFetchResult<CartResponse>

    suspend fun updateQuantity(
        cartId: Int,
        cartQuantity: CartQuantity,
    ): CartUpdateResult<Int>

    suspend fun delete(cartId: Int): CartFetchResult<Int>

    suspend fun addCartItem(
        goods: Goods,
        quantity: Int,
    ): CartFetchResult<AddItemResult>
}
