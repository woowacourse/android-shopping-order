package woowacourse.shopping.data.repository.remote

import woowacourse.shopping.data.datasource.remote.ProductRemoteDataSource
import woowacourse.shopping.data.dto.product.toDomain
import woowacourse.shopping.data.repository.handleResult
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

    override suspend fun fetchProductById(productId: Long): Result<Product> {
        val result = productRemoteDataSource.fetchProductById(productId)
        return result.fold(
            onSuccess = { product -> Result.success(product) },
            onFailure = { throwable -> Result.failure(throwable) },
        )
    }
}

data class PageableItem<T>(
    val items: List<T>,
    val last: Boolean,
)
