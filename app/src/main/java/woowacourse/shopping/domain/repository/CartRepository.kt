package woowacourse.shopping.domain.repository

import woowacourse.shopping.data.NetworkResult
import woowacourse.shopping.domain.Cart

interface CartRepository {
    fun load(
        startPage: Int,
        pageSize: Int,
        callBack: (NetworkResult<List<Cart>>) -> Unit,
    )

    fun saveNewCartItem(
        productId: Long,
        incrementAmount: Int,
        callBack: (NetworkResult<Long>) -> Unit,
    )

    fun updateCartItemQuantity(
        cartId: Long,
        newQuantity: Int,
        callBack: (NetworkResult<Unit>) -> Unit,
    )

    fun deleteCartItem(
        cartId: Long,
        callBack: (NetworkResult<Unit>) -> Unit,
    )

    fun getCount(callBack: (NetworkResult<Int>) -> Unit)
}
