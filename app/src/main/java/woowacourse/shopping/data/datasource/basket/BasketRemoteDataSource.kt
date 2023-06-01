package woowacourse.shopping.data.datasource.basket

import woowacourse.shopping.data.model.BasketProductEntity
import woowacourse.shopping.data.model.ProductEntity

interface BasketRemoteDataSource {

    fun getAll(onReceived: (List<BasketProductEntity>) -> Unit)

    fun add(product: ProductEntity, onReceived: (Int) -> Unit)

    fun update(basketProduct: BasketProductEntity)

    fun remove(basketProduct: BasketProductEntity)
}
