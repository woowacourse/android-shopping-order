package woowacourse.shopping.data.repository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import woowacourse.shopping.data.datasource.ProductsDataSource
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
    ): Result<ProductSinglePage> = withContext(Dispatchers.IO) {
        runCatching {
            val response = productDataSource.singlePage(category, page, pageSize)
            response.toDomain()
        }
    }

    override suspend fun loadProduct(productId: Long): Result<Product> = withContext(Dispatchers.IO) {
        runCatching {
            val response = productDataSource.getProduct(productId)
            response.toDomain()
        }
    }
}
