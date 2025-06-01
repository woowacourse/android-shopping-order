package woowacourse.shopping.domain.repository

import woowacourse.shopping.domain.Quantity
import woowacourse.shopping.domain.cart.Cart
import woowacourse.shopping.domain.cart.CartsSinglePage

interface CartRepository {
    fun addCart(
        cart: Cart,
        callback: (Result<String?>) -> Unit,
    )

    fun updateQuantity(
        cartId: Long,
        quantity: Quantity,
        callback: (Result<Unit?>) -> Unit,
    )

    fun deleteCart(
        cartId: Long,
        callback: (Result<Unit?>) -> Unit,
    )

    fun loadSinglePage(
        page: Int?,
        pageSize: Int?,
        callback: (Result<CartsSinglePage?>) -> Unit,
    )
}
