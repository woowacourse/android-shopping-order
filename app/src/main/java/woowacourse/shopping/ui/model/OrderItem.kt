package woowacourse.shopping.ui.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

typealias UiOrderItem = OrderItem

@Parcelize
class OrderItem(
    val count: UiCount,
    val product: UiProduct
) : Parcelable
