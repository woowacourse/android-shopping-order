package woowacourse.shopping.presentation.cart.order

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import woowacourse.shopping.presentation.cart.CartProductUi

@Parcelize
data class OrderNavArgs(
    val orderProducts: List<CartProductUi>,
) : Parcelable
