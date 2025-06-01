package woowacourse.shopping.domain.repository

import woowacourse.shopping.domain.model.CartProduct
import woowacourse.shopping.domain.model.PageableItem

interface CartRepository {
    fun fetchCartProducts(
        page: Int,
        size: Int,
        onResult: (Result<PageableItem<CartProduct>>) -> Unit,
    )

    fun deleteCartProduct(
        cartId: Long,
        onResult: (Result<Unit>) -> Unit,
    )

    fun increaseQuantity(
        productId: Long,
        increaseCount: Int,
        onResult: (Result<Unit>) -> Unit,
    )

    fun decreaseQuantity(
        productId: Long,
        decreaseCount: Int,
        onResult: (Result<Unit>) -> Unit,
    )

    fun fetchCartProductCount(onResult: (Result<Int>) -> Unit)

    fun findCartProductByProductId(productId: Long): Result<CartProduct>

    fun findCartProductsByProductIds(productIds: List<Long>): Result<List<CartProduct>>

    fun getAllCartProducts(): Result<List<CartProduct>>
}
