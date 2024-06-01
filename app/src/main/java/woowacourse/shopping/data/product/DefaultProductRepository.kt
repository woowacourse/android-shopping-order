package woowacourse.shopping.data.product

import woowacourse.shopping.data.cart.CartItemDataSource
import woowacourse.shopping.data.model.toDomain
import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.repository.product.ProductRepository

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

    override fun loadProduct(id: Long): Product = productDataSource.findById(id).toDomain(productQuantity(id))

    override fun isFinalPage(page: Int): Boolean = productDataSource.isFinalPage(page)

    override fun shoppingCartProductQuantity(): Int = cartItemDataSource.loadAll().sumOf { it.quantity }

    private fun productQuantity(productId: Long): Int {
        return cartItemDataSource.findByProductId(productId)?.quantity ?: 0
    }

    companion object {
        private const val TAG = "DefaultShoppingProductR"
    }
}
