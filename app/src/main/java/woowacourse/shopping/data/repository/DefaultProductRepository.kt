package woowacourse.shopping.data.repository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
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
    ): NetworkResult<ProductSinglePage> =
        withContext(Dispatchers.IO) {
            productDataSource.singlePage(category, page, pageSize)
        }

    override suspend fun loadProduct(productId: Long): NetworkResult<Product> =
        withContext(Dispatchers.IO) {
            productDataSource.getProduct(productId)
        }
}
