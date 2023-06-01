package woowacourse.shopping.data.product

import woowacourse.shopping.data.entity.ProductEntity.Companion.toDomain
import woowacourse.shopping.domain.product.Product
import woowacourse.shopping.repository.ProductRepository

class DefaultProductRepository(private val dataSource: ProductDataSource) : ProductRepository {

    override fun findAll(limit: Int, offset: Int, onFinish: (Result<List<Product>>) -> Unit) {
        return dataSource.findRanged(limit, offset) { result ->
            onFinish(result.mapCatching { products -> products.map { it.toDomain() } })
        }
    }

    override fun countAll(onFinish: (Result<Int>) -> Unit) {
        return dataSource.countAll(onFinish)
    }

    override fun findById(id: Long, onFinish: (Result<Product>) -> Unit) {
        return dataSource.findById(id) { result ->
            onFinish(result.mapCatching { products -> products.toDomain() })
        }
    }
}
