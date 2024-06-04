package woowacourse.shopping.domain.repository

import woowacourse.shopping.data.model.CartQuantity
import woowacourse.shopping.data.model.CartResponse
import woowacourse.shopping.domain.model.CartDomain

interface CartRepository {
    fun getCartItems(
        page: Int,
        size: Int,
        sort: String,
        onSuccess: (CartDomain) -> Unit,
        onFailure: (Throwable) -> Unit,
    )

    fun addCartItem(
        productId: Int,
        quantity: Int,
        onSuccess: () -> Unit,
        onFailure: (Throwable) -> Unit,
    )

    fun deleteCartItem(
        cartItemId: Int,
        onSuccess: () -> Unit,
        onFailure: (Throwable) -> Unit,
    )

    fun updateCartItem(
        cartItemId: Int,
        quantity: Int,
        onSuccess: () -> Unit,
        onFailure: (Throwable) -> Unit,
    )

    fun getCartTotalQuantity(
        onSuccess: (totalQuantity: Int) -> Unit,
        onFailure: (Throwable) -> Unit,
    )
}
