package woowacourse.shopping.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

typealias UiOrder = Order

@Parcelize
class Order(
    val orderProducts: List<OrderProduct>,
    val totalPayment: Price,
) : Parcelable
