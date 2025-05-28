package woowacourse.shopping.data.network.request

import kotlinx.serialization.Serializable
import woowacourse.shopping.domain.cart.Cart

@Serializable
data class CartItemRequest(
    val productId: Long,
    val quantity: Int,
)

fun Cart.toRequest() =
    CartItemRequest(
        productId = productId,
        quantity = quantity.value,
    )
