package woowacourse.shopping.data.carts.repository

import woowacourse.shopping.data.carts.dto.CartResponse

interface CartRemoteDataSource {
    fun fetchCartItemSize(onComplete: (Int) -> Unit)

    fun fetchPageCartItem(
        limit: Int,
        offset: Int,
        onSuccess: (CartResponse) -> Unit,
        onFailure: (Throwable) -> Unit,
    )

    fun fetchGoodsCount(onSuccess: (Int) -> Unit)

    fun increaseItemCount(itemId: Int)

    fun decreaseItemCount(itemId: Int)

    fun deleteItem(itemId: Int)

    fun addItem(itemId: Int)
}
