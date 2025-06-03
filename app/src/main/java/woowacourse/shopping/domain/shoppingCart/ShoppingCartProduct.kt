package woowacourse.shopping.domain.shoppingCart

import woowacourse.shopping.domain.product.Product
import java.io.Serializable

data class ShoppingCartProduct(
    val id: Long,
    val product: Product,
    val quantity: Int,
) : Serializable {
    val price: Int get() = product.price * quantity
}
