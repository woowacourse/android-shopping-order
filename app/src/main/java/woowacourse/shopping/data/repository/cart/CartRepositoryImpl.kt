package woowacourse.shopping.data.repository.cart

import com.example.domain.model.CartProduct
import com.example.domain.model.CartProducts
import com.example.domain.model.Product
import com.example.domain.repository.CartRepository
import woowacourse.shopping.data.datasource.remote.cart.CartRemoteDataSource

class CartRepositoryImpl(
    private val service: CartRemoteDataSource,
) : CartRepository {

    override fun getAll(): CartProducts {
        var cartProducts = emptyList<CartProduct>()

        val thread = Thread { cartProducts = service.loadAll() }
        thread.start()
        thread.join()

        return CartProducts(cartProducts)
    }

    override fun addProduct(product: Product): Int {
        var cartItemId = -1
        val thread = Thread { cartItemId = service.addCartProduct(product.id.toInt()) }
        thread.start()
        thread.join()
        return cartItemId
    }

    override fun updateProduct(cartItemId: Int, count: Int) {
        val thread = Thread { service.updateCartProductCount(cartItemId, count) }
        thread.start()
        thread.join()
    }

    override fun deleteProduct(cartItemId: Int) {
        val thread = Thread { service.deleteCartProduct(cartItemId) }
        thread.start()
        thread.join()
    }

    override fun getCartProductByProduct(product: Product): CartProduct? {
        var cartProducts = emptyList<CartProduct>()
        val thread = Thread { cartProducts = service.loadAll() }
        thread.start()
        thread.join()
        return cartProducts.find { it.product.id == product.id }
    }
}
