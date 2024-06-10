package woowacourse.shopping.domain.model.cart

import woowacourse.shopping.domain.model.product.Product
import woowacourse.shopping.domain.model.product.Quantity

class CartWithProduct(
    val id: Long,
    val product: Product,
    val quantity: Quantity = Quantity(),
)
