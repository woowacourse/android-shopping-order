package woowacourse.shopping.domain.model

import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.model.Quantity

class CartWithProduct(
    val id: Long,
    val product: Product,
    val quantity: Quantity = Quantity(),
)
