package woowacourse.shopping.data.repository.product

import woowacourse.shopping.data.datasource.product.ProductDataSource
import woowacourse.shopping.domain.Product

class ProductRepositoryImpl(
    private val dataSource: ProductDataSource,
) : ProductRepository {
    override suspend fun loadProducts(
        startIndex: Int,
        pageSize: Int,
        sort: List<String>,
        category: String?,
    ): List<Product> = dataSource.findAllProduct(startIndex, pageSize, sort, category)

    override suspend fun getProduct(id: String): Product = dataSource.findById(id)
}
