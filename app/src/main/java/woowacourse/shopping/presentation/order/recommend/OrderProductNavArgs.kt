package woowacourse.shopping.presentation.order.recommend

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import woowacourse.shopping.presentation.cart.CartProductUi

@Parcelize
data class OrderProductNavArgs(
    val orderProducts: List<CartProductUi>,
) : Parcelable
