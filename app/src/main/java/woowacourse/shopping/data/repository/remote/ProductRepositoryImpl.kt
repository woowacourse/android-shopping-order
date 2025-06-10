package woowacourse.shopping.data.repository.remote

import woowacourse.shopping.data.datasource.remote.ProductRemoteDataSource
import woowacourse.shopping.data.dto.product.toDomain
import woowacourse.shopping.data.handleResult
import woowacourse.shopping.domain.model.PageableItem
import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.repository.ProductRepository

class ProductRepositoryImpl(
    private val productRemoteDataSource: ProductRemoteDataSource,
) : ProductRepository {
    override suspend fun fetchPagingProducts(
        page: Int?,
        pageSize: Int?,
        category: String?,
    ): Result<PageableItem<Product>> =
        handleResult {
            val response =
                productRemoteDataSource.fetchPagingProducts(page, pageSize, category).getOrThrow()
            val products = response.content.map { productContent -> productContent.toDomain() }
            val last = response.last

            PageableItem(products, last)
        }

    override suspend fun fetchProductById(productId: Long): Result<Product> =
        handleResult {
            val response = productRemoteDataSource.fetchProductById(productId).getOrThrow()
            response.toDomain()
        }
}
