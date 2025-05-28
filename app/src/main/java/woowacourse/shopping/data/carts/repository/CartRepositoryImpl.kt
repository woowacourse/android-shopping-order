package woowacourse.shopping.data.carts.repository

import woowacourse.shopping.data.carts.CartFetchError
import woowacourse.shopping.data.carts.dto.CartQuantity
import woowacourse.shopping.data.carts.dto.CartResponse
import woowacourse.shopping.data.util.mapper.toCartItems
import woowacourse.shopping.domain.model.CartItem
import woowacourse.shopping.domain.model.Goods

class CartRepositoryImpl(
    private val remoteDataSource: CartRemoteDataSource,
) : CartRepository {
    override fun checkValidBasicKey(
        validKey: String,
        onResponse: (Int) -> Unit,
        onFail: (CartFetchError) -> Unit,
    ) {
        remoteDataSource.fetchAuthCode(validKey, onResponse, onFail)
    }

    override fun fetchAllCartItems(
        onComplete: (CartResponse) -> Unit,
        onFail: (Throwable) -> Unit,
    ) {
        remoteDataSource.fetchCartItemByOffset(
            Int.MAX_VALUE,
            0,
            { response ->
                onComplete(response)
            },
            { },
        )
    }

    override fun fetchCartItemsByOffset(
        limit: Int,
        offset: Int,
        onComplete: (CartResponse) -> Unit,
        onFail: (CartFetchError) -> Unit,
    ) {
        remoteDataSource.fetchCartItemByOffset(
            limit,
            offset,
            { response ->
                onComplete(response)
            },
            { response ->
                onFail(response)
            },
        )
    }

    override fun fetchCartItemsByPage(
        page: Int,
        size: Int,
        onComplete: (CartResponse) -> Unit,
        onFail: (CartFetchError) -> Unit,
    ) {
        remoteDataSource.fetchCartItemByPage(
            page,
            size,
            { response ->
                onComplete(response)
            },
            { response ->
                onFail(response)
            },
        )
    }

    private fun getCartItemByCartResponse(cartResponse: CartResponse): List<CartItem> = cartResponse.toCartItems()

    override fun updateQuantity(
        cartId: Int,
        cartQuantity: CartQuantity,
        onComplete: () -> Unit,
        onFail: (CartFetchError) -> Unit,
    ) {
        remoteDataSource.updateCartItemCount(
            cartId = cartId,
            cartQuantity = cartQuantity,
            onSuccess = { resultCode ->
                onComplete()
            },
            onFailure = { error ->
                onFail(error)
            },
        )
    }

    override fun delete(
        cartId: Int,
        onComplete: (Int) -> Unit,
    ) {
        remoteDataSource.deleteItem(
            cartId = cartId,
            onSuccess = onComplete,
        )
    }

    override fun addCartItem(
        goods: Goods,
        quantity: Int,
        onComplete: (Int) -> Unit,
        onFail: (CartFetchError) -> Unit,
    ) {
        remoteDataSource.addItem(goods.id, quantity, onComplete, onFail)
    }

    override fun getAllItemsSize(onComplete: (Int) -> Unit) {
        // Todo
    }
}
