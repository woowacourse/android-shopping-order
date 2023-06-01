package woowacourse.shopping.model

import android.os.Parcelable
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize
import woowacourse.shopping.ui.order.recyclerview.ListItem
import woowacourse.shopping.ui.orderdetail.recyclerview.OrderDetailViewType

typealias UiPayment = Payment

@Parcelize
data class Payment(
    val originalPayment: UiPrice,
    val finalPayment: UiPrice,
    val usedPoint: UiPoint,
) : Parcelable, ListItem {
    @IgnoredOnParcel
    override val viewType: Int = OrderDetailViewType.PAYMENT.value
}
