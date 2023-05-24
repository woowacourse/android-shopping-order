package woowacourse.shopping.domain.repository

import woowacourse.shopping.domain.BasketProduct
import woowacourse.shopping.domain.Product

interface BasketRepository {

    fun getAll(onReceived: (List<BasketProduct>) -> Unit)

    fun add(product: Product, onReceived: (Int) -> Unit)

    fun update(basketProduct: BasketProduct)

    fun remove(basketProduct: BasketProduct)
}
