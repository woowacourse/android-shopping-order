package woowacourse.shopping.data.cart

import woowacourse.shopping.data.NetworkResult
import woowacourse.shopping.domain.model.Cart

interface CartDataSource {
    fun getCartItems(
        startPage: Int,
        pageSize: Int,
        callBack: (NetworkResult<List<Cart>>) -> Unit,
    )

    fun saveCartItem(
        productId: Long,
        quantity: Int,
        callBack: (NetworkResult<Long>) -> Unit,
    )

    fun updateCartItemQuantity(
        cartId: Int,
        newQuantity: Int,
        callBack: (NetworkResult<Unit>) -> Unit,
    )

    fun deleteCartItem(
        cartId: Int,
        callBack: (NetworkResult<Unit>) -> Unit,
    )

    fun getTotalCount(callBack: (NetworkResult<Int>) -> Unit)
}
