package woowacourse.shopping.data.source.product

import woowacourse.shopping.data.network.product.ProductDao
import woowacourse.shopping.domain.Product

class ProductDataSourceImpl(
    private val productDao: ProductDao,
) : ProductDataSource {
    override suspend fun loadProducts(
        startIndex: Int,
        pageSize: Int,
        sort: List<String>,
        category: String?,
    ): List<Product> {
        return productDao.findAllProduct(
            startIndex = startIndex,
            pageSize = pageSize,
            sort = sort,
            category = category,
        )
    }

    override suspend fun getProduct(id: String): Product {
        return productDao.findById(id = id)
    }
}
