package woowacourse.shopping.domain.repository

import woowacourse.shopping.domain.model.CartProduct
import woowacourse.shopping.domain.model.PageableItem

interface CartRepository {
    fun fetchCartItems(
        page: Int,
        size: Int,
        onResult: (Result<PageableItem<CartProduct>>) -> Unit,
    )

    fun addCartItem(
        productId: Long,
        quantity: Int,
        onResult: (Result<Unit>) -> Unit,
    )

    fun deleteCartItem(
        cartId: Long,
        onResult: (Result<Unit>) -> Unit,
    )

    fun patchCartItemQuantity(
        cartId: Long,
        quantity: Int,
        onResult: (Result<Unit>) -> Unit,
    )

    fun fetchCartItemCount(onResult: (Result<Int>) -> Unit)
}
