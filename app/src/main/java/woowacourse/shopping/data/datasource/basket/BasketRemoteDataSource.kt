package woowacourse.shopping.data.datasource.basket

import woowacourse.shopping.data.datasource.response.BasketProductEntity
import woowacourse.shopping.data.datasource.response.ProductEntity

interface BasketRemoteDataSource {

    fun getAll(): Result<List<BasketProductEntity>>

    fun add(
        product: ProductEntity,
        onAdded: (Int) -> Unit,
        onFailed: (errorMessage: String) -> Unit,
    )

    fun update(
        basketProduct: BasketProductEntity,
        onUpdated: () -> Unit,
        onFailed: (errorMessage: String) -> Unit,
    )

    fun remove(
        basketProduct: BasketProductEntity,
        onRemoved: () -> Unit,
        onFailed: (errorMessage: String) -> Unit,
    )
}
