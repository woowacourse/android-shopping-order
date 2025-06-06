package woowacourse.shopping.domain.cart

import woowacourse.shopping.domain.Quantity
import woowacourse.shopping.domain.product.Product
import java.io.Serializable

data class ShoppingCart(
    val id: Long,
    val product: Product,
    val quantity: Quantity,
) : Serializable {
    val productId: Long
        get() = product.id

    val imgUrl: String
        get() = product.imgUrl

    fun increasedQuantity(): ShoppingCart {
        return copy(quantity = quantity + 1)
    }

    fun withDecreasedQuantityOrMin(minValue: Int): ShoppingCart {
        val decreased = quantity - 1
        return if (decreased.hasQuantity(minValue)) {
            copy(quantity = decreased)
        } else {
            copy(quantity = Quantity(minValue))
        }
    }
}
