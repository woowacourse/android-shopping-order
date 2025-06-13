package woowacourse.shopping.data.repository

import woowacourse.shopping.data.datasource.product.ProductRemoteDataSource
import woowacourse.shopping.data.di.ApiResult
import woowacourse.shopping.data.mapper.toDomain
import woowacourse.shopping.domain.model.Page
import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.model.Products
import woowacourse.shopping.domain.repository.ProductRepository

class ProductRepositoryImpl(
    private val dataSource: ProductRemoteDataSource,
) : ProductRepository {
    override suspend fun fetchCatalogProduct(productId: Long): Result<Product?> =
        when (val result = dataSource.getProductDetail(productId)) {
            is ApiResult.Success ->
                runCatching {
                    val detail = result.data.toDomain()
                    Product(detail)
                }

            is ApiResult.ClientError -> Result.failure(Exception("Client error: ${result.code} ${result.message}"))
            is ApiResult.ServerError -> Result.failure(Exception("Server error: ${result.code} ${result.message}"))
            is ApiResult.NetworkError -> Result.failure(result.throwable)
            ApiResult.UnknownError -> Result.failure(Exception("Unknown error"))
        }

    override suspend fun fetchProducts(
        page: Int,
        size: Int,
        category: String?,
    ): Result<Products> =
        when (val result = dataSource.getProducts(category, page, size)) {
            is ApiResult.Success ->
                runCatching {
                    val response = result.data
                    val items = response.content.map { it.toDomain() }
                    val pageInfo = Page(page, response.first, response.last)
                    Products(items, pageInfo)
                }

            is ApiResult.ClientError -> Result.failure(Exception("Client error: ${result.code} ${result.message}"))
            is ApiResult.ServerError -> Result.failure(Exception("Server error: ${result.code} ${result.message}"))
            is ApiResult.NetworkError -> Result.failure(result.throwable)
            ApiResult.UnknownError -> Result.failure(Exception("Unknown error"))
        }

    override suspend fun fetchAllProducts(): Result<List<Product>> =
        when (val result = dataSource.getProducts(null, 0, Int.MAX_VALUE)) {
            is ApiResult.Success ->
                runCatching {
                    result.data.content.map { it.toDomain() }
                }

            is ApiResult.ClientError -> Result.failure(Exception("Client error: ${result.code} ${result.message}"))
            is ApiResult.ServerError -> Result.failure(Exception("Server error: ${result.code} ${result.message}"))
            is ApiResult.NetworkError -> Result.failure(result.throwable)
            ApiResult.UnknownError -> Result.failure(Exception("Unknown error"))
        }
}
