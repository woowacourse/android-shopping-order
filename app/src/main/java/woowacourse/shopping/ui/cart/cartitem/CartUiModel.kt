package woowacourse.shopping.ui.cart.cartitem

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import woowacourse.shopping.domain.model.Quantity

@Parcelize
data class CartUiModel(
    val id: Long,
    val productId: Long,
    val name: String,
    val price: Int,
    val quantity: Int,
    val imageUrl: String,
    val isChecked: Boolean = false,
    val isLoading: Boolean = true,
):Parcelable {
    val totalPrice = price * quantity
}
