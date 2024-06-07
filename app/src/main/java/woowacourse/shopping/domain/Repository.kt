package woowacourse.shopping.domain

import woowacourse.shopping.data.remote.dto.request.CartItemRequest
import woowacourse.shopping.data.remote.dto.request.OrderRequest
import woowacourse.shopping.data.remote.dto.request.QuantityRequest
import woowacourse.shopping.data.remote.dto.response.Coupons

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

    suspend fun findByLimit(limit: Int): Result<List<RecentProduct>>

    suspend fun findOne(): Result<RecentProduct?>

    fun saveCart(cart: Cart): Result<Long>

    fun saveRecent(recent: Recent): Result<Long>

    suspend fun saveRecentProduct(recentProduct: RecentProduct): Result<Long>

    suspend fun updateRecentProduct(
        productId: Long,
        quantity: Int,
        cartId: Long,
    )

    fun deleteCart(id: Long): Result<Long>

    fun getMaxCartCount(): Result<Int>

    suspend fun getCartItemsCounts(): Result<Int>

    suspend fun getCuration(): Result<List<CartProduct>?>

    suspend fun getCoupons(): Result<List<Coupons>>
}
