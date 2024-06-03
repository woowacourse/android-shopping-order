package woowacourse.shopping.presentation.ui

import woowacourse.shopping.data.remote.dto.request.CartItemRequest
import woowacourse.shopping.data.remote.dto.request.OrderRequest
import woowacourse.shopping.data.remote.dto.request.QuantityRequest
import woowacourse.shopping.domain.CartProduct
import woowacourse.shopping.domain.RecentProduct
import woowacourse.shopping.domain.Repository

class FakeRepository : Repository {
    val carts = cartProducts.toMutableList()

    override fun getProducts(
        category: String,
        page: Int,
        size: Int,
    ): Result<List<CartProduct>> {
        TODO("Not yet implemented")
    }

    override fun getProductsByPaging(): Result<List<CartProduct>> {
        TODO("Not yet implemented")
    }

    override fun getCartItems(
        page: Int,
        size: Int,
    ): Result<List<CartProduct>> {
        return Result.success(carts)
    }

    override fun getProductById(id: Int): Result<CartProduct?> {
        TODO("Not yet implemented")
    }

    override fun postCartItem(cartItemRequest: CartItemRequest): Result<Int> {
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

    override fun patchCartItem(
        id: Int,
        quantityRequest: QuantityRequest,
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

    override fun deleteCartItem(id: Int): Result<Unit> {
        TODO("Not yet implemented")
    }

    override fun postOrders(orderRequest: OrderRequest): Result<Unit> {
        TODO("Not yet implemented")
    }

    override fun findByLimit(limit: Int): Result<List<RecentProduct>> = Result.success(recentProducts)

    override fun findOne(): Result<RecentProduct?> = Result.success(recentProduct)

    override fun saveRecentProduct(recentProduct: RecentProduct): Result<Long> {
        return Result.success(1L)
    }

    override fun getCartItemsCounts(): Result<Int> {
        TODO("Not yet implemented")
    }

    override fun getCuration(): Result<List<CartProduct>> {
        TODO("Not yet implemented")
    }
}
