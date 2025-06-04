package woowacourse.shopping.data.repository

import woowacourse.shopping.data.datasource.ProductsDataSource
import woowacourse.shopping.domain.exception.NetworkResult
import woowacourse.shopping.domain.product.Product
import woowacourse.shopping.domain.product.ProductSinglePage
import woowacourse.shopping.domain.repository.ProductRepository

class DefaultProductRepository(
    private val productDataSource: ProductsDataSource,
) : ProductRepository {
    override fun loadSinglePage(
        category: String?,
        page: Int?,
        pageSize: Int?,
        callback: (Result<ProductSinglePage>) -> Unit,
    ) {
        productDataSource.singlePage(category, page, pageSize) { callback(it) }
    }

    override suspend fun loadProduct(productId: Long): NetworkResult<Product> {
        return productDataSource.getProduct(productId)
    }
}
