package woowacourse.shopping.data.product

import woowacourse.shopping.data.entity.ProductEntity.Companion.toDomain
import woowacourse.shopping.domain.product.Product
import woowacourse.shopping.repository.ProductRepository

class DefaultProductRepository(private val dataSource: ProductDataSource) : ProductRepository {
    override fun findAll(limit: Int, offset: Int): Result<List<Product>> {
        return dataSource.findRanged(limit, offset).mapCatching { products ->
            products.map { it.toDomain() }
        }
    }

    override fun countAll(): Result<Int> {
        return dataSource.countAll()
    }

    override fun findById(id: Long): Result<Product> {
        return dataSource.findById(id).mapCatching { products ->
            products.toDomain()
        }
    }
}
