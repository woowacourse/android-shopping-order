package woowacourse.shopping.data.remote.repository

import woowacourse.shopping.data.remote.datasource.product.RetrofitProductDataSource
import woowacourse.shopping.data.remote.datasource.product.ProductDataSource
import woowacourse.shopping.data.remote.dto.mapper.toDomain
import woowacourse.shopping.data.remote.paging.LoadResult
import woowacourse.shopping.data.remote.paging.ProductPagingSource
import woowacourse.shopping.domain.CartProduct
import woowacourse.shopping.domain.repository.ProductRepository

class ProductRepositoryImpl(
    private val productDataSource: ProductDataSource = RetrofitProductDataSource(),
) : ProductRepository {
    private val productPagingSource = ProductPagingSource(productDataSource)

    override suspend fun getAllByPagingAndCategory(
        category: String,
        page: Int,
        size: Int,
    ): Result<List<CartProduct>> {
        return productDataSource.getAllByPaging(category, page, size).mapCatching {
            it.body?.content?.map { it.toDomain() } ?: emptyList()
        }
    }

    override suspend fun getAllByPaging(
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

    override suspend fun getById(id: Int): Result<CartProduct?> {
        return productDataSource.getById(id).mapCatching {
            it.body?.toDomain()
        }
    }
}
