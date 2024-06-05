package woowacourse.shopping.domain

import woowacourse.shopping.data.remote.dto.request.CartItemRequestDto
import woowacourse.shopping.data.remote.dto.request.OrderRequestDto
import woowacourse.shopping.data.remote.dto.request.QuantityRequestDto

interface Repository {
    fun getProducts(
        category: String,
        page: Int = 0,
        size: Int = 20,
    ): Result<List<CartProduct>>

    fun getProductsByPaging(): Result<List<CartProduct>>

    fun getCartItems(
        page: Int,
        size: Int,
    ): Result<List<CartProduct>>

    fun getProductById(id: Int): Result<CartProduct?>

    fun postCartItem(cartItemRequestDto: CartItemRequestDto): Result<Int>

    fun patchCartItem(
        id: Int,
        quantityRequestDto: QuantityRequestDto,
    ): Result<Unit>

    fun deleteCartItem(id: Int): Result<Unit>

    fun postOrders(orderRequestDto: OrderRequestDto): Result<Unit>

    fun findByLimit(limit: Int): Result<List<RecentProduct>>

    fun findOneRecent(): Result<RecentProduct?>

    fun saveRecentProduct(recentProduct: RecentProduct): Result<Long>

    fun getCartItemsCounts(): Result<Int>

    fun getCuration(): Result<List<CartProduct>>
}
