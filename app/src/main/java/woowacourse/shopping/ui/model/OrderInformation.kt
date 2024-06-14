package woowacourse.shopping.ui.model

import woowacourse.shopping.domain.model.CartItem
import java.io.Serializable

data class OrderInformation(
    val cartItems: List<CartItem>,
) : Serializable {
    fun getCartItemIds(): List<Long> = cartItems.map { it.id }

    fun countProducts(): Int = cartItems.sumOf { it.quantity }

    fun calculateOrderAmount(): Int = cartItems.sumOf { it.product.price * it.quantity }

    fun calculateDefaultTotalAmount(): Int = cartItems.sumOf { it.product.price * it.quantity } + SHIPPING_FEE

    fun determineShippingFee(isSelected: Boolean): Int = if (isSelected) FREE_SHIPPING_FEE else SHIPPING_FEE

    companion object {
        const val FREE_SHIPPING_FEE = 0
        const val SHIPPING_FEE = 3_000
    }
}
