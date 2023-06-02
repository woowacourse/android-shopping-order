package woowacourse.shopping.data.repository

import woowacourse.shopping.data.datasource.basket.BasketRemoteDataSource
import woowacourse.shopping.data.mapper.toBasketProductDomainModel
import woowacourse.shopping.data.mapper.toBasketProductEntity
import woowacourse.shopping.data.mapper.toProductEntity
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
                onReceived(dataBasketProduct.map { it.toBasketProductDomainModel() })
            },
            onFailed = { errorMessage ->
                onFailed(errorMessage)
            }
        )
    }

    override fun add(product: Product, onReceived: (Int) -> Unit) {
        basketRemoteDataSource.add(product.toProductEntity(), onReceived)
    }

    override fun update(basketProduct: BasketProduct) {
        basketRemoteDataSource.update(basketProduct.toBasketProductEntity())
    }

    override fun remove(basketProduct: BasketProduct) {
        basketRemoteDataSource.remove(basketProduct.toBasketProductEntity())
    }
}
