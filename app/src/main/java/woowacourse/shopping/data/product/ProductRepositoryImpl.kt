package woowacourse.shopping.data.product

import woowacourse.shopping.Product
import woowacourse.shopping.Products
import woowacourse.shopping.repository.ProductRepository

class ProductRepositoryImpl constructor(
    private val productRemoteDataSource: ProductRemoteDataSource,
) : ProductRepository {
    private val products: Products = Products(productRemoteDataSource.getAllProducts())
    override fun findProductById(id: Int): Product {
        return productRemoteDataSource.findProductById(id)
    }

    override fun getProductsWithRange(start: Int, size: Int): List<Product> {
        return products.getItemsInRange(start, size).items
    }
}
