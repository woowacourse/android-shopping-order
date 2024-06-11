package woowacourse.shopping.domain.repository

import woowacourse.shopping.domain.entity.Cart
import woowacourse.shopping.domain.entity.CartProduct
import woowacourse.shopping.domain.entity.Product

interface CartRepository {
    suspend fun findCartProduct(productId: Long): Result<CartProduct>

    fun existsCartProduct(productId: Long): Boolean

    suspend fun loadCurrentPageCart(
        currentPage: Int,
        pageSize: Int,
    ): Result<Cart>

    suspend fun loadCart(): Result<Cart>

    suspend fun filterCartProducts(productIds: List<Long>): Result<Cart>

    suspend fun createCartProduct(
        product: Product,
        count: Int,
    ): Result<Cart>

    suspend fun updateCartProduct(
        product: Product,
        count: Int,
    ): Result<Cart>

    suspend fun deleteCartProduct(productId: Long): Result<Cart>

    fun canLoadMoreCartProducts(
        currentPage: Int,
        pageSize: Int,
    ): Result<Boolean>

    fun clearCart()
}
