package woowacourse.shopping.presentation.cart

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import woowacourse.shopping.presentation.shopping.detail.ProductUi

@Parcelize
data class CartProductUi(
    val id: Long = 0,
    val product: ProductUi,
    val count: Int = 0,
    val isSelected: Boolean = true,
) : Parcelable {
    val isVisible: Boolean
        get() = count > 0
    val totalPrice: Int
        get() = product.price * count
}