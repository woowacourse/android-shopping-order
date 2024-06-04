package woowacourse.shopping.domain.repository

import woowacourse.shopping.domain.Cart

interface CartRepository {
    fun load(
        startPage: Int,
        pageSize: Int,
        onSuccess: (List<Cart>, Int) -> Unit,
        onFailure: () -> Unit,
    )

    fun saveNewCartItem(
        productId: Long,
        incrementAmount: Int,
        onSuccess: (Long, Int) -> Unit,
        onFailure: () -> Unit,
    )

    fun updateCartItemQuantity(
        cartId: Long,
        newQuantity: Int,
        onSuccess: (Long, Int) -> Unit,
        onFailure: () -> Unit,
    )

    fun deleteCartItem(
        cartId: Long,
        onSuccess: (Long, Int) -> Unit,
        onFailure: () -> Unit,
    )

    fun getCount(
        onSuccess: (Int) -> Unit,
        onFailure: () -> Unit,
    )
}
