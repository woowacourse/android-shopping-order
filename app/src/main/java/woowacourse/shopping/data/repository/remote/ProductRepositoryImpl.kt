package woowacourse.shopping.data.repository.remote

import woowacourse.shopping.data.datasource.remote.ProductRemoteDataSource
import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.repository.ProductRepository

class ProductRepositoryImpl(
    private val productRemoteDataSource: ProductRemoteDataSource,
) : ProductRepository {
    override suspend fun fetchPagingProducts(
        page: Int?,
        pageSize: Int?,
        category: String?,
    ): Result<List<Product>> {
        val result = productRemoteDataSource.fetchPagingProducts(page, pageSize, category)
        return result.fold(
            onSuccess = { products -> Result.success(products) },
            onFailure = { throwable -> Result.failure(throwable) },
        )
    }

    override suspend fun isLastPage(page: Int): Result<Boolean> {
        val result = productRemoteDataSource.isLastPage(page)
        return result.fold(
            onSuccess = { isLastPage -> Result.success(isLastPage) },
            onFailure = { throwable -> Result.failure(throwable) },
        )
    }

    override suspend fun fetchProductById(productId: Long): Result<Product> {
        val result = productRemoteDataSource.fetchProductById(productId)
        return result.fold(
            onSuccess = { product -> Result.success(product) },
            onFailure = { throwable -> Result.failure(throwable) },
        )
    }
}
