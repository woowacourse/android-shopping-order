package woowacourse.shopping.data.respository.cart

import woowacourse.shopping.data.respository.cart.source.local.CartLocalDataSource
import woowacourse.shopping.data.respository.cart.source.remote.CartRemoteDataSource
import woowacouse.shopping.data.repository.cart.CartRepository
import woowacouse.shopping.model.cart.CartProduct

class CartRepositoryImpl(
    private val cartLocalDataSource: CartLocalDataSource,
    private val cartRemoteDataSource: CartRemoteDataSource,
) : CartRepository {

    override fun addCartProduct(
        productId: Long,
        onFailure: () -> Unit,
        onSuccess: (Long) -> Unit,
    ) {
        cartRemoteDataSource.requestPostCartItem(productId, onFailure, onSuccess)
    }

    override fun loadAllCarts(
        onFailure: () -> Unit,
        onSuccess: (products: List<CartProduct>) -> Unit
    ) {
        cartRemoteDataSource.requestDatas(onFailure, onSuccess)
    }

    override fun loadCartsByCartIds(
        cartIds: ArrayList<Long>,
        onFailure: () -> Unit,
        onSuccess: (products: List<CartProduct>) -> Unit
    ) {
        cartRemoteDataSource.requestDatas(onFailure) { allCarts ->
            onSuccess(allCarts.filter { it.id in cartIds })
        }
    }

    override fun updateCartCount(
        cartProduct: CartProduct,
        onFailure: () -> Unit,
        onSuccess: () -> Unit
    ) {
        cartRemoteDataSource.requestPatchCartItem(cartProduct, onFailure, onSuccess)
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

    override fun getAllLocalCart(): List<CartProduct> {
        return cartLocalDataSource.selectAllCarts()
    }

    override fun deleteCart(cartId: Long) {
        cartRemoteDataSource.requestDeleteCartItem(cartId)
    }
}
