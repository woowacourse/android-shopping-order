package woowacourse.shopping.domain

import woowacourse.shopping.data.remote.dto.request.CartItemRequest
import woowacourse.shopping.data.remote.dto.request.OrderRequest
import woowacourse.shopping.data.remote.dto.request.QuantityRequest
import woowacourse.shopping.domain.coupon.Coupon

interface Repository {

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

    suspend fun findByLimit(limit: Int): Result<List<RecentProduct>>

    suspend fun findOne(): Result<RecentProduct?>

    suspend fun saveRecentProduct(recentProduct: RecentProduct): Result<Long>

    suspend fun updateRecentProduct(
        productId: Long,
        quantity: Int,
        cartId: Long,
    )

    suspend fun getCartItemsCounts(): Result<Int>

    suspend fun getCuration(): Result<List<CartProduct>?>

    suspend fun getCoupons(): Result<List<Coupon>>
}
