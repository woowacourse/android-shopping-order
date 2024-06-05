package woowacourse.shopping.domain

import woowacourse.shopping.data.remote.dto.request.CartItemRequest
import woowacourse.shopping.data.remote.dto.request.OrderRequest
import woowacourse.shopping.data.remote.dto.request.QuantityRequest
import woowacourse.shopping.data.remote.dto.response.QuantityResponse

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

    fun getCartItems(
        page: Int,
        size: Int,
        callback: (Result<List<CartProduct>?>) -> Unit,
    )

    fun postCartItem(cartItemRequest: CartItemRequest): Result<Int>

    fun patchCartItem(
        id: Int,
        quantityRequest: QuantityRequest,
    ): Result<Unit>

    fun deleteCartItem(id: Int): Result<Unit>

    fun postOrders(orderRequest: OrderRequest): Result<Unit>

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

    fun getCartItemsCounts(callback: (Result<QuantityResponse>) -> Unit)

    fun getCuration(callback: (Result<List<CartProduct>>) -> Unit)
}
