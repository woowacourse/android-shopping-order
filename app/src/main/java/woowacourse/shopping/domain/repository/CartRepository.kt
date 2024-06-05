package woowacourse.shopping.domain.repository

import woowacourse.shopping.domain.model.CartItem
import woowacourse.shopping.domain.model.Quantity

interface CartRepository {
    suspend fun findByProductId(productId: Int): Result<CartItem?>

    suspend fun findAll(): Result<List<CartItem>>

    suspend fun delete(id: Int): Result<Unit>

    suspend fun add(
        productId: Int,
        quantity: Quantity = Quantity(1),
    ): Result<Unit>

    suspend fun changeQuantity(
        id: Int,
        quantity: Quantity,
    ): Result<Unit>

    suspend fun getTotalQuantity(): Result<Int>
}
