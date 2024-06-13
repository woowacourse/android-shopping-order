package woowacourse.shopping.domain.model

import java.io.Serializable

data class CartItem(
    val id: Long,
    val productId: Long,
    val productName: String,
    val price: Long,
    val imgUrl: String,
    val quantity: Int,
    var isChecked: Boolean = false,
) : Serializable {
    val totalPrice: Long
        get() = price * quantity
}
