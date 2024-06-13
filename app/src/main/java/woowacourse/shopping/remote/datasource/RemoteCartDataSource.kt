package woowacourse.shopping.remote.datasource

import woowacourse.shopping.data.datasource.CartDataSource
import woowacourse.shopping.data.mapper.toCartItemId
import woowacourse.shopping.remote.dto.CartItemRequest
import woowacourse.shopping.remote.dto.CartQuantityDto
import woowacourse.shopping.remote.dto.CartResponse
import woowacourse.shopping.remote.service.CartService

class RemoteCartDataSource(
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
            location.toCartItemId()
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
