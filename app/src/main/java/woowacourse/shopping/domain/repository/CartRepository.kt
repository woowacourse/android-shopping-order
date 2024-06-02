package woowacourse.shopping.domain.repository

import woowacourse.shopping.domain.model.CartItem
import woowacourse.shopping.domain.model.Quantity

interface CartRepository {
    fun findByProductId(
        productId: Int,
        totalItemCount: Int,
        callback: (Result<CartItem?>) -> Unit,
    )

    fun syncFindByProductId(
        productId: Int,
        totalItemCount: Int,
    ): CartItem?

    fun findAll(callback: (Result<List<CartItem>>) -> Unit)

    fun delete(
        id: Int,
        callback: (Result<Unit>) -> Unit,
    )

    fun add(
        productId: Int,
        quantity: Quantity = Quantity(1),
        callback: (Result<Unit>) -> Unit,
    )

    fun changeQuantity(
        id: Int,
        quantity: Quantity,
        callback: (Result<Unit>) -> Unit,
    )

    fun getTotalQuantity(callback: (Result<Int>) -> Unit)

    fun syncGetTotalQuantity(): Int
}
