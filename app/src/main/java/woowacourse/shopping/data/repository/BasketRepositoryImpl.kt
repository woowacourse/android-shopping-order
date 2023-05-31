package woowacourse.shopping.data.repository

import woowacourse.shopping.data.datasource.basket.BasketRemoteDataSource
import woowacourse.shopping.data.mapper.toData
import woowacourse.shopping.data.mapper.toDomain
import woowacourse.shopping.domain.BasketProduct
import woowacourse.shopping.domain.Product
import woowacourse.shopping.domain.repository.BasketRepository

class BasketRepositoryImpl(
    private val basketRemoteDataSource: BasketRemoteDataSource
) : BasketRepository {

    override fun getAll(onReceived: (List<BasketProduct>) -> Unit) {
        basketRemoteDataSource.getAll { dataBasketProduct ->
            onReceived(dataBasketProduct.map { it.toDomain() })
        }
    }

    override fun add(product: Product, onReceived: (Int) -> Unit) {
        basketRemoteDataSource.add(product.toData(), onReceived)
    }

    override fun update(basketProduct: BasketProduct) {
        basketRemoteDataSource.update(basketProduct.toData())
    }

    override fun remove(basketProduct: BasketProduct) {
        basketRemoteDataSource.remove(basketProduct.toData())
    }
}
