package woowacourse.shopping.data.repository

import woowacourse.shopping.data.dto.CartItemRequest
import woowacourse.shopping.data.dto.CartQuantityDto
import woowacourse.shopping.data.mapper.toCartItems
import woowacourse.shopping.data.remote.datasource.CartDataSourceImpl
import woowacourse.shopping.domain.model.CartItem
import woowacourse.shopping.domain.repository.CartRepository

class CartRepositoryImpl(
    private val cartDataSourceImpl: CartDataSourceImpl,
) : CartRepository {
    override suspend fun getCartItems(
        page: Int,
        size: Int,
        sort: String,
    ): Result<List<CartItem>> {
        return cartDataSourceImpl.getCartResponse(page, size, sort).mapCatching { it.toCartItems() }
    }

    override suspend fun addCartItem(
        productId: Int,
        quantity: Int,
    ): Result<Int> {
        val cartItemRequest = CartItemRequest(productId, quantity)
        return cartDataSourceImpl.addCartItem(cartItemRequest)
    }

    override suspend fun deleteCartItem(cartItemId: Int): Result<Unit> {
        return cartDataSourceImpl.deleteCartItem(cartItemId)
    }

    override suspend fun updateCartItem(
        cartItemId: Int,
        quantity: Int,
    ): Result<Unit> {
        val cartQuantityDto = CartQuantityDto(quantity)
        return cartDataSourceImpl.updateCartItem(cartItemId, cartQuantityDto)
    }

    override suspend fun getCartTotalQuantity(): Result<Int> {
        return cartDataSourceImpl.getCartTotalQuantity()
    }
}
