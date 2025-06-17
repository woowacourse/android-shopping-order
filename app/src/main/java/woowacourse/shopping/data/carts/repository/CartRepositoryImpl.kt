package woowacourse.shopping.data.carts.repository

import woowacourse.shopping.data.carts.AddItemResult
import woowacourse.shopping.data.carts.dto.CartQuantity
import woowacourse.shopping.data.carts.dto.CartResponse
import woowacourse.shopping.data.util.api.ApiResult
import woowacourse.shopping.domain.model.Goods

class CartRepositoryImpl(
    private val remoteDataSource: CartRemoteDataSource,
) : CartRepository {
    override suspend fun fetchAllCartItems(): ApiResult<CartResponse> =
        remoteDataSource.fetchCartItemByOffset(
            Int.MAX_VALUE,
            0,
        )

    override suspend fun fetchCartItemsByOffset(
        limit: Int,
        offset: Int,
    ): ApiResult<CartResponse> =
        remoteDataSource.fetchCartItemByOffset(
            limit,
            offset,
        )

    override suspend fun updateQuantity(
        cartId: Int,
        cartQuantity: CartQuantity,
    ): ApiResult<Int> =
        remoteDataSource.updateCartItemCount(
            cartId = cartId,
            cartQuantity = cartQuantity,
        )

    override suspend fun delete(cartId: Int): ApiResult<Int> =
        remoteDataSource.deleteItem(
            cartId = cartId,
        )

    override suspend fun addCartItem(
        goods: Goods,
        quantity: Int,
    ): ApiResult<AddItemResult> = remoteDataSource.addItem(goods.id, quantity)
}
