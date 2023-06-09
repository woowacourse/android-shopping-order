package woowacourse.shopping.data.respository.cart

import woowacourse.shopping.data.mapper.toModel
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
        onFailure: (throwable: Throwable) -> Unit,
        onSuccess: (Long) -> Unit,
    ) {
        cartRemoteDataSource.requestPostCartItem(productId, onFailure) {
            cartLocalDataSource.insertCart(it)
            onSuccess(it)
        }
    }

    override fun loadAllCarts(
        onFailure: (throwable: Throwable) -> Unit,
        onSuccess: (products: List<CartProduct>) -> Unit
    ) {
        cartRemoteDataSource.requestDatas(onFailure) { carts ->
            onSuccess(carts.map { it.toModel() })
        }
    }

    override fun loadCartsByCartIds(
        cartIds: ArrayList<Long>,
        onFailure: (throwable: Throwable) -> Unit,
        onSuccess: (products: List<CartProduct>) -> Unit
    ) {
        cartRemoteDataSource.requestDatas(onFailure) { allCarts ->
            onSuccess(allCarts.filter { it.id in cartIds }.map { it.toModel() })
        }
    }

    override fun loadAllCartChecked(): List<CartProduct> {
        return cartLocalDataSource.selectAllCarts()
    }

    override fun updateCartCount(
        cartProduct: CartProduct,
        onFailure: (throwable: Throwable) -> Unit,
        onSuccess: () -> Unit
    ) {
        cartRemoteDataSource.requestPatchCartItem(cartProduct, onFailure, onSuccess)
        if (cartProduct.count == 0) cartLocalDataSource.deleteCart(cartProduct.id)
    }

    override fun updateCartChecked(cartId: Long, isChecked: Boolean) {
        cartLocalDataSource.updateCartChecked(cartId, isChecked)
    }

    override fun deleteCart(cartId: Long) {
        cartRemoteDataSource.requestDeleteCartItem(cartId)
        cartLocalDataSource.deleteCart(cartId)
    }

    override fun deleteCarts(cartIds: List<Long>) {
        cartLocalDataSource.deleteCarts(cartIds)
    }
}
