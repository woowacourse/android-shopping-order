package woowacourse.shopping.data.cart

import woowacourse.shopping.data.NetworkResult
import woowacourse.shopping.data.cart.remote.CartDataSource
import woowacourse.shopping.data.cart.remote.RemoteCartDataSource
import woowacourse.shopping.domain.Cart
import woowacourse.shopping.domain.repository.CartRepository
import woowacourse.shopping.domain.repository.LocalCartDataSource

class CartRepositoryImpl(
    private val remoteCartDataSource: CartDataSource = RemoteCartDataSource(),
    private val localCartDataSource: LocalCartDataSource,
) : CartRepository {
    override fun load(
        startPage: Int,
        pageSize: Int,
        callBack: (NetworkResult<List<Cart>>) -> Unit,
    ) {
        remoteCartDataSource.getCartItems(startPage, pageSize, callBack)
    }

    override fun saveNewCartItem(
        productId: Long,
        incrementAmount: Int,
        callBack: (NetworkResult<Long>) -> Unit,
    ) {
        remoteCartDataSource.saveCartItem(productId, incrementAmount, callBack)
    }

    override fun updateCartItemQuantity(
        cartId: Long,
        newQuantity: Int,
        callBack: (NetworkResult<Unit>) -> Unit,
    ) {
        if (newQuantity == 0) {
            deleteCartItem(cartId, callBack)
        } else {
            remoteCartDataSource.updateCartItemQuantity(cartId.toInt(), newQuantity, callBack)
        }
    }

    override fun deleteCartItem(
        cartId: Long,
        callBack: (NetworkResult<Unit>) -> Unit,
    ) {
        remoteCartDataSource.deleteCartItem(cartId.toInt(), callBack)
    }

    override fun getCount(callBack: (NetworkResult<Int>) -> Unit) {
        remoteCartDataSource.getTotalCount(callBack)
    }
}
