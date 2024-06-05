package woowacourse.shopping.domain

import woowacourse.shopping.data.remote.dto.request.CartItemRequestDto
import woowacourse.shopping.data.remote.dto.request.OrderRequestDto
import woowacourse.shopping.data.remote.dto.request.QuantityRequestDto
import woowacourse.shopping.data.remote.paging.LoadResult

interface Repository {
    suspend fun getProducts(
        category: String,
        page: Int = 0,
        size: Int = 20,
    ): Result<List<CartProduct>>

    suspend fun getProductsByPaging(
        offset: Int,
        pageSize: Int,
    ): Result<LoadResult.Page<CartProduct>>

    suspend fun getCartItems(
        page: Int,
        size: Int,
    ): Result<List<CartProduct>>

    suspend fun getProductById(id: Int): Result<CartProduct?>

    suspend fun postCartItem(cartItemRequestDto: CartItemRequestDto): Result<Int>

    suspend fun patchCartItem(
        id: Int,
        quantityRequestDto: QuantityRequestDto,
    ): Result<Unit>

    suspend fun deleteCartItem(id: Int): Result<Unit>

    suspend fun postOrders(orderRequestDto: OrderRequestDto): Result<Unit>

    suspend fun findByLimit(limit: Int): Result<List<RecentProduct>>

    suspend fun findOneRecent(): Result<RecentProduct?>

    suspend fun saveRecentProduct(recentProduct: RecentProduct): Result<Long>

    suspend fun getCartItemsCounts(): Result<Int>

    suspend fun getCuration(): Result<List<CartProduct>>
}
