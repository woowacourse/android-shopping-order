package woowacourse.shopping.presentation.ui

import woowacourse.shopping.data.remote.dto.request.CartItemRequest
import woowacourse.shopping.data.remote.dto.request.OrderRequest
import woowacourse.shopping.data.remote.dto.request.QuantityRequest
import woowacourse.shopping.data.remote.paging.LoadResult
import woowacourse.shopping.domain.CartProduct
import woowacourse.shopping.domain.Coupon
import woowacourse.shopping.domain.RecentProduct
import woowacourse.shopping.domain.Repository

class FakeRepository : Repository {
    val carts = cartProducts.toMutableList()

    override suspend fun getProducts(
        category: String,
        page: Int,
        size: Int,
    ): Result<List<CartProduct>> {
        TODO("Not yet implemented")
    }
    override suspend fun getCartItems(
        page: Int,
        size: Int,
    ): Result<List<CartProduct>> {
        return Result.success(carts)
    }

    override suspend fun getProductById(id: Int): Result<CartProduct?> {
        TODO("Not yet implemented")
    }

    override suspend fun postCartItem(cartItemRequest: CartItemRequest): Result<Int> {
        carts.add(
            CartProduct(
                productId = cartItemRequest.productId.toLong(),
                name = "",
                imgUrl = "",
                price = 1000L,
                quantity = 10,
                category = "",
                cartId = 0L,
            ),
        )
        return Result.success(cartItemRequest.productId)
    }

    override suspend fun patchCartItem(
        id: Int,
        quantityRequestDto: QuantityRequest,
    ): Result<Unit> {
        carts.add(
            CartProduct(
                productId = id.toLong(),
                name = "",
                imgUrl = "",
                price = 1000L,
                quantity = 10,
                category = "",
                cartId = 0L,
            ),
        )
        return Result.success(Unit)
    }

    override suspend fun deleteCartItem(id: Int): Result<Unit> {
        TODO("Not yet implemented")
    }

    override suspend fun postOrders(orderRequest: OrderRequest): Result<Unit> {
        TODO("Not yet implemented")
    }

    override suspend fun findByLimit(limit: Int): Result<List<RecentProduct>> = Result.success(recentProducts)

    override suspend fun findOneRecent(): Result<RecentProduct?> = Result.success(recentProduct)

    override suspend fun saveRecentProduct(recentProduct: RecentProduct): Result<Long> {
        return Result.success(1L)
    }

    override suspend fun getCartItemsCounts(): Result<Int> {
        TODO("Not yet implemented")
    }

    override suspend fun getCuration(): Result<List<CartProduct>> {
        TODO("Not yet implemented")
    }

    override suspend fun getCoupons(): Result<List<Coupon>> {
        TODO("Not yet implemented")
    }

    override suspend fun getProductsByPaging(
        offset: Int,
        pageSize: Int
    ): Result<LoadResult.Page<CartProduct>> {
        TODO("Not yet implemented")
    }
}
