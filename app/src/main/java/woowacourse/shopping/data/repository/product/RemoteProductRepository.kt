package woowacourse.shopping.data.repository.product

import woowacourse.shopping.data.datasource.product.ProductDataSource
import woowacourse.shopping.data.mapper.toDomain
import woowacourse.shopping.domain.product.Product
import woowacourse.shopping.domain.product.Products
import woowacourse.shopping.domain.repository.ProductRepository

class RemoteProductRepository(
    private val productDataSource: ProductDataSource,
) : ProductRepository {
    override suspend fun getProducts(
        page: Int,
        pageSize: Int,
    ): Products {
        val result = productDataSource.getProducts(page, pageSize)
        return result.toDomain()
    }

    override suspend fun getProduct(id: Int): Product? = productDataSource.getProduct(id)?.toDomain()
}
