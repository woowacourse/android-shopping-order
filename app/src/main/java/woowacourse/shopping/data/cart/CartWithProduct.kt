package woowacourse.shopping.data.cart

import woowacourse.shopping.model.Product
import woowacourse.shopping.model.Quantity


class CartWithProduct(
    val id: Long,
    val product: Product,
    val quantity: Quantity = Quantity()
)
