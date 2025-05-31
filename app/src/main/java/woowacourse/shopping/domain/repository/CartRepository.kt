package woowacourse.shopping.domain.repository

import woowacourse.shopping.domain.model.Products

interface CartRepository {
    fun fetchCartProducts(
        page: Int,
        size: Int,
        callback: (Result<Products>) -> Unit,
    )

    fun fetchAllCartProducts(callback: (Result<Products>) -> Unit)

    fun fetchCartItemCount(callback: (Result<Int>) -> Unit)

    fun addCartProduct(
        productId: Long,
        quantity: Int,
        callback: (Result<Unit>) -> Unit,
    )

    fun deleteCartProduct(
        cartId: Long,
        callback: (Result<Unit>) -> Unit,
    )

    fun updateCartProduct(
        cartId: Long,
        quantity: Int,
        callback: (Result<Unit>) -> Unit,
    )
}
