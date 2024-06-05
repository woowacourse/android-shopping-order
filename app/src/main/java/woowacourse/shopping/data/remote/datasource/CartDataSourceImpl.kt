package woowacourse.shopping.data.remote.datasource

import woowacourse.shopping.data.datasource.CartDataSource
import woowacourse.shopping.data.dto.CartItemDto
import woowacourse.shopping.data.dto.CartItemRequest
import woowacourse.shopping.data.dto.CartQuantityDto
import woowacourse.shopping.data.remote.service.CartService

class CartDataSourceImpl(
    private val cartService: CartService,
) : CartDataSource {
    override fun getCartItems(
        page: Int,
        size: Int,
        sort: String,
    ): Result<List<CartItemDto>> {
        return runCatching {
            cartService.getCartItems(page, size, sort).execute().body()?.cartItems
                ?: throw IllegalArgumentException()
        }
    }

    override fun addCartItem(cartItemRequest: CartItemRequest): Result<Unit> {
        return runCatching {
            cartService.addCartItem(cartItemRequest).execute()
        }
    }

    override fun deleteCartItem(productId: Int): Result<Unit> {
        return runCatching {
            cartService.deleteCartItem(productId).execute()
        }
    }

    override fun updateCartItem(
        productId: Int,
        cartQuantityDto: CartQuantityDto,
    ): Result<Unit> {
        return runCatching {
            cartService.updateCartItem(productId, cartQuantityDto).execute()
        }
    }

    override fun getCartTotalQuantity(): Result<Int> {
        return runCatching {
            cartService.getCartTotalQuantity().execute().body()?.quantity ?: 0
        }
    }
}
