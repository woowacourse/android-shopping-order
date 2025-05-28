package woowacourse.shopping.data.carts.repository

import woowacourse.shopping.data.carts.CartFetchError
import woowacourse.shopping.data.carts.dto.CartQuantity
import woowacourse.shopping.data.carts.dto.CartResponse
import woowacourse.shopping.domain.model.Goods

interface CartRepository {
    fun checkValidBasicKey(onResponse: (Int) -> Unit)

    fun fetchAllCartItems(
        onComplete: (CartResponse) -> Unit,
        onFail: (Throwable) -> Unit,
    )

    fun fetchCartItemsByOffset(
        limit: Int,
        offset: Int,
        onComplete: (CartResponse) -> Unit,
        onFail: (CartFetchError) -> Unit,
    )

    fun fetchCartItemsByPage(
        page: Int,
        size: Int,
        onComplete: (CartResponse) -> Unit,
        onFail: (CartFetchError) -> Unit,
    )

    fun updateQuantity(
        cartId: Int,
        cartQuantity: CartQuantity,
        onComplete: () -> Unit,
        onFail: (CartFetchError) -> Unit,
    )

    fun removeOrDecreaseQuantity(
        goods: Goods,
        removeQuantity: Int,
        onComplete: () -> Unit,
    )

    fun delete(
        cartId: Int,
        onComplete: (Int) -> Unit,
    )

    fun getAllItemsSize(onComplete: (Int) -> Unit)

    fun addCartItem(goods: Goods)
}
