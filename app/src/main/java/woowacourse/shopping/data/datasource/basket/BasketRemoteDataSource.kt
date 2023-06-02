package woowacourse.shopping.data.datasource.basket

import woowacourse.shopping.data.model.BasketProductEntity
import woowacourse.shopping.data.model.ProductEntity

interface BasketRemoteDataSource {

    fun getAll(
        onReceived: (List<BasketProductEntity>) -> Unit,
        onFailed: (errorMessage: String) -> Unit,
    )

    fun add(
        product: ProductEntity,
        onAdded: (Int) -> Unit,
        onFailed: (errorMessage: String) -> Unit,
    )

    fun update(basketProduct: BasketProductEntity)

    fun remove(basketProduct: BasketProductEntity)
}
