package woowacourse.shopping.data.shoppingCart.repository

import woowacourse.shopping.domain.product.Product
import woowacourse.shopping.domain.shoppingCart.ShoppingCarts

interface ShoppingCartRepository {
    fun load(
        page: Int,
        size: Int,
        onResult: (Result<ShoppingCarts>) -> Unit,
    )

    fun add(
        product: Product,
        quantity: Int,
        onResult: (Result<Unit>) -> Unit,
    )

    fun increaseQuantity(
        shoppingCartId: Long,
        quantity: Int,
        onResult: (Result<Unit>) -> Unit,
    )

    fun decreaseQuantity(
        shoppingCartId: Long,
        quantity: Int,
        onResult: (Result<Unit>) -> Unit,
    )

    fun remove(
        shoppingCartId: Long,
        onResult: (Result<Unit>) -> Unit,
    )

    fun fetchAllQuantity(onResult: (Result<Int>) -> Unit)
}
