package woowacourse.shopping.data.repository

import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import woowacourse.shopping.data.source.remote.datasource.ProductRemoteDataSource
import woowacourse.shopping.data.source.remote.dto.product.mapper.toDomain
import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.repository.ProductRepository
import kotlin.collections.map

class DefaultProductRepository(
    private val remoteDataSource: ProductRemoteDataSource,
) : ProductRepository {
    override suspend fun getProducts(
        offset: Int,
        limit: Int,
        category: String?,
    ): ImmutableList<Product> =
        remoteDataSource
            .fetchProducts(page = offset, size = limit, category = category)
            .content
            .map { it.toDomain() }
            .toImmutableList()

    override suspend fun getProductById(id: Long): Product = remoteDataSource.fetchProductById(id).toDomain()
}
