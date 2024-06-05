package woowacourse.shopping.domain.repository

import woowacourse.shopping.domain.Cart

interface CartRepository {
    fun load(
        startPage: Int,
        pageSize: Int,
        onSuccess: (List<Cart>, Int) -> Unit,
        onFailure: () -> Unit,
    )

    fun updateIncrementQuantity(
        cartId: Long,
        productId: Long,
        incrementAmount: Int,
        quantity: Int,
        onSuccess: (Long, Int) -> Unit,
        onFailure: () -> Unit,
    )

    fun updateDecrementQuantity(
        cartId: Long,
        productId: Long,
        decrementAmount: Int,
        quantity: Int,
        onSuccess: (Long, Int) -> Unit,
        onFailure: () -> Unit,
    )

    fun getCount(
        onSuccess: (Int) -> Unit,
        onFailure: () -> Unit,
    )

    fun deleteExistCartItem(
        cartId: Long,
        onSuccess: (Long, Int) -> Unit,
        onFailure: () -> Unit,
    )
}
