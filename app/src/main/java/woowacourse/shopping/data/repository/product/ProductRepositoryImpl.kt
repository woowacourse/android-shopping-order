package woowacourse.shopping.data.repository.product

import woowacourse.shopping.data.source.product.ProductDataSource
import woowacourse.shopping.domain.Product

class ProductRepositoryImpl(
    private val dataSource: ProductDataSource,
) : ProductRepository {
    override suspend fun loadProducts(
        startIndex: Int,
        pageSize: Int,
        sort: List<String>,
        category: String?,
    ): Pair<List<Product>, Boolean> = dataSource.loadProducts(startIndex, pageSize, sort, category)

    override suspend fun getProduct(id: Long): Product = dataSource.getProduct(id)
}
