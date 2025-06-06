package woowacourse.shopping.data.repository

import woowacourse.shopping.data.datasource.remote.ProductRemoteDataSource
import woowacourse.shopping.data.dto.response.toProduct
import woowacourse.shopping.data.model.PagedResult
import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.repository.ProductRepository

class ProductRepositoryImpl(
    private val remoteDataSource: ProductRemoteDataSource,
) : ProductRepository {
    override suspend fun getPagedProducts(
        page: Int?,
        size: Int?,
    ): Result<PagedResult<Product>> =
        remoteDataSource
            .getPagedProducts(page, size)
            .mapCatching { dto ->
                val products = dto.content.map { it.toProduct() }
                val hasNext = dto.last.not()
                PagedResult(products, hasNext)
            }
}
