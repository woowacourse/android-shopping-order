package woowacourse.shopping.data

import com.example.domain.model.CartProduct
import com.example.domain.model.Product
import com.example.domain.repository.CartRepository
import woowacourse.shopping.data.service.CartRemoteService

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
            service.updateCartProductCount(product.id.toInt(), count)
        } else {
            service.addCartProduct(product.id.toInt())
            service.updateCartProductCount(product.id.toInt(), count)
        }
        refreshCache()
    }

    override fun deleteProduct(product: Product) {
        service.deleteCartProduct(product.id.toInt())
        refreshCache()
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
