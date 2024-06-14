package woowacourse.shopping.data.remote.dto.request

import kotlinx.serialization.Serializable
import woowacourse.shopping.domain.CartProduct

@Serializable
data class CartItemRequest(
    val productId: Int,
    val quantity: Int,
) {
    companion object {
        fun fromCartProduct(cartProduct: CartProduct): CartItemRequest {
            return CartItemRequest(
                productId = cartProduct.productId.toInt(),
                quantity = cartProduct.quantity,
            )
        }
    }
}
