package woowacourse.shopping.data.repository.product

import woowacourse.shopping.data.datasource.product.ProductDataSource
import woowacourse.shopping.domain.Product
import woowacourse.shopping.domain.ProductPage

class ProductRepositoryImpl(
    private val dataSource: ProductDataSource,
) : ProductRepository {
    override suspend fun loadProducts(
        startIndex: Int,
        pageSize: Int,
        sort: List<String>,
        category: String?,
    ): ProductPage = dataSource.findAllProduct(startIndex, pageSize, sort, category)

    override suspend fun getProduct(id: String): Product = dataSource.findById(id)
}
