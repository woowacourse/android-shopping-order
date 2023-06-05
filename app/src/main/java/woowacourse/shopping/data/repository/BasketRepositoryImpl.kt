package woowacourse.shopping.data.repository

import woowacourse.shopping.data.datasource.basket.BasketRemoteDataSource
import woowacourse.shopping.data.mapper.toDomainModel
import woowacourse.shopping.data.mapper.toEntity
import woowacourse.shopping.domain.BasketProduct
import woowacourse.shopping.domain.Product
import woowacourse.shopping.domain.repository.BasketRepository

class BasketRepositoryImpl(
    private val basketRemoteDataSource: BasketRemoteDataSource,
) : BasketRepository {

    override fun getAll(
        onReceived: (List<BasketProduct>) -> Unit,
        onFailed: (errorMessage: String) -> Unit,
    ) {
        basketRemoteDataSource.getAll(
            onReceived = { dataBasketProduct ->
                onReceived(dataBasketProduct.map { it.toDomainModel() })
            },
            onFailed = { errorMessage ->
                onFailed(errorMessage)
            }
        )
    }

    override fun add(
        product: Product,
        onAdded: (Int) -> Unit,
        onFailed: (errorMessage: String) -> Unit,
    ) {
        basketRemoteDataSource.add(
            product = product.toEntity(),
            onAdded = onAdded,
            onFailed = onFailed
        )
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
