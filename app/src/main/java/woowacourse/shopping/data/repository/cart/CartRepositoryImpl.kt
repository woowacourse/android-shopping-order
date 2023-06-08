package woowacourse.shopping.data.repository.cart

import com.example.domain.model.CartProduct
import com.example.domain.model.CartProducts
import com.example.domain.model.FailureInfo
import com.example.domain.model.Product
import com.example.domain.repository.CartRepository
import woowacourse.shopping.data.datasource.remote.cart.CartRemoteDataSource

class CartRepositoryImpl(
    private val remoteDataSource: CartRemoteDataSource,
) : CartRepository {

    override fun getAll(
        onSuccess: (CartProducts) -> Unit,
        onFailure: (FailureInfo) -> Unit
    ) {
        remoteDataSource.loadAll(onSuccess, onFailure)
    }

    override fun addProduct(
        product: Product,
        onSuccess: (cartItemId: Int) -> Unit,
        onFailure: (FailureInfo) -> Unit
    ) {
        remoteDataSource.addCartProduct(product.id.toInt(), onSuccess, onFailure)
    }

    override fun updateProduct(
        cartItemId: Int,
        count: Int,
        onSuccess: () -> Unit,
        onFailure: (FailureInfo) -> Unit
    ) {
        remoteDataSource.updateCartProductCount(cartItemId, count, onSuccess, onFailure)
    }

    override fun deleteProduct(
        cartItemId: Int,
        onSuccess: () -> Unit,
        onFailure: (FailureInfo) -> Unit
    ) {
        remoteDataSource.deleteCartProduct(cartItemId, onSuccess, onFailure)
    }

    override fun getCartProductByProduct(
        product: Product,
        onSuccess: (CartProduct?) -> Unit,
        onFailure: (FailureInfo) -> Unit
    ) {
        remoteDataSource.getCartProductByProductId(product.id.toInt(), onSuccess, onFailure)
    }
}
