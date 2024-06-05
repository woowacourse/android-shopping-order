package woowacourse.shopping.domain.repository

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
    override fun pagedProducts(page: Int): List<Product> {
        val productsData = productsSource.findByPaged(page)
        return productsData.map { productData ->
            productData.toDomain(productQuantity(productData.id))
        }
    }

    override fun allProductsUntilPage(page: Int): List<Product> {
        val productsData = productsSource.findAllUntilPage(page)
        return productsData.map { productData ->
            productData.toDomain(productQuantity(productData.id))
        }
    }

    override fun loadProduct(id: Long): Product = productsSource.findById(id).toDomain(productQuantity(id))

    override fun isFinalPage(page: Int): Boolean = productsSource.isFinalPage(page)

    private fun productQuantity(productId: Long): Int {
        return cartSource.findByProductId(productId)?.quantity ?: 0
    }

    companion object {
        private const val TAG = "DefaultShoppingProductRepository"
    }
}
