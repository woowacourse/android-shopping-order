package woowacourse.shopping.data.carts.repository

import woowacourse.shopping.data.carts.CartFetchError
import woowacourse.shopping.data.carts.dto.CartQuantity
import woowacourse.shopping.data.carts.dto.CartResponse

interface CartRemoteDataSource {
    fun fetchCartItemSize(onComplete: (Int) -> Unit)

    fun fetchCartItemByPage(
        page: Int,
        size: Int,
        onSuccess: (CartResponse) -> Unit,
        onFailure: (CartFetchError) -> Unit,
    )

    fun fetchCartItemByOffset(
        limit: Int,
        offset: Int,
        onSuccess: (CartResponse) -> Unit,
        onFailure: (CartFetchError) -> Unit,
    )

    fun fetchCartCount(
        onSuccess: (Int) -> Unit,
        onFailure: (CartFetchError) -> Unit,
    )

    fun fetchAuthCode(onResponse: (Int) -> Unit)

    fun updateItemCount(
        cartId: Int,
        cartQuantity: CartQuantity,
        onSuccess: (resultCode: Int) -> Unit,
        onFailure: (CartFetchError) -> Unit,
    )

    fun increaseItemCount(itemId: Int)

    fun decreaseItemCount(itemId: Int)

    fun deleteItem(itemId: Int)

    fun addItem(itemId: Int)
}
