package woowacourse.shopping.model.fixture

import woowacourse.shopping.model.CartProduct
import woowacourse.shopping.model.fixture.ProductFixture.getProduct

object CartProductFixture {
    fun getCartProducts(quantity: Int, vararg ids: Long) = ids.map { getCartProduct(it, quantity) }

    fun getCartProduct(id: Long, quantity: Int = 1) =
        CartProduct(id, getProduct(id), quantity, true)
}
