package woowacourse.shopping.domain

import woowacourse.shopping.data.remote.dto.request.CartItemRequest
import woowacourse.shopping.data.remote.dto.request.OrderRequest
import woowacourse.shopping.data.remote.dto.request.QuantityRequest

interface Repository {
    fun findProductByPaging(
        offset: Int,
        pageSize: Int,
    ): Result<List<CartProduct>>

    suspend fun getProducts(
        category: String,
        page: Int = 0,
        size: Int = 20,
    ): Result<List<CartProduct>?>

    suspend fun getProductsByPaging(): Result<List<CartProduct>?>

    suspend fun getCartItems(
        page: Int,
        size: Int,
    ): Result<List<CartProduct>?>

    suspend fun postCartItem(cartItemRequest: CartItemRequest): Result<Int>

    suspend fun updateCartItem(
        id: Int,
        quantityRequest: QuantityRequest,
    ): Result<Unit>

    suspend fun deleteCartItem(id: Int): Result<Unit>

    suspend fun submitOrders(orderRequest: OrderRequest): Result<Unit>

    fun findCartByPaging(
        offset: Int,
        pageSize: Int,
    ): Result<List<CartProduct>>

    fun findByLimit(limit: Int): Result<List<RecentProduct>>

    fun findOne(): Result<RecentProduct?>

    fun saveCart(cart: Cart): Result<Long>

    fun saveRecent(recent: Recent): Result<Long>

    fun saveRecentProduct(recentProduct: RecentProduct): Result<Long>

    fun updateRecentProduct(
        productId: Long,
        quantity: Int,
        cartId: Long,
    )

    fun deleteCart(id: Long): Result<Long>

    fun getMaxCartCount(): Result<Int>

    suspend fun getCartItemsCounts(): Result<Int>

    fun getCuration(callback: (Result<List<CartProduct>>) -> Unit)
}
