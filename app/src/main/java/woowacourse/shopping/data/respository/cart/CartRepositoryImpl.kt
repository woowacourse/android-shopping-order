package woowacourse.shopping.data.respository.cart

import com.example.domain.cart.CartProduct
import com.example.domain.cart.CartProducts
import woowacourse.shopping.data.mapper.toDomain
import woowacourse.shopping.data.mapper.toEntity
import woowacourse.shopping.data.model.CartLocalEntity
import woowacourse.shopping.data.respository.cart.source.local.CartLocalDataSource
import woowacourse.shopping.data.respository.cart.source.remote.CartRemoteDataSource

class CartRepositoryImpl(
    private val cartLocalDataSource: CartLocalDataSource,
    private val cartRemoteDataSource: CartRemoteDataSource,
) : CartRepository {

    override fun addCartProduct(
        productId: Long,
        onFailure: () -> Unit,
        onSuccess: (cartId: Long) -> Unit,
    ) {
        cartRemoteDataSource.requestPostCartItem(productId, onFailure, onSuccess)
    }

    override fun loadAllCarts(
        onFailure: () -> Unit,
        onSuccess: (products: CartProducts) -> Unit,
    ) {
        cartRemoteDataSource.requestDatas(onFailure) { cartRemoteEntities ->
            onSuccess(CartProducts(cartRemoteEntities.map { it.toDomain() }))
        }
    }

    override fun updateCartCount(
        cartProduct: CartProduct,
        onFailure: () -> Unit,
        onSuccess: () -> Unit,
    ) {
        cartRemoteDataSource.requestPatchCartItem(cartProduct.toEntity(), onFailure, onSuccess)
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
