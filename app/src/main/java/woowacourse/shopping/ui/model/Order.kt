package woowacourse.shopping.ui.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.time.LocalDateTime

typealias UiOrder = Order

@Parcelize
class Order(
    val orderId: Int,
    val createdAt: LocalDateTime,
    val orderItems: List<UiOrderItem>,
    val totalPrice: UiPrice,
    val usedPoint: UiPoint,
    val earnedPoint: UiPoint
) : Parcelable
