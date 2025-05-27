package woowacourse.shopping.data.carts.repository

import woowacourse.shopping.data.carts.CartFetchError
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
        remoteDataSource.fetchPageCartItem(
            Int.MAX_VALUE,
            0,
            { response ->
                onComplete(getCartItemByCartResponse(response))
            },
            { },
        )
    }

    override fun fetchPageCartItems(
        limit: Int,
        offset: Int,
        onComplete: (List<CartItem>) -> Unit,
        onFail: (CartFetchError) -> Unit,
    ) {
        remoteDataSource.fetchPageCartItem(
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

    private fun getCartItemByCartResponse(cartResponse: CartResponse): List<CartItem> = cartResponse.toCartItems()

    override fun addOrIncreaseQuantity(
        goods: Goods,
        addQuantity: Int,
        onComplete: () -> Unit,
    ) {
        // Todo
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
