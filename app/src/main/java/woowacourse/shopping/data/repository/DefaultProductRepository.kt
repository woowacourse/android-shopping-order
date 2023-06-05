package woowacourse.shopping.data.repository

import woowacourse.shopping.data.entity.ProductEntity.Companion.toDomain
import woowacourse.shopping.data.datasource.ProductDataSource
import woowacourse.shopping.domain.product.Product
import woowacourse.shopping.repository.ProductRepository
import java.util.concurrent.CompletableFuture

class DefaultProductRepository(
    private val dataSource: ProductDataSource,
) : ProductRepository {
    override fun findAll(limit: Int, offset: Int): CompletableFuture<Result<List<Product>>> {
        return CompletableFuture.supplyAsync {
            dataSource.findRanged(limit, offset).mapCatching { products ->
                products.map { it.toDomain() }
            }
        }
    }

    override fun countAll(): CompletableFuture<Result<Int>> {
        return CompletableFuture.supplyAsync {
            dataSource.countAll()
        }
    }

    override fun findById(id: Long): CompletableFuture<Result<Product>> {
        return CompletableFuture.supplyAsync {
            dataSource.findById(id).mapCatching { products ->
                products.toDomain()
            }
        }
    }
}
