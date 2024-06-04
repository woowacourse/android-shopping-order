package woowacourse.shopping.domain.repository

import android.util.Log
import woowacourse.shopping.data.model.toDomain
import woowacourse.shopping.data.source.ProductDataSource
import woowacourse.shopping.data.source.ShoppingCartDataSource
import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.model.ProductIdsCount
import woowacourse.shopping.ui.model.CartItem

class DefaultShoppingProductRepository(
    private val productsSource: ProductDataSource,
    private val cartSource: ShoppingCartDataSource,
) : ShoppingProductsRepository {
    override fun loadAllProducts(page: Int): List<Product> {
        val productsData = productsSource.findByPaged(page)
        return productsData.map { productData ->
            productData.toDomain(productQuantity(productData.id))
        }
    }

    override fun loadPagedCartItem(): List<CartItem> {
        return cartSource.loadAllCartItems().map {
            CartItem(
                id = it.id,
                quantity = it.quantity,
                product = it.product,
                checked = false,
            )
        }
    }

    override fun loadProduct(id: Long): Product = productsSource.findById(id).toDomain(productQuantity(id))

    override fun isFinalPage(page: Int): Boolean = productsSource.isFinalPage(page)

    override fun shoppingCartProductQuantity(): Int = cartSource.loadAllCartItems().sumOf { it.quantity }

    override fun increaseInShoppingCart(cartItemId: Long, quantity: Int) {
        cartSource.plusProductsIdCount(cartItemId, quantity)
    }

    override fun decreaseInShoppingCart(cartItemId: Long, quantity: Int) {
        cartSource.minusProductsIdCount(cartItemId, quantity)
    }

    private fun productQuantity(productId: Long): Int {
        return cartSource.findByProductId(productId)?.quantity ?: 0
    }

    override fun increaseShoppingCartProduct(
        id: Long,
        quantity: Int,
    ) {
        val all = cartSource.loadAllCartItems()
        val cartItem = all.find { it.product.id == id } ?: throw NoSuchElementException()
        cartSource.plusProductsIdCount(cartItem.id, quantity)
    }

    override fun decreaseShoppingCartProduct(
        id: Long,
        quantity: Int,
    ) {
        cartSource.minusProductsIdCount(id, quantity)
    }

    override fun addShoppingCartProduct(
        id: Long,
        quantity: Int,
    ) {
        val all = cartSource.loadAllCartItems()
        val cartItem = all.find { it.product.id == id }

        if (cartItem == null) {
            cartSource.addedNewProductsId(ProductIdsCount(id, quantity))
            return
        }

        cartSource.plusProductsIdCount(cartItem.id, quantity = cartItem.quantity + quantity)
    }

    override fun removeShoppingCartProduct(id: Long) {
        cartSource.removedProductsId(id)
    }

    companion object {
        private const val TAG = "DefaultShoppingProductR"
    }
}
