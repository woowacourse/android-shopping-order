package woowacourse.shopping.domain.model

import java.io.Serializable

data class Cart(
    val id: Int = EMPTY_CART_ID,
    val quantity: Int = INIT_QUANTITY_NUM,
    val product: Product,
) : Serializable {
    val totalPrice: Int
        get() = product.price * quantity

    companion object {
        const val EMPTY_CART_ID = -1
        const val INIT_QUANTITY_NUM = 0
    }
}
