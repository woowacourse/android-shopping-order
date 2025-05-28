package woowacourse.shopping.domain.repository

import woowacourse.shopping.domain.model.CartProduct
import woowacourse.shopping.domain.model.PageableItem

interface CartRepository {
    fun fetchCartItems(
        page: Int,
        size: Int,
        onResult: (Result<PageableItem<CartProduct>>) -> Unit,
    )

    fun deleteCartItem(
        cartId: Long,
        onResult: (Result<Unit>) -> Unit,
    )

    fun insertCartProductQuantityToCart(
        productId: Long,
        increaseCount: Int,
        onResult: (Result<Unit>) -> Unit,
    )

    fun decreaseCartProductQuantityFromCart(
        productId: Long,
        decreaseCount: Int,
        onResult: (Result<Unit>) -> Unit,
    )

    fun fetchCartItemCount(onResult: (Result<Int>) -> Unit)

    fun findQuantityByProductId(productId: Long): Result<Int>

    fun findCartProductsByProductIds(productIds: List<Long>): Result<List<CartProduct>>
}
