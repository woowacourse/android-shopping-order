package woowacourse.shopping.domain.repository

import woowacourse.shopping.domain.BasketProduct

interface BasketRepository {

    fun getAll(onReceived: (List<BasketProduct>) -> Unit)

    fun add(basketProduct: BasketProduct)

    fun add2(basketProduct: BasketProduct)

    fun minus(basketProduct: BasketProduct)

    fun overWriteUpdate(basketProduct: BasketProduct)

    fun update(basketProduct: BasketProduct)

    fun remove(basketProduct: BasketProduct)
}
