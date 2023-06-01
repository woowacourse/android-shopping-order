package woowacourse.shopping.model

import android.os.Parcelable
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize
import woowacourse.shopping.ui.order.recyclerview.ListItem
import woowacourse.shopping.ui.orderdetail.recyclerview.OrderDetailViewType

@Parcelize
data class PaymentModel(
    val originalPayment: PriceModel,
    val finalPayment: PriceModel,
    val usedPoint: PointModel,
) : Parcelable, ListItem {
    @IgnoredOnParcel
    override val viewType: Int = OrderDetailViewType.PAYMENT.value
}
