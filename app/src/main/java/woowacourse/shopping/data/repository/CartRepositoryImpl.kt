package woowacourse.shopping.data.repository

import woowacourse.shopping.data.mapper.toCartItems
import woowacourse.shopping.domain.model.CartItem
import woowacourse.shopping.domain.repository.CartRepository
import woowacourse.shopping.remote.datasource.RemoteCartDataSource
import woowacourse.shopping.remote.dto.CartItemRequest
import woowacourse.shopping.remote.dto.CartQuantityDto

class CartRepositoryImpl(
    private val remoteCartDataSource: RemoteCartDataSource,
) : CartRepository {
    override suspend fun getCartItems(
        page: Int,
        size: Int,
        sort: String,
    ): Result<List<CartItem>> {
        return remoteCartDataSource.getCartResponse(page, size, sort)
            .mapCatching { it.toCartItems() }
    }

    override suspend fun addCartItem(
        productId: Int,
        quantity: Int,
    ): Result<Int> {
        val cartItemRequest = CartItemRequest(productId, quantity)
        return remoteCartDataSource.addCartItem(cartItemRequest)
    }

    override suspend fun deleteCartItem(cartItemId: Int): Result<Unit> {
        return remoteCartDataSource.deleteCartItem(cartItemId)
    }

    override suspend fun updateCartItem(
        cartItemId: Int,
        quantity: Int,
    ): Result<Unit> {
        val cartQuantityDto = CartQuantityDto(quantity)
        return remoteCartDataSource.updateCartItem(cartItemId, cartQuantityDto)
    }

    override suspend fun getCartTotalQuantity(): Result<Int> {
        return remoteCartDataSource.getCartTotalQuantity()
    }
}
