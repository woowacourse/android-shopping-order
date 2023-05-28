package woowacourse.shopping.data.repository.cart

import com.example.domain.model.CartProduct
import com.example.domain.model.Product
import com.example.domain.repository.CartRepository
import woowacourse.shopping.data.datasource.local.cart.CartCache
import woowacourse.shopping.data.datasource.remote.cart.CartRemoteService

class CartRemoteRepositoryImpl(
    private val service: CartRemoteService,
    private val cartCache: CartCache
) : CartRepository {

    override fun getAll(): List<CartProduct> {
        if (cartCache.cartProducts.isEmpty()) {
            cartCache.addCartProducts(service.loadAll())
        }
        return cartCache.cartProducts
    }

    override fun addProduct(product: Product, count: Int) {
        val findProduct = cartCache.cartProducts.find { it.product.id == product.id }
        if (findProduct != null) {
            service.updateCartProductCount(findProduct.cartProductId.toInt(), count)
        } else {
            val cartItemId = service.addCartProduct(product.id.toInt())
            if (cartItemId != -1) {
                service.updateCartProductCount(cartItemId, count)
            }
        }
        refreshCache()
    }

    override fun deleteProduct(product: Product) {
        val cartId =
            cartCache.cartProducts.find { it.product.id == product.id }?.cartProductId ?: -1

        if (cartId != -1L) {
            service.deleteCartProduct(cartId.toInt())
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
        cartCache.addCartProducts(service.loadAll())
    }
}
