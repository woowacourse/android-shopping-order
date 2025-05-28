package woowacourse.shopping.feature

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import woowacourse.shopping.domain.model.CartItem

@Parcelize
data class CartUiModel(
    val cartId : Int,
    val cartQuantity: Int,
) : Parcelable

fun CartItem.toUiModel() =
    CartUiModel(this.id,this.quantity)