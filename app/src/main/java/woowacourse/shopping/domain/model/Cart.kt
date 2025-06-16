package woowacourse.shopping.domain.model

import java.io.Serializable

data class Cart(
    val id: Long,
    val product: Product,
    var quantity: Int,
) : Serializable {
    val totalPrice: Int get() = product.price * quantity
}
