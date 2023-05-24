package woowacourse.shopping.data.product

import woowacourse.shopping.Product
import woowacourse.shopping.Products
import woowacourse.shopping.repository.ProductRepository

class ProductRepositoryImpl constructor(
    private val productRemoteDataSource: ProductRemoteDataSource,
) : ProductRepository {
    override fun findProductById(id: Int): Product {
        return productRemoteDataSource.findProductById(id)
    }

    override fun getProductsWithRange(start: Int, size: Int): List<Product> {
        val products = Products(productRemoteDataSource.products)
        return products.getItemsInRange(start, size).items
    }
}
