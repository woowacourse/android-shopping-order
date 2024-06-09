package woowacourse.shopping.data.remote.repository

import woowacourse.shopping.data.local.mapper.toDomain
import woowacourse.shopping.data.remote.datasource.product.DefaultProductDataSource
import woowacourse.shopping.data.remote.datasource.product.ProductDataSource
import woowacourse.shopping.data.remote.dto.mapper.toDomain
import woowacourse.shopping.data.remote.paging.LoadResult
import woowacourse.shopping.data.remote.paging.ProductPagingSource
import woowacourse.shopping.domain.CartProduct
import woowacourse.shopping.domain.ProductRepository

class ProductRepositoryImpl(
    private val productDataSource: ProductDataSource = DefaultProductDataSource(),
) : ProductRepository {
    private val productPagingSource = ProductPagingSource(productDataSource)

    override suspend fun getProducts(
        category: String,
        page: Int,
        size: Int,
    ): Result<List<CartProduct>> =
        runCatching {
            val response = productDataSource.getProducts(category, page, size)
            if (response.isSuccessful) {
                return Result.success(response.body()?.content?.map { it.toDomain() } ?: emptyList())
            }
            return Result.failure(Throwable(response.errorBody().toString()))
        }

    override suspend fun getProductsByPaging(
        offset: Int,
        pageSize: Int,
    ): Result<LoadResult.Page<CartProduct>> {
        return when (val data = productPagingSource.load(defaultOffset = offset, defaultPageSize = pageSize)) {
            is LoadResult.Page -> {
                Result.success(data)
            }
            is LoadResult.Error -> {
                Result.failure(Throwable(data.errorType.message))
            }
        }
    }

    override suspend fun getProductById(id: Int): Result<CartProduct?> =
        runCatching {
            val response = productDataSource.getProductById(id = id)
            if (response.isSuccessful) {
                return Result.success(response.body()?.toDomain())
            }
            return Result.failure(Throwable(response.errorBody().toString()))
        }
}
