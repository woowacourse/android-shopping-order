package woowacourse.shopping.data.datasource.basket

import woowacourse.shopping.data.datasource.response.BasketProductEntity
import woowacourse.shopping.data.datasource.response.ProductEntity

interface BasketRemoteDataSource {

    fun getAll(): Result<List<BasketProductEntity>>

    fun add(product: ProductEntity): Result<Int>

    fun update(basketProduct: BasketProductEntity): Result<Unit>

    fun remove(basketProduct: BasketProductEntity): Result<Unit>
}
