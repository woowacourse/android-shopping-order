package woowacourse.shopping.presentation.ui

import woowacourse.shopping.data.remote.dto.request.CartItemRequest
import woowacourse.shopping.data.remote.dto.request.OrderRequest
import woowacourse.shopping.data.remote.dto.request.QuantityRequest
import woowacourse.shopping.domain.Cart
import woowacourse.shopping.domain.CartProduct
import woowacourse.shopping.domain.Recent
import woowacourse.shopping.domain.RecentProduct
import woowacourse.shopping.domain.Repository
import kotlin.math.min

class FakeRepository : Repository {
    val carts = cartProducts.toMutableList()

    override fun findProductByPaging(
        offset: Int,
        pageSize: Int,
    ): Result<List<CartProduct>> = Result.success(carts)

    override fun getProducts(
        category: String,
        page: Int,
        size: Int,
    ): Result<List<CartProduct>?> {
        TODO("Not yet implemented")
    }

    override fun getProductsByPaging(): Result<List<CartProduct>?> {
        TODO("Not yet implemented")
    }

    override fun getCartItems(
        page: Int,
        size: Int,
    ): Result<List<CartProduct>?> {
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

    override fun findCartByPaging(
        offset: Int,
        pageSize: Int,
    ): Result<List<CartProduct>> =
        Result.success(
            carts.map {
                CartProduct(
                    productId = it.productId,
                    name = "${it.productId}",
                    imgUrl = "www.naver.com",
                    price = 10000,
                    quantity = it.quantity,
                )
            }.subList(offset * pageSize, min(offset * pageSize + pageSize, carts.size)),
        )

    override fun findByLimit(limit: Int): Result<List<RecentProduct>> =
        Result.success(recentProducts)

    override fun findOne(): Result<RecentProduct?> = Result.success(recentProduct)

    override fun findProductById(id: Long): Result<CartProduct?> =
        Result.success(
            cartProduct,
        )

    override fun saveCart(cart: Cart): Result<Long> =
        runCatching {
            carts.add(
                CartProduct(
                    productId = cart.productId,
                    name = "${cart.productId}",
                    imgUrl = "www.naver.com",
                    price = 10000,
                    quantity = cart.quantity,
                ),
            )
            cart.productId
        }

    override fun saveRecent(recent: Recent): Result<Long> = Result.success(1)

    override fun saveRecentProduct(recentProduct: RecentProduct): Result<Long> {
        return Result.success(1L)
    }

    override fun deleteCart(id: Long): Result<Long> = Result.success(1)

    override fun getMaxCartCount(): Result<Int> =
        runCatching {
            carts.size
        }

    override fun getCartItemsCounts(): Result<Int> {
        TODO("Not yet implemented")
    }

    override fun getCuration(): Result<List<CartProduct>> {
        TODO("Not yet implemented")
    }
}