package woowacourse.shopping.data.repository

import woowacourse.shopping.data.datasource.product.ProductRemoteDataSource
import woowacourse.shopping.data.mapper.toDomain
import woowacourse.shopping.domain.Product
import woowacourse.shopping.domain.repository.ProductRepository

class ProductRepositoryImpl(
    private val remoteProductRemoteDataSource: ProductRemoteDataSource
) : ProductRepository {
    override fun getPartially(
        size: Int,
        lastId: Int,
        onReceived: (products: List<Product>) -> Unit
    ) {
        remoteProductRemoteDataSource.getPartially(size, lastId) { dataProducts ->
            onReceived(dataProducts.map { it.toDomain() })
        }
    }
}
