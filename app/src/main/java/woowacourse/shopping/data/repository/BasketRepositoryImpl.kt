package woowacourse.shopping.data.repository

import woowacourse.shopping.data.datasource.basket.BasketDataSource
import woowacourse.shopping.data.mapper.toData
import woowacourse.shopping.data.mapper.toDomain
import woowacourse.shopping.domain.BasketProduct
import woowacourse.shopping.domain.Product
import woowacourse.shopping.domain.repository.BasketRepository

class BasketRepositoryImpl(
    private val remoteBasketDataSource: BasketDataSource.Remote
) :
    BasketRepository {

    override fun getAll(onReceived: (List<BasketProduct>) -> Unit) {
        remoteBasketDataSource.getAll { dataBasketProduct ->
            onReceived(dataBasketProduct.map { it.toDomain() })
        }
    }

    override fun add(product: Product, onReceived: (Int) -> Unit) {
        remoteBasketDataSource.add(product.toData(), onReceived)
    }

    override fun update(basketProduct: BasketProduct) {
        remoteBasketDataSource.update(basketProduct.toData())
    }

    override fun remove(basketProduct: BasketProduct) {
        remoteBasketDataSource.remove(basketProduct.toData())
    }
}
