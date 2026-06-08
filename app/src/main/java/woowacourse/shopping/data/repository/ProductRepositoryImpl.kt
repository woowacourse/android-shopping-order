package woowacourse.shopping.data.repository

import woowacourse.shopping.data.datasource.product.ProductRemoteDataSource
import woowacourse.shopping.data.mapper.toDomain
import woowacourse.shopping.domain.product.Product
import woowacourse.shopping.domain.product.Products
import woowacourse.shopping.domain.repository.ProductRepository

class ProductRepositoryImpl(
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
