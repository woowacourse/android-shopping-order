package woowacourse.shopping.data.source.product

import woowacourse.shopping.domain.Product

class ProductDataSourceImpl(
    private val productDataSource: ProductDataSource,
) : ProductDataSource {
    override suspend fun loadProducts(
        startIndex: Int,
        pageSize: Int,
        sort: List<String>,
        category: String?,
    ): Pair<List<Product>, Boolean> =
        productDataSource.loadProducts(
            startIndex = startIndex,
            pageSize = pageSize,
            sort = sort,
            category = category,
        )

    override suspend fun getProduct(id: Long): Product = productDataSource.getProduct(id = id)
}
