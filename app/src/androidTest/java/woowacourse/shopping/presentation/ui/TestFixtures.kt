package woowacourse.shopping.presentation.ui

import woowacourse.shopping.data.remote.dto.request.CartItemRequest
import woowacourse.shopping.data.remote.dto.request.OrderRequest
import woowacourse.shopping.data.remote.dto.request.QuantityRequest
import woowacourse.shopping.data.remote.dto.response.QuantityResponse
import woowacourse.shopping.domain.Cart
import woowacourse.shopping.domain.CartProduct
import woowacourse.shopping.domain.Recent
import woowacourse.shopping.domain.RecentProduct
import woowacourse.shopping.domain.Repository
import kotlin.math.min

val cartProducts =
    List(51) { id ->
        CartProduct(
            productId = id.toLong(),
            name = "$id",
            imgUrl = "https://image.utoimage.com/preview/cp872722/2022/12/202212008462_500.jpg",
            price = 10000,
            quantity = 1,
        )
    }

val cartProduct: CartProduct = cartProducts.first()

val recentProducts =
    List(10) { id ->
        RecentProduct(
            productId = id.toLong(),
            name = "$id",
            imgUrl = "www.naver.com",
            price = 10000,
            createdAt = System.currentTimeMillis(),
            category = "fashion",
            cartId = id.toLong(),
            quantity = 0,
        )
    }

val recentProduct: RecentProduct = recentProducts.first()

val fakeRepository =
    object : Repository {
        val carts = cartProducts.toMutableList()

        override fun findProductByPaging(
            offset: Int,
            pageSize: Int,
        ): Result<List<CartProduct>> = Result.success(carts)

        override fun getProducts(
            category: String,
            page: Int,
            size: Int,
            callback: (Result<List<CartProduct>?>) -> Unit,
        ) {
            TODO("Not yet implemented")
        }

        override fun getProductsByPaging(callback: (Result<List<CartProduct>?>) -> Unit) {
            TODO("Not yet implemented")
        }

        override fun getCartItems(
            page: Int,
            size: Int,
            callback: (Result<List<CartProduct>?>) -> Unit,
        ) {
            TODO("Not yet implemented")
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

        override fun findByLimit(limit: Int): Result<List<RecentProduct>> = Result.success(recentProducts)

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

        override fun updateRecentProduct(
            productId: Long,
            quantity: Int,
            cartId: Long,
        ) {
            TODO("Not yet implemented")
        }

        override fun deleteCart(id: Long): Result<Long> = Result.success(1)

        override fun getMaxCartCount(): Result<Int> =
            runCatching {
                carts.size
            }

        override fun getCartItemsCounts(callback: (Result<QuantityResponse>) -> Unit) {
            TODO("Not yet implemented")
        }

        override fun getCuration(callback: (Result<List<CartProduct>>) -> Unit) {
            TODO("Not yet implemented")
        }
    }
