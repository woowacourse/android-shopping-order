package woowacourse.shopping.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Cart(
    val id: Int = DEFAULT_CART_ID,
    val quantity: Int = INIT_QUANTITY_NUM,
    val product: Product,
) : Parcelable {
    val totalPrice: Int
        get() = product.price * quantity

    companion object {
        const val DEFAULT_CART_ID = -1
        const val INIT_QUANTITY_NUM = 0
    }
}
