package woowacourse.shopping.data.repository

import woowacourse.shopping.data.datasource.product.ProductRemoteDataSource
import woowacourse.shopping.data.mapper.toDomainModel
import woowacourse.shopping.domain.Product
import woowacourse.shopping.domain.repository.ProductRepository
import java.util.concurrent.CompletableFuture

class ProductRepositoryImpl(
    private val remoteProductRemoteDataSource: ProductRemoteDataSource,
) : ProductRepository {
    override fun getPartially(
        size: Int,
        lastId: Int,
    ): CompletableFuture<Result<List<Product>>> {
        return CompletableFuture.supplyAsync {
            remoteProductRemoteDataSource.getPartially(size, lastId).mapCatching { products ->
                products.map { it.toDomainModel() }
            }
        }
    }
}
