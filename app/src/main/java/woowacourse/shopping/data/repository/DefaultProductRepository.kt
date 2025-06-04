package woowacourse.shopping.data.repository

import woowacourse.shopping.data.datasource.ProductsDataSource
import woowacourse.shopping.domain.exception.NetworkResult
import woowacourse.shopping.domain.product.Product
import woowacourse.shopping.domain.product.ProductSinglePage
import woowacourse.shopping.domain.repository.ProductRepository

class DefaultProductRepository(
    private val productDataSource: ProductsDataSource,
) : ProductRepository {
    override suspend fun loadSinglePage(
        category: String?,
        page: Int?,
        pageSize: Int?,
    ): NetworkResult<ProductSinglePage> {
        return productDataSource.singlePage(category, page, pageSize)
    }

    override suspend fun loadProduct(productId: Long): NetworkResult<Product> {
        return productDataSource.getProduct(productId)
    }
}
