package woowacourse.shopping.domain.repository

import woowacourse.shopping.domain.model.CartItem
import woowacourse.shopping.domain.model.Product

interface CartRepository {
    fun getTotalCount(onResult: (Result<Int>) -> Unit)

    fun fetchPagedCartItems(
        page: Int,
        pageSize: Int? = null,
        onResult: (Result<List<CartItem>>) -> Unit,
    )

    fun fetchAllCartItems(onFinished: (() -> Unit)? = null)

    fun getCartItemById(productId: Long): CartItem?

    fun insertOrUpdate(
        product: Product,
        productQuantity: Int,
        onResult: (Result<Unit>) -> Unit,
    )

    fun insertProduct(
        product: Product,
        productQuantity: Int,
        onResult: (Result<Long>) -> Unit,
    )

    fun updateProduct(
        cartId: Long,
        product: Product,
        quantity: Int,
        onResult: (Result<Unit>) -> Unit,
    )

    fun increaseQuantity(
        productId: Long,
        onResult: (Result<Unit>) -> Unit,
    )

    fun decreaseQuantity(
        productId: Long,
        onResult: (Result<Unit>) -> Unit,
    )

    fun deleteProduct(
        productId: Long,
        onResult: (Result<Unit>) -> Unit,
    )
}
