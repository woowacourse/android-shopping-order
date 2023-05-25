package woowacourse.shopping.data.respository.cart

import woowacourse.shopping.data.model.CartLocalEntity
import woowacourse.shopping.data.model.CartRemoteEntity
import woowacourse.shopping.data.respository.cart.source.local.CartLocalDataSource
import woowacourse.shopping.data.respository.cart.source.remote.CartRemoteDataSource

class CartRepositoryImpl(
    private val cartLocalDataSource: CartLocalDataSource,
    private val cartRemoteDataSource: CartRemoteDataSource,
) : CartRepository {

    override fun addCartProduct(
        productId: Long,
        onFailure: () -> Unit,
        onSuccess: () -> Unit,
    ) {
        cartRemoteDataSource.requestPostCartItem(productId, onFailure, onSuccess)
    }

    override fun loadAllCarts(
        onFailure: () -> Unit,
        onSuccess: (products: List<CartRemoteEntity>) -> Unit,
    ) {
        cartRemoteDataSource.requestDatas(onFailure, onSuccess)
    }

    override fun updateCartCount(
        cartEntity: CartRemoteEntity,
        onFailure: () -> Unit,
        onSuccess: () -> Unit,
    ) {
        cartRemoteDataSource.requestPatchCartItem(cartEntity, onFailure, onSuccess)
    }

    override fun addLocalCart(cartId: Long) {
        cartLocalDataSource.insertCart(cartId)
    }

    override fun deleteLocalCart(cartId: Long) {
        cartLocalDataSource.deleteCart(cartId)
    }

    override fun updateLocalCartChecked(cartId: Long, isChecked: Boolean) {
        cartLocalDataSource.updateCartChecked(cartId, isChecked)
    }

    override fun getAllLocalCart(): List<CartLocalEntity> {
        return cartLocalDataSource.selectAllCarts()
    }

    override fun deleteCart(cartId: Long) {
        cartRemoteDataSource.requestDeleteCartItem(cartId)
    }
}
