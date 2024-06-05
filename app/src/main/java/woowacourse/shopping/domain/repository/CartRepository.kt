package woowacourse.shopping.domain.repository

import woowacourse.shopping.domain.entity.Cart
import woowacourse.shopping.domain.entity.CartProduct
import woowacourse.shopping.domain.entity.Product

interface CartRepository {
    fun findCartProduct(productId: Long): Result<CartProduct>

    fun loadCurrentPageCart(
        currentPage: Int,
        pageSize: Int,
    ): Result<Cart>

    fun loadCart(): Result<Cart>

    fun filterCartProducts(productIds: List<Long>): Result<Cart>

    fun createCartProduct(
        product: Product,
        count: Int,
    ): Result<Cart>

    fun updateCartProduct(
        product: Product,
        count: Int,
    ): Result<Cart>

    fun deleteCartProduct(productId: Long): Result<Cart>

    fun canLoadMoreCartProducts(
        currentPage: Int,
        pageSize: Int,
    ): Result<Boolean>

    fun orderCartProducts(productIds: List<Long>): Result<Unit>
}
