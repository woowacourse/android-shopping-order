package woowacourse.shopping.ui.model

import woowacourse.shopping.domain.model.OrderItem
import woowacourse.shopping.domain.model.Product

data class CartItem(
    val id: Long,
    val quantity: Int,
    val product: Product,
    val checked: Boolean,
) {
    fun price(): Int = quantity * product.price

    companion object {
        const val CHANGE_AMOUNT_PER_EACH = 1
    }
}

fun CartItem.toOrderItem(): OrderItem {
    return OrderItem(
        cartItemId = id,
        quantity = quantity,
        product = product,
    )
}

