package woowacourse.shopping.ui.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

typealias UiOrderItem = OrderItem

@Parcelize
data class OrderItem(
    val count: UiCount,
    val product: UiProduct
) : Parcelable
