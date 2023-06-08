package woowacourse.shopping.model.fixture

import woowacourse.shopping.model.Price
import woowacourse.shopping.model.Product

object ProductFixture {
    fun getProducts(vararg ids: Long) = ids.map { getProduct(it) }

    fun getProduct(id: Long) = Product(id, "text.com", "test", Price(10000))
}
