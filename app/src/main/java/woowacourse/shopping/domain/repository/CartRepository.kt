package woowacourse.shopping.domain.repository

import woowacourse.shopping.domain.Cart

interface CartRepository {
    fun load(
        startPage: Int,
        pageSize: Int,
        onSuccess: (List<Cart>, Int) -> Unit,
        onFailure: () -> Unit,
    )

    fun loadById(
        productId: Long,
        onSuccess: (Cart?) -> Unit,
        onFailure: () -> Unit,
    )

    fun loadAll(
        onSuccess: (List<Cart>) -> Unit,
        onFailure: () -> Unit,
    )

    fun modifyExistCartQuantity(
        productId: Long,
        quantityDelta: Int,
        onSuccess: (Long, Int) -> Unit,
        onFailure: () -> Unit,
    )

    fun setNewCartQuantity(
        productId: Long,
        newQuantity: Int,
        onSuccess: (Long, Int) -> Unit,
        onFailure: () -> Unit,
    )

    fun getCount(
        onSuccess: (Int) -> Unit,
        onFailure: () -> Unit,
    )

    fun deleteExistCartItem(
        productId: Long,
        onSuccess: (Long, Int) -> Unit,
        onFailure: () -> Unit,
    )
}
