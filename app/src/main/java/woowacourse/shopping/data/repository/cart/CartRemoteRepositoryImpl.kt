package woowacourse.shopping.data.repository.cart

import com.example.domain.model.CartProduct
import com.example.domain.model.Product
import com.example.domain.repository.CartRepository
import woowacourse.shopping.data.datasource.local.cart.CartCache
import woowacourse.shopping.data.datasource.remote.cart.CartRemoteDataSource

class CartRemoteRepositoryImpl(
    private val service: CartRemoteDataSource,
    private val cartCache: CartCache
) : CartRepository {

    override fun getAll(): List<CartProduct> {
        if (cartCache.cartProducts.isEmpty()) {
            val thread = Thread { cartCache.addCartProducts(service.loadAll()) }
            thread.start()
            thread.join()
        }
        return cartCache.cartProducts
    }

    override fun addProduct(product: Product, count: Int) {
        val findProduct = cartCache.cartProducts.find { it.product.id == product.id }
        if (findProduct != null) {
            val thread =
                Thread { service.updateCartProductCount(findProduct.cartProductId.toInt(), count) }
            thread.start()
            thread.join()
        } else {
            var cartItemId = -1
            val addThread = Thread { cartItemId = service.addCartProduct(product.id.toInt()) }
            addThread.start()
            addThread.join()
            if (cartItemId != -1) {
                val updateThread = Thread { service.updateCartProductCount(cartItemId, count) }
                updateThread.start()
                updateThread.join()
            }
        }
        refreshCache()
    }

    override fun deleteProduct(product: Product) {
        val cartId =
            cartCache.cartProducts.find { it.product.id == product.id }?.cartProductId ?: -1

        if (cartId != -1L) {
            val thread = Thread { service.deleteCartProduct(cartId.toInt()) }
            thread.start()
            thread.join()
            refreshCache()
        }
    }

    override fun updateSelection(product: Product, isSelected: Boolean) {
        cartCache.updateSelection(product, isSelected)
    }

    override fun getProductsByPage(page: Int, size: Int): List<CartProduct> {
        return cartCache.getCartProductsByPage(page, size)
    }

    private fun refreshCache() {
        cartCache.clear()
        val thread = Thread { cartCache.addCartProducts(service.loadAll()) }
        thread.start()
        thread.join()
    }
}
