package woowacourse.shopping.domain.repository.product

import woowacourse.shopping.data.model.toDomain
import woowacourse.shopping.data.source.product.ProductDataSource
import woowacourse.shopping.data.source.cart.CartItemDataSource
import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.model.ProductIdsCount
import woowacourse.shopping.ui.model.CartItem

class DefaultProductRepository(
    private val productDataSource: ProductDataSource,
    private val cartItemDataSource: CartItemDataSource,
) : ProductRepository {
    override fun loadAllProducts(page: Int): List<Product> {
        val productsData = productDataSource.findByPaged(page)
        return productsData.map { productData ->
            productData.toDomain(productQuantity(productData.id))
        }
    }

    override fun loadPagedCartItem(): List<CartItem> {
        return cartItemDataSource.loadAllCartItems().map {
            CartItem(
                id = it.id,
                quantity = it.quantity,
                product = it.product,
                checked = false,
            )
        }
    }

    override fun loadProduct(id: Long): Product = productDataSource.findById(id).toDomain(productQuantity(id))

    override fun isFinalPage(page: Int): Boolean = productDataSource.isFinalPage(page)

    override fun isCartFinalPage(page: Int): Boolean = cartItemDataSource.isFinalPage(page)

    override fun shoppingCartProductQuantity(): Int = cartItemDataSource.loadAll().sumOf { it.quantity }

    private fun productQuantity(productId: Long): Int {
        return cartItemDataSource.findByProductId(productId)?.quantity ?: 0
    }

    override fun increaseShoppingCartProduct(
        id: Long,
        quantity: Int,
    ) {
        val all = cartItemDataSource.loadAllCartItems()
        val cartItem = all.find { it.product.id == id } ?: throw NoSuchElementException()
        cartItemDataSource.plusProductsIdCount(cartItem.id, quantity)
    }

    override fun decreaseShoppingCartProduct(
        id: Long,
        quantity: Int,
    ) {
        cartItemDataSource.minusProductsIdCount(id, quantity)
    }

    override fun addShoppingCartProduct(
        id: Long,
        quantity: Int,
    ) {
        val all = cartItemDataSource.loadAllCartItems()
        val cartItem = all.find { it.product.id == id }

        if (cartItem == null) {
            cartItemDataSource.addedNewProductsId(ProductIdsCount(id, quantity))
            return
        }

        cartItemDataSource.plusProductsIdCount(cartItem.id, quantity = cartItem.quantity + quantity)
    }

    override fun removeShoppingCartProduct(id: Long) {
        cartItemDataSource.removedProductsId(id)
    }

    companion object {
        private const val TAG = "DefaultShoppingProductR"
    }
}
