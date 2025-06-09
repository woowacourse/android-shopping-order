package woowacourse.shopping.data.repository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import woowacourse.shopping.data.datasource.remote.RemoteProductsDataSource
import woowacourse.shopping.domain.product.Product
import woowacourse.shopping.domain.product.ProductSinglePage
import woowacourse.shopping.domain.repository.ProductRepository

class DefaultProductRepository(
    private val remoteProductDataSource: RemoteProductsDataSource,
) : ProductRepository {
    override suspend fun loadSinglePage(
        category: String?,
        page: Int?,
        pageSize: Int?,
    ): Result<ProductSinglePage> =
        withContext(Dispatchers.IO) {
            remoteProductDataSource.singlePage(category, page, pageSize)
        }

    override suspend fun loadProduct(productId: Long): Result<Product> =
        withContext(Dispatchers.IO) {
            remoteProductDataSource.getProduct(productId)
        }
}
