package woowacourse.shopping.data.repository

import woowacourse.shopping.data.datasource.product.ProductDataSource
import woowacourse.shopping.data.mapper.toDomain
import woowacourse.shopping.domain.Product
import woowacourse.shopping.domain.repository.ProductRepository

class ProductRepositoryImpl(
    private val remoteProductDataSource: ProductDataSource.Remote
) : ProductRepository {
    override fun getPartially(
        size: Int,
        lastId: Int,
        onReceived: (products: List<Product>) -> Unit
    ) {
        remoteProductDataSource.getPartially(size, lastId) { dataProducts ->
            onReceived(dataProducts.map { it.toDomain() })
        }
    }
}
