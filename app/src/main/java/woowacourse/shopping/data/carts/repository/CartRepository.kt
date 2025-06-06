package woowacourse.shopping.data.carts.repository

import woowacourse.shopping.data.carts.CartFetchError
import woowacourse.shopping.data.carts.CartFetchResult
import woowacourse.shopping.data.carts.CartUpdateResult
import woowacourse.shopping.data.carts.dto.CartQuantity
import woowacourse.shopping.data.carts.dto.CartResponse
import woowacourse.shopping.domain.model.Goods

interface CartRepository {
    fun saveBasicKey(
        onResponse: () -> Unit,
        onFail: () -> Unit,
    )

    fun checkValidBasicKey(
        basicKey: String,
        onResponse: (Int) -> Unit,
        onFail: (CartFetchError) -> Unit,
    )

    fun checkValidLocalSavedBasicKey(
        onResponse: (Int) -> Unit,
        onFail: (CartFetchError) -> Unit,
    )

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

    fun addCartItem(
        goods: Goods,
        quantity: Int,
        onComplete: (resultCode: Int, cartId: Int) -> Unit,
        onFail: (CartFetchError) -> Unit,
    )
}
