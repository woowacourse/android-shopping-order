package woowacourse.shopping.data.carts.repository

import woowacourse.shopping.data.carts.CartFetchError
import woowacourse.shopping.data.carts.dto.CartQuantity
import woowacourse.shopping.domain.model.CartItem
import woowacourse.shopping.domain.model.Goods

interface CartRepository {
    fun checkValidBasicKey(onResponse: (Int) -> Unit)

    fun fetchAllCartItems(
        onComplete: (List<CartItem>) -> Unit,
        onFail: (Throwable) -> Unit,
    )

    fun fetchCartItemsByOffset(
        limit: Int,
        offset: Int,
        onComplete: (List<CartItem>) -> Unit,
        onFail: (CartFetchError) -> Unit,
    )

    fun fetchCartItemsByPage(
        page: Int,
        size: Int,
        onComplete: (List<CartItem>) -> Unit,
        onFail: (CartFetchError) -> Unit,
    )

    fun addOrIncreaseQuantity(
        goods: Goods,
        addQuantity: Int,
        onComplete: () -> Unit,
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
        cartId : Int,
        onComplete: (Int) -> Unit,
    )

    fun getAllItemsSize(onComplete: (Int) -> Unit)

    fun addCartItem(goods: Goods)
}
