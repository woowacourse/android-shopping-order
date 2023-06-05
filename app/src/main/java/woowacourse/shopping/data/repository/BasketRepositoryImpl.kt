package woowacourse.shopping.data.repository

import woowacourse.shopping.data.datasource.basket.BasketRemoteDataSource
import woowacourse.shopping.data.mapper.toDomainModel
import woowacourse.shopping.data.mapper.toEntity
import woowacourse.shopping.domain.BasketProduct
import woowacourse.shopping.domain.Product
import woowacourse.shopping.domain.repository.BasketRepository
import java.util.concurrent.CompletableFuture

class BasketRepositoryImpl(
    private val basketRemoteDataSource: BasketRemoteDataSource,
) : BasketRepository {

    override fun getAll(): CompletableFuture<Result<List<BasketProduct>>> {
        return CompletableFuture.supplyAsync {
            basketRemoteDataSource.getAll().mapCatching { basketProducts ->
                basketProducts.map { it.toDomainModel() }
            }
        }
    }

    override fun add(
        product: Product,
    ): CompletableFuture<Result<Int>> {
        return CompletableFuture.supplyAsync {
            basketRemoteDataSource.add(product.toEntity())
        }
    }

    override fun update(
        basketProduct: BasketProduct,
        onUpdated: () -> Unit,
        onFailed: (errorMessage: String) -> Unit,
    ) {
        basketRemoteDataSource.update(
            basketProduct = basketProduct.toEntity(),
            onUpdated = onUpdated,
            onFailed = onFailed
        )
    }

    override fun remove(
        basketProduct: BasketProduct,
        onRemoved: () -> Unit,
        onFailed: (errorMessage: String) -> Unit,
    ) {
        basketRemoteDataSource.remove(
            basketProduct = basketProduct.toEntity(),
            onRemoved = onRemoved,
            onFailed = { errorMessage ->
                onFailed(errorMessage)
            }
        )
    }
}
