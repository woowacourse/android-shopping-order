package woowacourse.shopping.data.carts.repository

import woowacourse.shopping.data.carts.AddItemResult
import woowacourse.shopping.data.carts.CartFetchResult
import woowacourse.shopping.data.carts.CartUpdateResult
import woowacourse.shopping.data.carts.dto.CartQuantity
import woowacourse.shopping.data.carts.dto.CartResponse
import woowacourse.shopping.domain.model.Goods

class CartRepositoryImpl(
    private val remoteDataSource: CartRemoteDataSource,
) : CartRepository {
    override suspend fun fetchAllCartItems(): CartFetchResult<CartResponse> =
        remoteDataSource.fetchCartItemByOffset(
            Int.MAX_VALUE,
            0,
        )

    override suspend fun fetchCartItemsByOffset(
        limit: Int,
        offset: Int,
    ): CartFetchResult<CartResponse> =
        remoteDataSource.fetchCartItemByOffset(
            limit,
            offset,
        )

    override suspend fun updateQuantity(
        cartId: Int,
        cartQuantity: CartQuantity,
    ): CartUpdateResult<Int> =
        remoteDataSource.updateCartItemCount(
            cartId = cartId,
            cartQuantity = cartQuantity,
        )

    override suspend fun delete(cartId: Int): CartFetchResult<Int> =
        remoteDataSource.deleteItem(
            cartId = cartId,
        )

    override suspend fun addCartItem(
        goods: Goods,
        quantity: Int,
    ): CartFetchResult<AddItemResult> = remoteDataSource.addItem(goods.id, quantity)
}
