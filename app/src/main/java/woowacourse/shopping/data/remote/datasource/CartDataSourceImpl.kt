package woowacourse.shopping.data.remote.datasource

import woowacourse.shopping.data.datasource.CartDataSource
import woowacourse.shopping.data.dto.CartItemRequest
import woowacourse.shopping.data.dto.CartQuantityDto
import woowacourse.shopping.data.dto.CartResponse
import woowacourse.shopping.data.remote.service.CartService

class CartDataSourceImpl(
    private val cartService: CartService,
) : CartDataSource {
    override suspend fun getCartResponse(
        page: Int,
        size: Int,
        sort: String,
    ): Result<CartResponse> {
        return runCatching {
            cartService.getCartItems(page, size, sort)
        }
    }

    override suspend fun addCartItem(cartItemRequest: CartItemRequest): Result<Int> {
        return runCatching {
            val location =
                cartService.addCartItem(cartItemRequest).headers()["location"]
                    ?: throw IllegalArgumentException()
            val segments = location.split("/")
            val cartItemId = segments.last().toInt()
            cartItemId
        }
    }

    override suspend fun deleteCartItem(productId: Int): Result<Unit> {
        return runCatching {
            cartService.deleteCartItem(productId)
        }
    }

    override suspend fun updateCartItem(
        productId: Int,
        cartQuantityDto: CartQuantityDto,
    ): Result<Unit> {
        return runCatching {
            cartService.updateCartItem(productId, cartQuantityDto)
        }
    }

    override suspend fun getCartTotalQuantity(): Result<Int> {
        return runCatching {
            cartService.getCartTotalQuantity().body()?.quantity ?: 0
        }
    }
}
