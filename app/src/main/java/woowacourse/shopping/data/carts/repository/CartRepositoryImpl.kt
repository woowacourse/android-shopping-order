package woowacourse.shopping.data.carts.repository

import android.util.Log
import woowacourse.shopping.data.carts.CartFetchError
import woowacourse.shopping.data.carts.dto.CartQuantity
import woowacourse.shopping.data.carts.dto.CartResponse
import woowacourse.shopping.data.util.mapper.toCartItems
import woowacourse.shopping.domain.model.CartItem
import woowacourse.shopping.domain.model.Goods

class CartRepositoryImpl(
    private val remoteDataSource: CartRemoteDataSource,
) : CartRepository {
    override fun checkValidBasicKey(onResponse: (Int) -> Unit) {
        remoteDataSource.fetchAuthCode { code ->
            onResponse(code)
        }
    }

    override fun fetchAllCartItems(
        onComplete: (List<CartItem>) -> Unit,
        onFail: (Throwable) -> Unit,
    ) {
        remoteDataSource.fetchCartItemByOffset(
            Int.MAX_VALUE,
            0,
            { response ->
                onComplete(getCartItemByCartResponse(response))
            },
            { },
        )
    }

    override fun fetchCartItemsByOffset(
        limit: Int,
        offset: Int,
        onComplete: (List<CartItem>) -> Unit,
        onFail: (CartFetchError) -> Unit,
    ) {
        remoteDataSource.fetchCartItemByOffset(
            limit,
            offset,
            { response ->
                onComplete(getCartItemByCartResponse(response))
            },
            { response ->
                onFail(response)
            },
        )
    }

    override fun fetchCartItemsByPage(
        page: Int,
        size: Int,
        onComplete: (List<CartItem>) -> Unit,
        onFail: (CartFetchError) -> Unit,
    ) {
        remoteDataSource.fetchCartItemByOffset(
            page,
            size,
            { response ->
                onComplete(getCartItemByCartResponse(response))
            },
            { response ->
                onFail(response)
            },
        )
    }

    private fun getCartItemByCartResponse(cartResponse: CartResponse): List<CartItem> = cartResponse.toCartItems()

    override fun addOrIncreaseQuantity(
        goods: Goods,
        addQuantity: Int,
        onComplete: () -> Unit,
    ) {
        fetchAllCartItems({ cartItems: List<CartItem> ->
            if (goods.id in cartItems.map { it.goods.id }) {
            } else {
                addCartItem(goods) {
                }
            }
        }, {
        })
    }

    override fun updateQuantity(
        cartId: Int,
        cartQuantity: CartQuantity,
        onComplete: () -> Unit,
        onFail: (CartFetchError) -> Unit,
    ) {
        Log.d("updateQuantity리포지토리", "$cartId, $cartQuantity")
        remoteDataSource.updateItemCount(
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

    private fun addCartItem(
        goods: Goods,
        onComplete: () -> Unit,
    ) {
    }

    override fun removeOrDecreaseQuantity(
        goods: Goods,
        removeQuantity: Int,
        onComplete: () -> Unit,
    ) {
        // Todo
    }

    override fun delete(
        goods: Goods,
        onComplete: () -> Unit,
    ) {
        // Todo
    }

    override fun getAllItemsSize(onComplete: (Int) -> Unit) {
        // Todo
    }
}
