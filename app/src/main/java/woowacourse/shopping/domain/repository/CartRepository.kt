package woowacourse.shopping.domain.repository

import woowacourse.shopping.domain.model.CartItem
import woowacourse.shopping.domain.model.Order
import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.model.ShoppingCart

interface CartRepository {
    suspend fun updateCartItems(): Result<Unit>

    suspend fun insert(
        product: Product,
        quantity: Int,
    ): Result<Unit>

    suspend fun update(
        productId: Long,
        quantity: Int,
    ): Result<Unit>

    suspend fun updateQuantity(
        cartItemId: Long,
        quantity: Int,
    ): Result<Unit>

    suspend fun updateQuantityWithProductId(
        productId: Long,
        quantity: Int,
    ): Result<Unit>

    suspend fun findQuantityWithProductId(productId: Long): Result<Int>

    suspend fun makeOrder(order: Order): Result<Unit>

    suspend fun size(): Result<Int>

    suspend fun sumOfQuantity(): Result<Int>

    suspend fun findOrNullWithProductId(productId: Long): Result<CartItem?>

    suspend fun findWithCartItemId(cartItemId: Long): Result<CartItem>

    suspend fun findAll(): Result<ShoppingCart>

    suspend fun findAllPagedItems(
        page: Int,
        pageSize: Int,
    ): Result<ShoppingCart>

    suspend fun delete(cartItemId: Long): Result<Unit>

    suspend fun deleteWithProductId(productId: Long): Result<Unit>

    suspend fun deleteAll(): Result<Unit>
}
