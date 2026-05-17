package woowacourse.shopping.data.repository.product

import woowacourse.shopping.data.datasource.product.ProductRemoteDataSource
import woowacourse.shopping.data.mapper.toDomain
import woowacourse.shopping.domain.model.product.Product
import woowacourse.shopping.domain.model.product.Products
import woowacourse.shopping.domain.repository.ProductRepository

class RemoteProductRepository(
    private val productRemoteDataSource: ProductRemoteDataSource,
) : ProductRepository {
    override suspend fun getProducts(
        page: Int,
        pageSize: Int,
    ): Products {
        val result = productRemoteDataSource.getProducts(page, pageSize)
        return result.toDomain()
    }

    override suspend fun getProduct(id: Int): Product? = productRemoteDataSource.getProduct(id)?.toDomain()
}
