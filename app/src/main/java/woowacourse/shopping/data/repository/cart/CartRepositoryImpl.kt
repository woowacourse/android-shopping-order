package woowacourse.shopping.data.repository.cart

import com.example.domain.model.CartProduct
import com.example.domain.model.CartProducts
import com.example.domain.model.Product
import com.example.domain.repository.CartRepository
import woowacourse.shopping.data.datasource.remote.cart.CartRemoteDataSource

class CartRepositoryImpl(
    private val remoteDataSource: CartRemoteDataSource,
) : CartRepository {

    override fun getAll(): CartProducts {
        return CartProducts(remoteDataSource.loadAll())
    }

    override fun addProduct(product: Product): Int {
        return remoteDataSource.addCartProduct(product.id.toInt())
    }

    override fun updateProduct(cartItemId: Int, count: Int) {
        remoteDataSource.updateCartProductCount(cartItemId, count)
    }

    override fun deleteProduct(cartItemId: Int) {
        remoteDataSource.deleteCartProduct(cartItemId)
    }

    override fun getCartProductByProduct(product: Product): CartProduct? {
        return remoteDataSource.loadAll().find { it.product.id == product.id }
    }
}
