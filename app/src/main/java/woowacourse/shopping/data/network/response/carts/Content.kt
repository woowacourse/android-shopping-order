package woowacourse.shopping.data.network.response.carts

import kotlinx.serialization.Serializable
import woowacourse.shopping.domain.Quantity
import woowacourse.shopping.domain.cart.ShoppingCart

@Serializable
data class Content(
    val id: Long,
    val product: Product,
    val quantity: Int,
) {
    fun toDomain(): ShoppingCart {
        return ShoppingCart(
            id,
            product.toDomain(),
            Quantity(quantity),
        )
    }
}
