package woowacourse.shopping.data.repository

import woowacourse.shopping.data.source.remote.datasource.ProductRemoteDataSource
import woowacourse.shopping.data.source.remote.dto.product.mapper.toDomain
import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.model.ProductsPage
import woowacourse.shopping.domain.repository.ProductRepository

class DefaultProductRepository(
    private val remoteDataSource: ProductRemoteDataSource,
) : ProductRepository {
    override suspend fun getProducts(
        page: Int,
        limit: Int,
        category: String?,
    ): ProductsPage =
        remoteDataSource
            .fetchProducts(page = page, size = limit, category = category)
            .toDomain()

    override suspend fun getProductById(id: Long): Product = remoteDataSource.fetchProductById(id).toDomain()
}
